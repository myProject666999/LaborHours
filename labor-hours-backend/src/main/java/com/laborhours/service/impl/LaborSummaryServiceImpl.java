package com.laborhours.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.laborhours.dto.LaborSummaryDTO;
import com.laborhours.entity.LaborDailySummary;
import com.laborhours.entity.LaborRecord;
import com.laborhours.entity.ProdOrder;
import com.laborhours.entity.ProdProcess;
import com.laborhours.entity.ProdWorkOrder;
import com.laborhours.entity.SysTeam;
import com.laborhours.entity.SysWorker;
import com.laborhours.mapper.LaborDailySummaryMapper;
import com.laborhours.service.LaborRecordService;
import com.laborhours.service.LaborSummaryService;
import com.laborhours.service.ProdOrderService;
import com.laborhours.service.ProdProcessService;
import com.laborhours.service.ProdWorkOrderService;
import com.laborhours.service.RedisLaborService;
import com.laborhours.service.SysTeamService;
import com.laborhours.service.SysWorkerService;
import com.laborhours.vo.OrderLaborSummaryVO;
import com.laborhours.vo.ProcessLaborSummaryVO;
import com.laborhours.vo.WorkOrderCompareVO;
import com.laborhours.vo.WorkerLaborSummaryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LaborSummaryServiceImpl extends ServiceImpl<LaborDailySummaryMapper, LaborDailySummary> implements LaborSummaryService {

    @Autowired
    private LaborRecordService laborRecordService;

    @Autowired
    private SysWorkerService sysWorkerService;

    @Autowired
    private SysTeamService sysTeamService;

    @Autowired
    private ProdOrderService prodOrderService;

    @Autowired
    private ProdProcessService prodProcessService;

    @Autowired
    private ProdWorkOrderService prodWorkOrderService;

    @Autowired
    private RedisLaborService redisLaborService;

    @Override
    public List<LaborDailySummary> listAll() {
        return list();
    }

    @Override
    public IPage<LaborDailySummary> pageList(Integer pageNum, Integer pageSize, LaborDailySummary summary) {
        LambdaQueryWrapper<LaborDailySummary> wrapper = new LambdaQueryWrapper<>();
        if (summary != null) {
            if (summary.getSummaryDate() != null) {
                wrapper.eq(LaborDailySummary::getSummaryDate, summary.getSummaryDate());
            }
            if (summary.getWorkerId() != null) {
                wrapper.eq(LaborDailySummary::getWorkerId, summary.getWorkerId());
            }
            if (summary.getOrderId() != null) {
                wrapper.eq(LaborDailySummary::getOrderId, summary.getOrderId());
            }
            if (summary.getProcessId() != null) {
                wrapper.eq(LaborDailySummary::getProcessId, summary.getProcessId());
            }
        }
        wrapper.orderByDesc(LaborDailySummary::getSummaryDate);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public LaborDailySummary getById(Long id) {
        return super.getById(id);
    }

    @Override
    public boolean add(LaborDailySummary summary) {
        return save(summary);
    }

    @Override
    public boolean update(LaborDailySummary summary) {
        return updateById(summary);
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }

    @Override
    public List<WorkerLaborSummaryVO> summaryByWorker(LaborSummaryDTO dto) {
        List<LaborRecord> records = getRecordsByDTO(dto);

        Map<Long, WorkerLaborSummaryVO> summaryMap = new HashMap<>();
        Set<Long> workerIds = new HashSet<>();

        for (LaborRecord record : records) {
            Long workerId = record.getWorkerId();
            workerIds.add(workerId);
            WorkerLaborSummaryVO vo = summaryMap.computeIfAbsent(workerId, k -> createEmptyWorkerSummary());
            vo.setWorkerId(workerId);
            accumulateWorkerSummary(vo, record);
        }

        fillWorkerInfo(summaryMap.values());
        return new ArrayList<>(summaryMap.values());
    }

    @Override
    public List<OrderLaborSummaryVO> summaryByOrder(LaborSummaryDTO dto) {
        List<LaborRecord> records = getRecordsByDTO(dto);

        Map<Long, OrderLaborSummaryVO> summaryMap = new HashMap<>();

        for (LaborRecord record : records) {
            Long orderId = record.getOrderId();
            if (orderId == null) {
                continue;
            }
            OrderLaborSummaryVO vo = summaryMap.computeIfAbsent(orderId, k -> createEmptyOrderSummary());
            vo.setOrderId(orderId);
            accumulateOrderSummary(vo, record);
        }

        fillOrderInfo(summaryMap.values());
        return new ArrayList<>(summaryMap.values());
    }

    @Override
    public List<ProcessLaborSummaryVO> summaryByProcess(LaborSummaryDTO dto) {
        List<LaborRecord> records = getRecordsByDTO(dto);

        Map<Long, ProcessLaborSummaryVO> summaryMap = new HashMap<>();

        for (LaborRecord record : records) {
            Long processId = record.getProcessId();
            if (processId == null) {
                continue;
            }
            ProcessLaborSummaryVO vo = summaryMap.computeIfAbsent(processId, k -> createEmptyProcessSummary());
            vo.setProcessId(processId);
            accumulateProcessSummary(vo, record);
        }

        fillProcessInfo(summaryMap.values());
        return new ArrayList<>(summaryMap.values());
    }

    @Override
    public List<WorkOrderCompareVO> compareWorkOrder(Long orderId) {
        List<WorkOrderCompareVO> result = new ArrayList<>();

        LambdaQueryWrapper<ProdWorkOrder> workOrderWrapper = new LambdaQueryWrapper<>();
        if (orderId != null) {
            workOrderWrapper.eq(ProdWorkOrder::getOrderId, orderId);
        }
        List<ProdWorkOrder> workOrders = prodWorkOrderService.list(workOrderWrapper);

        if (workOrders.isEmpty()) {
            return result;
        }

        Set<Long> workOrderIds = workOrders.stream().map(ProdWorkOrder::getId).collect(Collectors.toSet());
        Set<Long> orderIds = workOrders.stream().map(ProdWorkOrder::getOrderId).collect(Collectors.toSet());
        Set<Long> processIds = workOrders.stream().map(ProdWorkOrder::getProcessId).collect(Collectors.toSet());

        Map<Long, ProdOrder> orderMap = prodOrderService.listByIds(orderIds).stream()
                .collect(Collectors.toMap(ProdOrder::getId, o -> o));
        Map<Long, ProdProcess> processMap = prodProcessService.listByIds(processIds).stream()
                .collect(Collectors.toMap(ProdProcess::getId, p -> p));

        LambdaQueryWrapper<LaborRecord> recordWrapper = new LambdaQueryWrapper<>();
        recordWrapper.in(LaborRecord::getWorkOrderId, workOrderIds);
        recordWrapper.eq(LaborRecord::getStatus, 1);
        List<LaborRecord> records = laborRecordService.list(recordWrapper);

        Map<Long, List<LaborRecord>> recordMap = records.stream()
                .filter(r -> r.getWorkOrderId() != null)
                .collect(Collectors.groupingBy(LaborRecord::getWorkOrderId));

        for (ProdWorkOrder workOrder : workOrders) {
            WorkOrderCompareVO vo = new WorkOrderCompareVO();
            vo.setWorkOrderId(workOrder.getId());
            vo.setWorkOrderNo(workOrder.getWorkOrderNo());
            vo.setOrderId(workOrder.getOrderId());
            vo.setProcessId(workOrder.getProcessId());
            vo.setPlanQty(workOrder.getPlanQty() != null ? workOrder.getPlanQty() : 0);
            vo.setCompletedQty(workOrder.getCompletedQty() != null ? workOrder.getCompletedQty() : 0);
            vo.setStandardHour(workOrder.getStandardHour() != null ? workOrder.getStandardHour() : BigDecimal.ZERO);

            ProdOrder order = orderMap.get(workOrder.getOrderId());
            if (order != null) {
                vo.setOrderNo(order.getOrderNo());
            }

            ProdProcess process = processMap.get(workOrder.getProcessId());
            if (process != null) {
                vo.setProcessName(process.getProcessName());
            }

            BigDecimal actualHours = BigDecimal.ZERO;
            List<LaborRecord> woRecords = recordMap.get(workOrder.getId());
            if (woRecords != null) {
                for (LaborRecord r : woRecords) {
                    if (r.getWorkHours() != null) {
                        actualHours = actualHours.add(r.getWorkHours());
                    }
                }
            }
            vo.setActualHours(actualHours);

            BigDecimal standardTotal = vo.getStandardHour().multiply(BigDecimal.valueOf(vo.getCompletedQty()));
            vo.setDiffHours(actualHours.subtract(standardTotal));

            if (actualHours.compareTo(BigDecimal.ZERO) > 0 && standardTotal.compareTo(BigDecimal.ZERO) > 0) {
                vo.setEfficiency(standardTotal.divide(actualHours, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)));
            } else {
                vo.setEfficiency(BigDecimal.ZERO);
            }

            result.add(vo);
        }

        return result;
    }

    @Override
    public List<LaborSummaryDTO> summaryByWorker(LocalDate startDate, LocalDate endDate) {
        List<LaborRecord> records = getRecordsByDateRange(startDate, endDate);

        Map<Long, LaborSummaryDTO> summaryMap = new HashMap<>();
        for (LaborRecord record : records) {
            Long workerId = record.getWorkerId();
            LaborSummaryDTO dto = summaryMap.computeIfAbsent(workerId, k -> createEmptySummary());
            dto.setWorkerId(workerId);
            accumulateSummary(dto, record);
        }

        fillWorkerNames(summaryMap.values());
        return new ArrayList<>(summaryMap.values());
    }

    @Override
    public List<LaborSummaryDTO> summaryByOrder(LocalDate startDate, LocalDate endDate) {
        List<LaborRecord> records = getRecordsByDateRange(startDate, endDate);

        Map<Long, LaborSummaryDTO> summaryMap = new HashMap<>();
        for (LaborRecord record : records) {
            Long orderId = record.getOrderId();
            if (orderId == null) {
                continue;
            }
            LaborSummaryDTO dto = summaryMap.computeIfAbsent(orderId, k -> createEmptySummary());
            dto.setOrderId(orderId);
            accumulateSummary(dto, record);
        }

        fillOrderNos(summaryMap.values());
        return new ArrayList<>(summaryMap.values());
    }

    @Override
    public List<LaborSummaryDTO> summaryByProcess(LocalDate startDate, LocalDate endDate) {
        List<LaborRecord> records = getRecordsByDateRange(startDate, endDate);

        Map<Long, LaborSummaryDTO> summaryMap = new HashMap<>();
        for (LaborRecord record : records) {
            Long processId = record.getProcessId();
            if (processId == null) {
                continue;
            }
            LaborSummaryDTO dto = summaryMap.computeIfAbsent(processId, k -> createEmptySummary());
            dto.setProcessId(processId);
            accumulateSummary(dto, record);
        }

        fillProcessNames(summaryMap.values());
        return new ArrayList<>(summaryMap.values());
    }

    @Override
    public List<LaborSummaryDTO> getTodayRealtimeSummary() {
        LocalDate today = LocalDate.now();
        List<LaborRecord> todayRecords = getRecordsByDateRange(today, today);

        Map<Long, LaborSummaryDTO> summaryMap = new HashMap<>();
        for (LaborRecord record : todayRecords) {
            Long workerId = record.getWorkerId();
            LaborSummaryDTO dto = summaryMap.computeIfAbsent(workerId, k -> createEmptySummary());
            dto.setWorkerId(workerId);
            accumulateSummary(dto, record);
        }

        for (Map.Entry<Long, LaborSummaryDTO> entry : summaryMap.entrySet()) {
            Long workerId = entry.getKey();
            LaborSummaryDTO dto = entry.getValue();

            BigDecimal redisDirect = redisLaborService.getTodayDirectHours(workerId);
            BigDecimal redisIndirect = redisLaborService.getTodayIndirectHours(workerId);
            BigDecimal redisOvertime = redisLaborService.getTodayOvertimeHours(workerId);
            BigDecimal redisTotal = redisLaborService.getTodayTotalHours(workerId);

            if (redisTotal.compareTo(dto.getTotalHours()) > 0) {
                dto.setDirectHours(redisDirect);
                dto.setIndirectHours(redisIndirect);
                dto.setOvertimeHours(redisOvertime);
                dto.setTotalHours(redisTotal);
            }
        }

        fillWorkerNames(summaryMap.values());
        return new ArrayList<>(summaryMap.values());
    }

    @Override
    public LaborSummaryDTO summaryByWorkerAndDate(Long workerId, LocalDate date) {
        LaborSummaryDTO dto = createEmptySummary();
        dto.setWorkerId(workerId);

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        LambdaQueryWrapper<LaborRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LaborRecord::getWorkerId, workerId);
        wrapper.eq(LaborRecord::getStatus, 1);
        wrapper.between(LaborRecord::getStartTime, startOfDay, endOfDay);

        List<LaborRecord> records = laborRecordService.list(wrapper);
        for (LaborRecord record : records) {
            accumulateSummary(dto, record);
        }

        if (date.equals(LocalDate.now())) {
            BigDecimal redisDirect = redisLaborService.getTodayDirectHours(workerId);
            BigDecimal redisIndirect = redisLaborService.getTodayIndirectHours(workerId);
            BigDecimal redisOvertime = redisLaborService.getTodayOvertimeHours(workerId);
            BigDecimal redisTotal = redisLaborService.getTodayTotalHours(workerId);

            if (redisTotal.compareTo(dto.getTotalHours()) > 0) {
                dto.setDirectHours(redisDirect);
                dto.setIndirectHours(redisIndirect);
                dto.setOvertimeHours(redisOvertime);
                dto.setTotalHours(redisTotal);
            }
        }

        SysWorker worker = sysWorkerService.getById(workerId);
        if (worker != null) {
            dto.setWorkerName(worker.getWorkerName());
        }

        return dto;
    }

    private List<LaborRecord> getRecordsByDTO(LaborSummaryDTO dto) {
        LocalDate startDate = dto != null && dto.getStartDate() != null ? dto.getStartDate() : LocalDate.now().minusDays(30);
        LocalDate endDate = dto != null && dto.getEndDate() != null ? dto.getEndDate() : LocalDate.now();

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        LambdaQueryWrapper<LaborRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LaborRecord::getStatus, 1);
        wrapper.between(LaborRecord::getStartTime, start, end);

        if (dto != null) {
            if (dto.getWorkerId() != null) {
                wrapper.eq(LaborRecord::getWorkerId, dto.getWorkerId());
            }
            if (dto.getTeamId() != null) {
                LambdaQueryWrapper<SysWorker> workerWrapper = new LambdaQueryWrapper<>();
                workerWrapper.eq(SysWorker::getTeamId, dto.getTeamId());
                List<SysWorker> workers = sysWorkerService.list(workerWrapper);
                if (!workers.isEmpty()) {
                    List<Long> workerIds = workers.stream().map(SysWorker::getId).collect(Collectors.toList());
                    wrapper.in(LaborRecord::getWorkerId, workerIds);
                } else {
                    return new ArrayList<>();
                }
            }
            if (dto.getOrderId() != null) {
                wrapper.eq(LaborRecord::getOrderId, dto.getOrderId());
            }
            if (dto.getProcessId() != null) {
                wrapper.eq(LaborRecord::getProcessId, dto.getProcessId());
            }
            if (dto.getLaborType() != null) {
                wrapper.eq(LaborRecord::getLaborType, dto.getLaborType());
            }
        }

        return laborRecordService.list(wrapper);
    }

    private List<LaborRecord> getRecordsByDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        LambdaQueryWrapper<LaborRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LaborRecord::getStatus, 1);
        wrapper.between(LaborRecord::getStartTime, start, end);

        return laborRecordService.list(wrapper);
    }

    private WorkerLaborSummaryVO createEmptyWorkerSummary() {
        WorkerLaborSummaryVO vo = new WorkerLaborSummaryVO();
        vo.setDirectHours(BigDecimal.ZERO);
        vo.setIndirectHours(BigDecimal.ZERO);
        vo.setOvertimeHours(BigDecimal.ZERO);
        vo.setTotalHours(BigDecimal.ZERO);
        vo.setCompletedQty(0);
        return vo;
    }

    private OrderLaborSummaryVO createEmptyOrderSummary() {
        OrderLaborSummaryVO vo = new OrderLaborSummaryVO();
        vo.setDirectHours(BigDecimal.ZERO);
        vo.setIndirectHours(BigDecimal.ZERO);
        vo.setTotalHours(BigDecimal.ZERO);
        vo.setCompletedQty(0);
        return vo;
    }

    private ProcessLaborSummaryVO createEmptyProcessSummary() {
        ProcessLaborSummaryVO vo = new ProcessLaborSummaryVO();
        vo.setDirectHours(BigDecimal.ZERO);
        vo.setIndirectHours(BigDecimal.ZERO);
        vo.setTotalHours(BigDecimal.ZERO);
        return vo;
    }

    private LaborSummaryDTO createEmptySummary() {
        LaborSummaryDTO dto = new LaborSummaryDTO();
        dto.setDirectHours(BigDecimal.ZERO);
        dto.setIndirectHours(BigDecimal.ZERO);
        dto.setOvertimeHours(BigDecimal.ZERO);
        dto.setTotalHours(BigDecimal.ZERO);
        dto.setCompletedQty(0);
        return dto;
    }

    private void accumulateWorkerSummary(WorkerLaborSummaryVO vo, LaborRecord record) {
        BigDecimal hours = record.getWorkHours() != null ? record.getWorkHours() : BigDecimal.ZERO;
        BigDecimal overtime = (record.getIsOvertime() != null && record.getIsOvertime() == 1) ? hours : BigDecimal.ZERO;
        Integer qty = record.getCompletedQty() != null ? record.getCompletedQty() : 0;

        if (record.getLaborType() != null && record.getLaborType() == 1) {
            vo.setDirectHours(vo.getDirectHours().add(hours));
        } else {
            vo.setIndirectHours(vo.getIndirectHours().add(hours));
        }

        vo.setOvertimeHours(vo.getOvertimeHours().add(overtime));
        vo.setTotalHours(vo.getTotalHours().add(hours));
        vo.setCompletedQty(vo.getCompletedQty() + qty);
    }

    private void accumulateOrderSummary(OrderLaborSummaryVO vo, LaborRecord record) {
        BigDecimal hours = record.getWorkHours() != null ? record.getWorkHours() : BigDecimal.ZERO;
        Integer qty = record.getCompletedQty() != null ? record.getCompletedQty() : 0;

        if (record.getLaborType() != null && record.getLaborType() == 1) {
            vo.setDirectHours(vo.getDirectHours().add(hours));
        } else {
            vo.setIndirectHours(vo.getIndirectHours().add(hours));
        }

        vo.setTotalHours(vo.getTotalHours().add(hours));
        vo.setCompletedQty(vo.getCompletedQty() + qty);
    }

    private void accumulateProcessSummary(ProcessLaborSummaryVO vo, LaborRecord record) {
        BigDecimal hours = record.getWorkHours() != null ? record.getWorkHours() : BigDecimal.ZERO;

        if (record.getLaborType() != null && record.getLaborType() == 1) {
            vo.setDirectHours(vo.getDirectHours().add(hours));
        } else {
            vo.setIndirectHours(vo.getIndirectHours().add(hours));
        }

        vo.setTotalHours(vo.getTotalHours().add(hours));
    }

    private void accumulateSummary(LaborSummaryDTO dto, LaborRecord record) {
        BigDecimal hours = record.getWorkHours() != null ? record.getWorkHours() : BigDecimal.ZERO;
        BigDecimal overtime = (record.getIsOvertime() != null && record.getIsOvertime() == 1) ? hours : BigDecimal.ZERO;
        Integer qty = record.getCompletedQty() != null ? record.getCompletedQty() : 0;

        if (record.getLaborType() != null && record.getLaborType() == 1) {
            dto.setDirectHours(dto.getDirectHours().add(hours));
        } else {
            dto.setIndirectHours(dto.getIndirectHours().add(hours));
        }

        dto.setOvertimeHours(dto.getOvertimeHours().add(overtime));
        dto.setTotalHours(dto.getTotalHours().add(hours));
        dto.setCompletedQty(dto.getCompletedQty() + qty);
    }

    private void fillWorkerInfo(Iterable<WorkerLaborSummaryVO> vos) {
        List<Long> workerIds = new ArrayList<>();
        for (WorkerLaborSummaryVO vo : vos) {
            if (vo.getWorkerId() != null) {
                workerIds.add(vo.getWorkerId());
            }
        }
        if (workerIds.isEmpty()) {
            return;
        }

        List<SysWorker> workers = sysWorkerService.listByIds(workerIds);
        Map<Long, SysWorker> workerMap = workers.stream()
                .collect(Collectors.toMap(SysWorker::getId, w -> w));

        Set<Long> teamIds = workers.stream()
                .map(SysWorker::getTeamId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        Map<Long, String> teamNameMap = new HashMap<>();
        if (!teamIds.isEmpty()) {
            List<SysTeam> teams = sysTeamService.listByIds(teamIds);
            teamNameMap = teams.stream()
                    .collect(Collectors.toMap(SysTeam::getId, SysTeam::getTeamName));
        }

        for (WorkerLaborSummaryVO vo : vos) {
            if (vo.getWorkerId() != null) {
                SysWorker worker = workerMap.get(vo.getWorkerId());
                if (worker != null) {
                    vo.setWorkerNo(worker.getWorkerNo());
                    vo.setWorkerName(worker.getWorkerName());
                    if (worker.getTeamId() != null) {
                        vo.setTeamName(teamNameMap.get(worker.getTeamId()));
                    }
                }
            }
        }
    }

    private void fillWorkerNames(Iterable<LaborSummaryDTO> dtos) {
        List<Long> workerIds = new ArrayList<>();
        for (LaborSummaryDTO dto : dtos) {
            if (dto.getWorkerId() != null) {
                workerIds.add(dto.getWorkerId());
            }
        }
        if (workerIds.isEmpty()) {
            return;
        }

        List<SysWorker> workers = sysWorkerService.listByIds(workerIds);
        Map<Long, String> workerNameMap = workers.stream()
                .collect(Collectors.toMap(SysWorker::getId, SysWorker::getWorkerName));

        for (LaborSummaryDTO dto : dtos) {
            if (dto.getWorkerId() != null) {
                dto.setWorkerName(workerNameMap.get(dto.getWorkerId()));
            }
        }
    }

    private void fillOrderInfo(Iterable<OrderLaborSummaryVO> vos) {
        List<Long> orderIds = new ArrayList<>();
        for (OrderLaborSummaryVO vo : vos) {
            if (vo.getOrderId() != null) {
                orderIds.add(vo.getOrderId());
            }
        }
        if (orderIds.isEmpty()) {
            return;
        }

        List<ProdOrder> orders = prodOrderService.listByIds(orderIds);
        Map<Long, ProdOrder> orderMap = orders.stream()
                .collect(Collectors.toMap(ProdOrder::getId, o -> o));

        for (OrderLaborSummaryVO vo : vos) {
            if (vo.getOrderId() != null) {
                ProdOrder order = orderMap.get(vo.getOrderId());
                if (order != null) {
                    vo.setOrderNo(order.getOrderNo());
                    vo.setOrderName(order.getOrderName());
                    vo.setProductName(order.getProductName());
                }
            }
        }
    }

    private void fillOrderNos(Iterable<LaborSummaryDTO> dtos) {
        List<Long> orderIds = new ArrayList<>();
        for (LaborSummaryDTO dto : dtos) {
            if (dto.getOrderId() != null) {
                orderIds.add(dto.getOrderId());
            }
        }
        if (orderIds.isEmpty()) {
            return;
        }

        List<ProdOrder> orders = prodOrderService.listByIds(orderIds);
        Map<Long, String> orderNoMap = orders.stream()
                .collect(Collectors.toMap(ProdOrder::getId, ProdOrder::getOrderNo));

        for (LaborSummaryDTO dto : dtos) {
            if (dto.getOrderId() != null) {
                dto.setOrderNo(orderNoMap.get(dto.getOrderId()));
            }
        }
    }

    private void fillProcessInfo(Iterable<ProcessLaborSummaryVO> vos) {
        List<Long> processIds = new ArrayList<>();
        for (ProcessLaborSummaryVO vo : vos) {
            if (vo.getProcessId() != null) {
                processIds.add(vo.getProcessId());
            }
        }
        if (processIds.isEmpty()) {
            return;
        }

        List<ProdProcess> processes = prodProcessService.listByIds(processIds);
        Map<Long, ProdProcess> processMap = processes.stream()
                .collect(Collectors.toMap(ProdProcess::getId, p -> p));

        for (ProcessLaborSummaryVO vo : vos) {
            if (vo.getProcessId() != null) {
                ProdProcess process = processMap.get(vo.getProcessId());
                if (process != null) {
                    vo.setProcessCode(process.getProcessCode());
                    vo.setProcessName(process.getProcessName());
                    vo.setProcessType(process.getProcessType());
                }
            }
        }
    }

    private void fillProcessNames(Iterable<LaborSummaryDTO> dtos) {
        List<Long> processIds = new ArrayList<>();
        for (LaborSummaryDTO dto : dtos) {
            if (dto.getProcessId() != null) {
                processIds.add(dto.getProcessId());
            }
        }
        if (processIds.isEmpty()) {
            return;
        }

        List<ProdProcess> processes = prodProcessService.listByIds(processIds);
        Map<Long, String> processNameMap = processes.stream()
                .collect(Collectors.toMap(ProdProcess::getId, ProdProcess::getProcessName));

        for (LaborSummaryDTO dto : dtos) {
            if (dto.getProcessId() != null) {
                dto.setProcessName(processNameMap.get(dto.getProcessId()));
            }
        }
    }
}
