package com.laborhours.controller;

import com.laborhours.common.Result;
import com.laborhours.dto.WorkHourCompareDTO;
import com.laborhours.service.WorkHourCompareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workHourCompare")
public class WorkHourCompareController {

    @Autowired
    private WorkHourCompareService workHourCompareService;

    @GetMapping("/workOrder/{workOrderId}")
    public Result<WorkHourCompareDTO> compareByWorkOrder(@PathVariable Long workOrderId) {
        WorkHourCompareDTO dto = workHourCompareService.compareByWorkOrder(workOrderId);
        if (dto == null) {
            return Result.error("工单不存在");
        }
        return Result.success(dto);
    }

    @GetMapping("/order/{orderId}")
    public Result<List<WorkHourCompareDTO>> compareByOrder(@PathVariable Long orderId) {
        return Result.success(workHourCompareService.compareByOrder(orderId));
    }

    @GetMapping("/list")
    public Result<List<WorkHourCompareDTO>> compareList() {
        return Result.success(workHourCompareService.compareList());
    }
}
