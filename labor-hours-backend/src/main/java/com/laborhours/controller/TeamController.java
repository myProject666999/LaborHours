package com.laborhours.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.laborhours.common.Result;
import com.laborhours.entity.SysTeam;
import com.laborhours.service.SysTeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/team")
public class TeamController {

    @Autowired
    private SysTeamService sysTeamService;

    @GetMapping("/list")
    public Result<List<SysTeam>> list() {
        return Result.success(sysTeamService.listAll());
    }

    @GetMapping("/page")
    public Result<IPage<SysTeam>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                       @RequestParam(defaultValue = "10") Integer pageSize,
                                       SysTeam team) {
        return Result.success(sysTeamService.pageList(pageNum, pageSize, team));
    }

    @GetMapping("/{id}")
    public Result<SysTeam> getById(@PathVariable Long id) {
        return Result.success(sysTeamService.getById(id));
    }

    @PostMapping("/add")
    public Result<Void> add(@RequestBody SysTeam team) {
        boolean success = sysTeamService.add(team);
        return success ? Result.success() : Result.error();
    }

    @PostMapping("/update")
    public Result<Void> update(@RequestBody SysTeam team) {
        boolean success = sysTeamService.update(team);
        return success ? Result.success() : Result.error();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = sysTeamService.delete(id);
        return success ? Result.success() : Result.error();
    }
}
