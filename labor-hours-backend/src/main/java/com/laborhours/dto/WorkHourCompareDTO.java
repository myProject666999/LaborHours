package com.laborhours.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WorkHourCompareDTO {

    private Long workOrderId;

    private String workOrderNo;

    private String orderName;

    private String processName;

    private Integer planQty;

    private Integer completedQty;

    private BigDecimal standardHours;

    private BigDecimal actualHours;

    private BigDecimal diffHours;

    private BigDecimal diffRate;
}
