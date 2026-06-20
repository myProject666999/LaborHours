package com.laborhours.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkerLaborSummaryVO {

    private Long workerId;

    private String workerNo;

    private String workerName;

    private String teamName;

    private BigDecimal directHours;

    private BigDecimal indirectHours;

    private BigDecimal overtimeHours;

    private BigDecimal totalHours;

    private Integer completedQty;
}
