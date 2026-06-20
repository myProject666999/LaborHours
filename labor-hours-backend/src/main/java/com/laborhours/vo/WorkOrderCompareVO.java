package com.laborhours.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrderCompareVO {

    private Long workOrderId;

    private String workOrderNo;

    private Long orderId;

    private String orderNo;

    private Long processId;

    private String processName;

    private Integer planQty;

    private Integer completedQty;

    private BigDecimal standardHour;

    private BigDecimal actualHours;

    private BigDecimal diffHours;

    private BigDecimal efficiency;
}
