package com.laborhours.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.laborhours.dto.WorkHourCompareDTO;
import com.laborhours.entity.LaborRecord;
import com.laborhours.entity.ProdOrder;
import com.laborhours.entity.ProdProcess;
import com.laborhours.entity.ProdWorkOrder;
import com.laborhours.service.LaborRecordService;
import com.laborhours.service.ProdOrderService;
import com.laborhours.service.ProdProcessService;
import com.laborhours.service.ProdWorkOrderService;
import com.laborhours.service.WorkHourCompareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WorkHourCompareServiceImpl implements WorkHourCompareService {

    @Autowired
    private ProdWorkOrderService prodWorkOrderService;

    @Autowired
    private ProdOrderService prodOrderService;

    @Autowired
    private ProdProcessService prodProcessService;

    @Autowired
    private LaborRecordService laborRecordService;

    @Override
    public WorkHourCompareDTO compareByWorkOrder(Long workOrderId) {
        ProdWorkOrder workOrder = prodWorkOrderService.getById(workOrderId);
        if (workOrder == null) {
            return null;
        }
        return buildCompareDTO(workOrder);
    }

    @Override
    public List<WorkHourCompareDTO> compareByOrder(Long orderId) {
        LambdaQueryWrapper<ProdWorkOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProdWorkOrder::getOrderId, orderId);
        List<ProdWorkOrder> workOrders = prodWorkOrderService.list(wrapper);

        List<WorkHourCompareDTO> result = new ArrayList<>();
        for (ProdWorkOrder workOrder : workOrders) {
            result.add(buildCompareDTO(workOrder));
        }
        return result;
    }

    @Override
    public List<WorkHourCompareDTO> compareList() {
        List<ProdWorkOrder> workOrders = prodWorkOrderService.list();
        List<WorkHourCompareDTO> result = new ArrayList<>();
        for (ProdWorkOrder workOrder : workOrders) {
            result.add(buildCompareDTO(workOrder));
        }
        return result;
    }

    private WorkHourCompareDTO buildCompareDTO(ProdWorkOrder workOrder) {
        WorkHourCompareDTO dto = new WorkHourCompareDTO();
        dto.setWorkOrderId(workOrder.getId());
        dto.setWorkOrderNo(workOrder.getWorkOrderNo());
        dto.setPlanQty(workOrder.getPlanQty());
        dto.setCompletedQty(workOrder.getCompletedQty());

        BigDecimal standardHour = workOrder.getStandardHour() != null ? workOrder.getStandardHour() : BigDecimal.ZERO;
        Integer planQty = workOrder.getPlanQty() != null ? workOrder.getPlanQty() : 0;
        BigDecimal standardHours = standardHour.multiply(BigDecimal.valueOf(planQty));
        dto.setStandardHours(standardHours);

        BigDecimal actualHours = calculateActualHours(workOrder.getId());
        dto.setActualHours(actualHours);

        BigDecimal diffHours = actualHours.subtract(standardHours);
        dto.setDiffHours(diffHours);

        if (standardHours.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal diffRate = diffHours.divide(standardHours, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            dto.setDiffRate(diffRate);
        } else {
            dto.setDiffRate(BigDecimal.ZERO);
        }

        if (workOrder.getOrderId() != null) {
            ProdOrder order = prodOrderService.getById(workOrder.getOrderId());
            if (order != null) {
                dto.setOrderName(order.getOrderName());
            }
        }

        if (workOrder.getProcessId() != null) {
            ProdProcess process = prodProcessService.getById(workOrder.getProcessId());
            if (process != null) {
                dto.setProcessName(process.getProcessName());
            }
        }

        return dto;
    }

    private BigDecimal calculateActualHours(Long workOrderId) {
        LambdaQueryWrapper<LaborRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LaborRecord::getWorkOrderId, workOrderId);
        wrapper.eq(LaborRecord::getStatus, 1);

        List<LaborRecord> records = laborRecordService.list(wrapper);
        BigDecimal total = BigDecimal.ZERO;
        for (LaborRecord record : records) {
            if (record.getWorkHours() != null) {
                total = total.add(record.getWorkHours());
            }
        }
        return total;
    }
}
