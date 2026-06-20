package com.laborhours.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodayLaborCacheVO {

    private Long workerId;

    private String workerName;

    private LocalDate date;

    private BigDecimal directHours;

    private BigDecimal indirectHours;

    private BigDecimal overtimeHours;

    private BigDecimal totalHours;

    private Long timestamp;
}
