package com.laborhours.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.laborhours.common.Result;
import com.laborhours.entity.ProdWorkOrder;
import com.laborhours.service.ProdWorkOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/workOrder")
public class WorkOrderController {

    @Autowired
    private ProdWorkOrderService prodWorkOrderService;

    @GetMapping("/list")
    public Result<List<ProdWorkOrder>> list() {
        return Result.success(prodWorkOrderService.listAll());
    }

    @GetMapping("/page")
    public Result<IPage<ProdWorkOrder>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                             @RequestParam(defaultValue = "10") Integer pageSize,
                                             ProdWorkOrder workOrder) {
        return Result.success(prodWorkOrderService.pageList(pageNum, pageSize, workOrder));
    }

    @GetMapping("/{id}")
    public Result<ProdWorkOrder> getById(@PathVariable Long id) {
        return Result.success(prodWorkOrderService.getById(id));
    }

    @PostMapping("/add")
    public Result<Void> add(@RequestBody ProdWorkOrder workOrder) {
        boolean success = prodWorkOrderService.add(workOrder);
        return success ? Result.success() : Result.error();
    }

    @PostMapping("/update")
    public Result<Void> update(@RequestBody ProdWorkOrder workOrder) {
        boolean success = prodWorkOrderService.update(workOrder);
        return success ? Result.success() : Result.error();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = prodWorkOrderService.delete(id);
        return success ? Result.success() : Result.error();
    }
}
