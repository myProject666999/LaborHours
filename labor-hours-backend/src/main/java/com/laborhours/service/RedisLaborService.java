package com.laborhours.service;

import com.laborhours.vo.TodayLaborCacheVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface RedisLaborService {

    void addTodayLaborHours(Long workerId, Integer laborType, BigDecimal hours, BigDecimal overtimeHours);

    TodayLaborCacheVO getTodayLaborHours(Long workerId);

    Map<Long, TodayLaborCacheVO> getTodayLaborHoursBatch(List<Long> workerIds);

    Map<Long, TodayLaborCacheVO> getAllTodayLaborHours();

    void deleteTodayLaborCache(Long workerId);

    void refreshTodayLaborCache(Long workerId);

    BigDecimal getTodayDirectHours(Long workerId);

    BigDecimal getTodayIndirectHours(Long workerId);

    BigDecimal getTodayOvertimeHours(Long workerId);

    BigDecimal getTodayTotalHours(Long workerId);
}
