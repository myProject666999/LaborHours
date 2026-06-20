package com.laborhours.service.impl;

import com.laborhours.entity.LaborRecord;
import com.laborhours.entity.SysWorker;
import com.laborhours.service.LaborRecordService;
import com.laborhours.service.RedisLaborService;
import com.laborhours.service.SysWorkerService;
import com.laborhours.vo.TodayLaborCacheVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class RedisLaborServiceImpl implements RedisLaborService {

    private static final Logger log = LoggerFactory.getLogger(RedisLaborServiceImpl.class);

    private static final String KEY_PREFIX = "labor:today:";
    private static final String FIELD_DIRECT = "directHours";
    private static final String FIELD_INDIRECT = "indirectHours";
    private static final String FIELD_OVERTIME = "overtimeHours";
    private static final String FIELD_TOTAL = "totalHours";
    private static final String FIELD_TIMESTAMP = "timestamp";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    @org.springframework.context.annotation.Lazy
    private LaborRecordService laborRecordService;

    @Autowired
    private SysWorkerService sysWorkerService;

    @Override
    public void addTodayLaborHours(Long workerId, Integer laborType, BigDecimal hours, BigDecimal overtimeHours) {
        String key = getTodayKey(workerId);
        HashOperations<String, String, Object> hashOps = redisTemplate.opsForHash();

        log.debug("更新工人[{}]当日工时缓存: laborType={}, hours={}, overtime={}", workerId, laborType, hours, overtimeHours);

        BigDecimal directHours = BigDecimal.ZERO;
        BigDecimal indirectHours = BigDecimal.ZERO;
        BigDecimal currentOvertime = BigDecimal.ZERO;

        Map<String, Object> existing = hashOps.entries(key);
        if (existing != null && !existing.isEmpty()) {
            directHours = toBigDecimal(existing.get(FIELD_DIRECT));
            indirectHours = toBigDecimal(existing.get(FIELD_INDIRECT));
            currentOvertime = toBigDecimal(existing.get(FIELD_OVERTIME));
        }

        if (laborType != null && laborType == 1) {
            directHours = directHours.add(hours);
        } else {
            indirectHours = indirectHours.add(hours);
        }
        if (overtimeHours != null) {
            currentOvertime = currentOvertime.add(overtimeHours);
        }
        BigDecimal totalHours = directHours.add(indirectHours);

        Map<String, Object> data = new HashMap<>();
        data.put(FIELD_DIRECT, directHours);
        data.put(FIELD_INDIRECT, indirectHours);
        data.put(FIELD_OVERTIME, currentOvertime);
        data.put(FIELD_TOTAL, totalHours);
        data.put(FIELD_TIMESTAMP, System.currentTimeMillis());

        hashOps.putAll(key, data);
        long ttl = getSecondsUntilNextDay2AM();
        redisTemplate.expire(key, ttl, TimeUnit.SECONDS);

        log.debug("工人[{}]当日工时缓存更新完成: direct={}, indirect={}, overtime={}, total={}",
                workerId, directHours, indirectHours, currentOvertime, totalHours);
    }

    @Override
    public TodayLaborCacheVO getTodayLaborHours(Long workerId) {
        String key = getTodayKey(workerId);
        HashOperations<String, String, Object> hashOps = redisTemplate.opsForHash();
        Map<String, Object> data = hashOps.entries(key);

        TodayLaborCacheVO vo = new TodayLaborCacheVO();
        vo.setWorkerId(workerId);
        vo.setDate(LocalDate.now());

        if (data != null && !data.isEmpty()) {
            vo.setDirectHours(toBigDecimal(data.get(FIELD_DIRECT)));
            vo.setIndirectHours(toBigDecimal(data.get(FIELD_INDIRECT)));
            vo.setOvertimeHours(toBigDecimal(data.get(FIELD_OVERTIME)));
            vo.setTotalHours(toBigDecimal(data.get(FIELD_TOTAL)));
            vo.setTimestamp(toLong(data.get(FIELD_TIMESTAMP)));
        } else {
            vo.setDirectHours(BigDecimal.ZERO);
            vo.setIndirectHours(BigDecimal.ZERO);
            vo.setOvertimeHours(BigDecimal.ZERO);
            vo.setTotalHours(BigDecimal.ZERO);
            vo.setTimestamp(System.currentTimeMillis());
        }

        SysWorker worker = sysWorkerService.getById(workerId);
        if (worker != null) {
            vo.setWorkerName(worker.getWorkerName());
        }

        return vo;
    }

    @Override
    public Map<Long, TodayLaborCacheVO> getTodayLaborHoursBatch(List<Long> workerIds) {
        Map<Long, TodayLaborCacheVO> result = new HashMap<>();
        for (Long workerId : workerIds) {
            result.put(workerId, getTodayLaborHours(workerId));
        }
        return result;
    }

    @Override
    public Map<Long, TodayLaborCacheVO> getAllTodayLaborHours() {
        String pattern = KEY_PREFIX + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ":*";
        Set<String> keys = redisTemplate.keys(pattern);

        Map<Long, TodayLaborCacheVO> result = new HashMap<>();
        if (keys == null || keys.isEmpty()) {
            List<SysWorker> workers = sysWorkerService.lambdaQuery()
                    .eq(SysWorker::getStatus, 1)
                    .list();
            for (SysWorker worker : workers) {
                TodayLaborCacheVO vo = getTodayLaborHours(worker.getId());
                result.put(worker.getId(), vo);
            }
            return result;
        }

        for (String key : keys) {
            try {
                String[] parts = key.split(":");
                Long workerId = Long.parseLong(parts[parts.length - 1]);
                result.put(workerId, getTodayLaborHours(workerId));
            } catch (Exception e) {
                log.warn("解析Redis key失败: {}", key, e);
            }
        }
        return result;
    }

    @Override
    public void deleteTodayLaborCache(Long workerId) {
        String key = getTodayKey(workerId);
        log.debug("删除工人[{}]当日工时缓存", workerId);
        redisTemplate.delete(key);
    }

    @Override
    public void refreshTodayLaborCache(Long workerId) {
        log.debug("刷新工人[{}]当日工时缓存", workerId);
        deleteTodayLaborCache(workerId);

        List<LaborRecord> todayRecords = laborRecordService.getTodayRecords(workerId);
        for (LaborRecord record : todayRecords) {
            BigDecimal overtime = record.getIsOvertime() != null && record.getIsOvertime() == 1
                    ? record.getWorkHours() : BigDecimal.ZERO;
            addTodayLaborHours(workerId, record.getLaborType(), record.getWorkHours(), overtime);
        }
    }

    private String getTodayKey(Long workerId) {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return KEY_PREFIX + dateStr + ":" + workerId;
    }

    private long getSecondsUntilNextDay2AM() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime next2AM = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(2, 0));
        return ChronoUnit.SECONDS.between(now, next2AM);
    }

    private BigDecimal toBigDecimal(Object obj) {
        if (obj == null) {
            return BigDecimal.ZERO;
        }
        if (obj instanceof BigDecimal) {
            return (BigDecimal) obj;
        }
        try {
            return new BigDecimal(obj.toString());
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    @Override
    public BigDecimal getTodayDirectHours(Long workerId) {
        return getTodayLaborHours(workerId).getDirectHours();
    }

    @Override
    public BigDecimal getTodayIndirectHours(Long workerId) {
        return getTodayLaborHours(workerId).getIndirectHours();
    }

    @Override
    public BigDecimal getTodayOvertimeHours(Long workerId) {
        return getTodayLaborHours(workerId).getOvertimeHours();
    }

    @Override
    public BigDecimal getTodayTotalHours(Long workerId) {
        return getTodayLaborHours(workerId).getTotalHours();
    }

    private Long toLong(Object obj) {
        if (obj == null) {
            return System.currentTimeMillis();
        }
        if (obj instanceof Long) {
            return (Long) obj;
        }
        try {
            return Long.parseLong(obj.toString());
        } catch (Exception e) {
            return System.currentTimeMillis();
        }
    }
}
