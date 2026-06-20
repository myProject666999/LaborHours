package com.laborhours.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.laborhours.common.Result;
import com.laborhours.entity.SysWorker;
import com.laborhours.service.SysWorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/worker")
public class WorkerController {

    @Autowired
    private SysWorkerService sysWorkerService;

    @GetMapping("/list")
    public Result<List<SysWorker>> list() {
        return Result.success(sysWorkerService.listAll());
    }

    @GetMapping("/page")
    public Result<IPage<SysWorker>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                         @RequestParam(defaultValue = "10") Integer pageSize,
                                         SysWorker worker) {
        return Result.success(sysWorkerService.pageList(pageNum, pageSize, worker));
    }

    @GetMapping("/{id}")
    public Result<SysWorker> getById(@PathVariable Long id) {
        return Result.success(sysWorkerService.getById(id));
    }

    @PostMapping("/add")
    public Result<Void> add(@RequestBody SysWorker worker) {
        boolean success = sysWorkerService.add(worker);
        return success ? Result.success() : Result.error();
    }

    @PostMapping("/update")
    public Result<Void> update(@RequestBody SysWorker worker) {
        boolean success = sysWorkerService.update(worker);
        return success ? Result.success() : Result.error();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = sysWorkerService.delete(id);
        return success ? Result.success() : Result.error();
    }
}
