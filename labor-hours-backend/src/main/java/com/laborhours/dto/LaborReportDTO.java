package com.laborhours.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LaborReportDTO {

    private Long workerId;

    private Long workOrderId;

    private Integer laborType;

    private Integer reportType;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private BigDecimal workHours;

    private Integer completedQty;

    private Integer isOvertime;

    private String remark;
}
