package com.laborhours.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.laborhours.common.Result;
import com.laborhours.entity.ProdOrder;
import com.laborhours.service.ProdOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private ProdOrderService prodOrderService;

    @GetMapping("/list")
    public Result<List<ProdOrder>> list() {
        return Result.success(prodOrderService.listAll());
    }

    @GetMapping("/page")
    public Result<IPage<ProdOrder>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                         @RequestParam(defaultValue = "10") Integer pageSize,
                                         ProdOrder order) {
        return Result.success(prodOrderService.pageList(pageNum, pageSize, order));
    }

    @GetMapping("/{id}")
    public Result<ProdOrder> getById(@PathVariable Long id) {
        return Result.success(prodOrderService.getById(id));
    }

    @PostMapping("/add")
    public Result<Void> add(@RequestBody ProdOrder order) {
        boolean success = prodOrderService.add(order);
        return success ? Result.success() : Result.error();
    }

    @PostMapping("/update")
    public Result<Void> update(@RequestBody ProdOrder order) {
        boolean success = prodOrderService.update(order);
        return success ? Result.success() : Result.error();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = prodOrderService.delete(id);
        return success ? Result.success() : Result.error();
    }
}
