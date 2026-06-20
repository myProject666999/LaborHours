package com.laborhours.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.laborhours.common.Result;
import com.laborhours.dto.LaborReportDTO;
import com.laborhours.entity.LaborRecord;
import com.laborhours.service.LaborRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/laborRecord")
public class LaborRecordController {

    private static final Logger log = LoggerFactory.getLogger(LaborRecordController.class);

    @Autowired
    private LaborRecordService laborRecordService;

    @GetMapping("/list")
    public Result<List<LaborRecord>> list() {
        log.debug("查询所有报工记录");
        return Result.success(laborRecordService.listAll());
    }

    @GetMapping("/page")
    public Result<IPage<LaborRecord>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            LaborRecord record) {
        log.debug("分页查询报工记录, pageNum={}, pageSize={}", pageNum, pageSize);
        return Result.success(laborRecordService.pageList(pageNum, pageSize, record));
    }

    @GetMapping("/{id}")
    public Result<LaborRecord> getById(@PathVariable Long id) {
        log.debug("根据ID查询报工记录, id={}", id);
        return Result.success(laborRecordService.getById(id));
    }

    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody LaborRecord record) {
        log.debug("新增报工记录, record={}", record);
        return Result.success(laborRecordService.add(record));
    }

    @PostMapping("/update")
    public Result<Boolean> update(@RequestBody LaborRecord record) {
        log.debug("更新报工记录, record={}", record);
        return Result.success(laborRecordService.update(record));
    }

    @PostMapping("/delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        log.debug("删除报工记录, id={}", id);
        return Result.success(laborRecordService.delete(id));
    }

    @PostMapping("/report")
    public Result<LaborRecord> reportLabor(@RequestBody LaborReportDTO dto) {
        log.info("工人报工, workerId={}, workOrderId={}, laborType={}",
                dto.getWorkerId(), dto.getWorkOrderId(), dto.getLaborType());
        try {
            LaborRecord record = laborRecordService.reportLabor(dto);
            log.info("报工成功, recordNo={}, workHours={}", record.getRecordNo(), record.getWorkHours());
            return Result.success("报工成功", record);
        } catch (IllegalArgumentException e) {
            log.warn("报工校验失败: {}", e.getMessage());
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("报工失败", e);
            return Result.error("报工失败: " + e.getMessage());
        }
    }

    @GetMapping("/checkOverlap")
    public Result<Boolean> checkOverlap(
            @RequestParam Long workerId,
            @RequestParam LocalDateTime startTime,
            @RequestParam LocalDateTime endTime,
            @RequestParam(required = false) Long excludeId) {
        log.debug("检查时间重叠, workerId={}, startTime={}, endTime={}", workerId, startTime, endTime);
        boolean overlap = laborRecordService.checkTimeOverlap(workerId, startTime, endTime, excludeId);
        return Result.success(overlap);
    }

    @GetMapping("/today/{workerId}")
    public Result<List<LaborRecord>> getTodayRecords(@PathVariable Long workerId) {
        log.debug("查询工人今日报工记录, workerId={}", workerId);
        return Result.success(laborRecordService.getTodayRecords(workerId));
    }
}
