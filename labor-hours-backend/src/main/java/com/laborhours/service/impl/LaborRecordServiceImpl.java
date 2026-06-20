package com.laborhours.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.laborhours.dto.LaborReportDTO;
import com.laborhours.entity.LaborRecord;
import com.laborhours.entity.ProdWorkOrder;
import com.laborhours.mapper.LaborRecordMapper;
import com.laborhours.service.LaborRecordService;
import com.laborhours.service.ProdWorkOrderService;
import com.laborhours.service.RedisLaborService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Service
public class LaborRecordServiceImpl extends ServiceImpl<LaborRecordMapper, LaborRecord> implements LaborRecordService {

    @Autowired
    private ProdWorkOrderService prodWorkOrderService;

    @Autowired
    private RedisLaborService redisLaborService;

    private static final Random RANDOM = new Random();

    @Override
    public List<LaborRecord> listAll() {
        return list();
    }

    @Override
    public IPage<LaborRecord> pageList(Integer pageNum, Integer pageSize, LaborRecord record) {
        LambdaQueryWrapper<LaborRecord> wrapper = new LambdaQueryWrapper<>();
        if (record != null) {
            if (record.getRecordNo() != null && !record.getRecordNo().isEmpty()) {
                wrapper.like(LaborRecord::getRecordNo, record.getRecordNo());
            }
            if (record.getWorkerId() != null) {
                wrapper.eq(LaborRecord::getWorkerId, record.getWorkerId());
            }
            if (record.getWorkOrderId() != null) {
                wrapper.eq(LaborRecord::getWorkOrderId, record.getWorkOrderId());
            }
            if (record.getOrderId() != null) {
                wrapper.eq(LaborRecord::getOrderId, record.getOrderId());
            }
            if (record.getProcessId() != null) {
                wrapper.eq(LaborRecord::getProcessId, record.getProcessId());
            }
            if (record.getLaborType() != null) {
                wrapper.eq(LaborRecord::getLaborType, record.getLaborType());
            }
            if (record.getStatus() != null) {
                wrapper.eq(LaborRecord::getStatus, record.getStatus());
            }
        }
        wrapper.orderByDesc(LaborRecord::getCreateTime);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public LaborRecord getById(Long id) {
        return super.getById(id);
    }

    @Override
    public boolean add(LaborRecord record) {
        return save(record);
    }

    @Override
    public boolean update(LaborRecord record) {
        return updateById(record);
    }

    @Override
    public boolean delete(Long id) {
        LaborRecord record = getById(id);
        if (record != null) {
            boolean result = removeById(id);
            if (result) {
                redisLaborService.deleteTodayLaborCache(record.getWorkerId());
            }
            return result;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LaborRecord reportLabor(LaborReportDTO dto) {
        validateReportDTO(dto);

        BigDecimal workHours = calculateWorkHours(dto);
        BigDecimal overtimeHours = BigDecimal.ZERO;
        if (dto.getIsOvertime() != null && dto.getIsOvertime() == 1) {
            overtimeHours = workHours;
        }

        LaborRecord record = new LaborRecord();
        record.setRecordNo(generateRecordNo());
        record.setWorkerId(dto.getWorkerId());
        record.setWorkOrderId(dto.getWorkOrderId());
        record.setLaborType(dto.getLaborType());
        record.setReportType(dto.getReportType());
        record.setStartTime(dto.getStartTime());
        record.setEndTime(dto.getEndTime());
        record.setWorkHours(workHours);
        record.setCompletedQty(dto.getCompletedQty());
        record.setIsOvertime(dto.getIsOvertime() != null ? dto.getIsOvertime() : 0);
        record.setStatus(1);
        record.setRemark(dto.getRemark());
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());

        if (dto.getWorkOrderId() != null) {
            ProdWorkOrder workOrder = prodWorkOrderService.getById(dto.getWorkOrderId());
            if (workOrder != null) {
                record.setOrderId(workOrder.getOrderId());
                record.setProcessId(workOrder.getProcessId());
            }
        }

        save(record);

        redisLaborService.addTodayLaborHours(dto.getWorkerId(), dto.getLaborType(), workHours, overtimeHours);

        return record;
    }

    @Override
    public boolean checkTimeOverlap(Long workerId, LocalDateTime startTime, LocalDateTime endTime, Long excludeId) {
        if (startTime == null || endTime == null) {
            return false;
        }

        LambdaQueryWrapper<LaborRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LaborRecord::getWorkerId, workerId);
        wrapper.eq(LaborRecord::getStatus, 1);
        wrapper.lt(LaborRecord::getStartTime, endTime);
        wrapper.gt(LaborRecord::getEndTime, startTime);

        if (excludeId != null) {
            wrapper.ne(LaborRecord::getId, excludeId);
        }

        Long count = count(wrapper);
        return count != null && count > 0;
    }

    @Override
    public List<LaborRecord> getTodayRecords(Long workerId) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);

        LambdaQueryWrapper<LaborRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LaborRecord::getWorkerId, workerId);
        wrapper.eq(LaborRecord::getStatus, 1);
        wrapper.between(LaborRecord::getStartTime, startOfDay, endOfDay);
        wrapper.orderByDesc(LaborRecord::getCreateTime);

        return list(wrapper);
    }

    private void validateReportDTO(LaborReportDTO dto) {
        if (dto.getWorkerId() == null) {
            throw new IllegalArgumentException("工人ID不能为空");
        }
        if (dto.getLaborType() == null) {
            throw new IllegalArgumentException("工时类型不能为空");
        }
        if (dto.getReportType() == null) {
            throw new IllegalArgumentException("报工类型不能为空");
        }
        if (dto.getStartTime() == null) {
            throw new IllegalArgumentException("开始时间不能为空");
        }

        if (dto.getReportType() == 1) {
            if (dto.getEndTime() == null) {
                throw new IllegalArgumentException("按时长报工时结束时间不能为空");
            }
            if (dto.getEndTime().isBefore(dto.getStartTime())) {
                throw new IllegalArgumentException("结束时间不能早于开始时间");
            }
            if (checkTimeOverlap(dto.getWorkerId(), dto.getStartTime(), dto.getEndTime(), null)) {
                throw new IllegalArgumentException("该时间段已有报工记录");
            }
        } else if (dto.getReportType() == 2) {
            if (dto.getCompletedQty() == null || dto.getCompletedQty() <= 0) {
                throw new IllegalArgumentException("按数量报工时完成数量不能为空且必须大于0");
            }
        }
    }

    private BigDecimal calculateWorkHours(LaborReportDTO dto) {
        if (dto.getReportType() == 1 && dto.getStartTime() != null && dto.getEndTime() != null) {
            Duration duration = Duration.between(dto.getStartTime(), dto.getEndTime());
            long minutes = duration.toMinutes();
            return BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 2, BigDecimal.ROUND_HALF_UP);
        }
        if (dto.getWorkHours() != null) {
            return dto.getWorkHours();
        }
        return BigDecimal.ZERO;
    }

    private String generateRecordNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int randomNum = RANDOM.nextInt(900) + 100;
        return "R" + timestamp + randomNum;
    }
}
