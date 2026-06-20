package com.laborhours.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderLaborSummaryVO {

    private Long orderId;

    private String orderNo;

    private String orderName;

    private String productName;

    private BigDecimal directHours;

    private BigDecimal indirectHours;

    private BigDecimal totalHours;

    private Integer completedQty;
}
