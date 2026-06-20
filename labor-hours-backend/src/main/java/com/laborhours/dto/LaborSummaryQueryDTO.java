package com.laborhours.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LaborSummaryQueryDTO {

    private Long workerId;

    private Long teamId;

    private Long orderId;

    private Long processId;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer laborType;
}
