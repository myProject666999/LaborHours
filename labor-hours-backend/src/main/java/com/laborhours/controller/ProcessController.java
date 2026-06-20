package com.laborhours.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.laborhours.common.Result;
import com.laborhours.entity.ProdProcess;
import com.laborhours.service.ProdProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/process")
public class ProcessController {

    @Autowired
    private ProdProcessService prodProcessService;

    @GetMapping("/list")
    public Result<List<ProdProcess>> list() {
        return Result.success(prodProcessService.listAll());
    }

    @GetMapping("/page")
    public Result<IPage<ProdProcess>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                           @RequestParam(defaultValue = "10") Integer pageSize,
                                           ProdProcess process) {
        return Result.success(prodProcessService.pageList(pageNum, pageSize, process));
    }

    @GetMapping("/{id}")
    public Result<ProdProcess> getById(@PathVariable Long id) {
        return Result.success(prodProcessService.getById(id));
    }

    @PostMapping("/add")
    public Result<Void> add(@RequestBody ProdProcess process) {
        boolean success = prodProcessService.add(process);
        return success ? Result.success() : Result.error();
    }

    @PostMapping("/update")
    public Result<Void> update(@RequestBody ProdProcess process) {
        boolean success = prodProcessService.update(process);
        return success ? Result.success() : Result.error();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = prodProcessService.delete(id);
        return success ? Result.success() : Result.error();
    }
}
