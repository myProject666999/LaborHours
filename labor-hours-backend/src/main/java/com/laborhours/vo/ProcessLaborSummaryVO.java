package com.laborhours.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessLaborSummaryVO {

    private Long processId;

    private String processCode;

    private String processName;

    private Integer processType;

    private BigDecimal directHours;

    private BigDecimal indirectHours;

    private BigDecimal totalHours;
}
