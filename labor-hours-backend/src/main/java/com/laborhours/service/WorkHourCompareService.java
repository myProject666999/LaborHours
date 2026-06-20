package com.laborhours.service;

import com.laborhours.dto.WorkHourCompareDTO;

import java.util.List;

public interface WorkHourCompareService {

    WorkHourCompareDTO compareByWorkOrder(Long workOrderId);

    List<WorkHourCompareDTO> compareByOrder(Long orderId);

    List<WorkHourCompareDTO> compareList();
}
