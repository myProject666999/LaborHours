package com.laborhours.controller;

import com.laborhours.common.Result;
import com.laborhours.service.RedisLaborService;
import com.laborhours.vo.TodayLaborCacheVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/redisLabor")
public class RedisLaborController {

    private static final Logger log = LoggerFactory.getLogger(RedisLaborController.class);

    @Autowired
    private RedisLaborService redisLaborService;

    @GetMapping("/today/{workerId}")
    public Result<TodayLaborCacheVO> getTodayLaborHours(@PathVariable Long workerId) {
        log.debug("从Redis获取工人当日实时工时, workerId={}", workerId);
        return Result.success(redisLaborService.getTodayLaborHours(workerId));
    }

    @PostMapping("/today/batch")
    public Result<Map<Long, TodayLaborCacheVO>> getTodayLaborHoursBatch(@RequestBody List<Long> workerIds) {
        log.debug("批量从Redis获取工人当日实时工时, workerIds={}", workerIds);
        return Result.success(redisLaborService.getTodayLaborHoursBatch(workerIds));
    }

    @GetMapping("/today/all")
    public Result<Map<Long, TodayLaborCacheVO>> getAllTodayLaborHours() {
        log.debug("从Redis获取所有工人当日实时工时");
        return Result.success(redisLaborService.getAllTodayLaborHours());
    }

    @PostMapping("/refresh/{workerId}")
    public Result<Void> refreshTodayLaborCache(@PathVariable Long workerId) {
        log.info("刷新工人当日工时缓存, workerId={}", workerId);
        redisLaborService.refreshTodayLaborCache(workerId);
        return Result.success();
    }

    @PostMapping("/delete/{workerId}")
    public Result<Void> deleteTodayLaborCache(@PathVariable Long workerId) {
        log.info("删除工人当日工时缓存, workerId={}", workerId);
        redisLaborService.deleteTodayLaborCache(workerId);
        return Result.success();
    }
}
