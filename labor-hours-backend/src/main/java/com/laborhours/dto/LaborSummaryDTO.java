package com.laborhours.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LaborSummaryDTO {

    private Long workerId;

    private String workerName;

    private Long teamId;

    private Long orderId;

    private String orderNo;

    private Long processId;

    private String processName;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer laborType;

    private BigDecimal directHours;

    private BigDecimal indirectHours;

    private BigDecimal overtimeHours;

    private BigDecimal totalHours;

    private Integer completedQty;
}
