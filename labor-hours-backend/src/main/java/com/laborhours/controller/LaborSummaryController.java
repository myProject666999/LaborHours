package com.laborhours.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.laborhours.common.Result;
import com.laborhours.dto.LaborSummaryDTO;
import com.laborhours.entity.LaborDailySummary;
import com.laborhours.service.LaborSummaryService;
import com.laborhours.vo.OrderLaborSummaryVO;
import com.laborhours.vo.ProcessLaborSummaryVO;
import com.laborhours.vo.WorkOrderCompareVO;
import com.laborhours.vo.WorkerLaborSummaryVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/summary")
public class LaborSummaryController {

    private static final Logger log = LoggerFactory.getLogger(LaborSummaryController.class);

    @Autowired
    private LaborSummaryService laborSummaryService;

    @GetMapping("/list")
    public Result<List<LaborDailySummary>> list() {
        log.debug("查询所有工时日汇总");
        return Result.success(laborSummaryService.listAll());
    }

    @GetMapping("/page")
    public Result<IPage<LaborDailySummary>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            LaborDailySummary summary) {
        log.debug("分页查询工时日汇总, pageNum={}, pageSize={}", pageNum, pageSize);
        return Result.success(laborSummaryService.pageList(pageNum, pageSize, summary));
    }

    @GetMapping("/{id}")
    public Result<LaborDailySummary> getById(@PathVariable Long id) {
        log.debug("根据ID查询工时日汇总, id={}", id);
        return Result.success(laborSummaryService.getById(id));
    }

    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody LaborDailySummary summary) {
        log.debug("新增工时日汇总, summary={}", summary);
        return Result.success(laborSummaryService.add(summary));
    }

    @PostMapping("/update")
    public Result<Boolean> update(@RequestBody LaborDailySummary summary) {
        log.debug("更新工时日汇总, summary={}", summary);
        return Result.success(laborSummaryService.update(summary));
    }

    @PostMapping("/delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        log.debug("删除工时日汇总, id={}", id);
        return Result.success(laborSummaryService.delete(id));
    }

    @PostMapping("/byWorker")
    public Result<List<WorkerLaborSummaryVO>> summaryByWorker(@RequestBody(required = false) LaborSummaryDTO dto) {
        log.debug("按工人汇总工时, dto={}", dto);
        return Result.success(laborSummaryService.summaryByWorker(dto));
    }

    @PostMapping("/byOrder")
    public Result<List<OrderLaborSummaryVO>> summaryByOrder(@RequestBody(required = false) LaborSummaryDTO dto) {
        log.debug("按订单汇总工时, dto={}", dto);
        return Result.success(laborSummaryService.summaryByOrder(dto));
    }

    @PostMapping("/byProcess")
    public Result<List<ProcessLaborSummaryVO>> summaryByProcess(@RequestBody(required = false) LaborSummaryDTO dto) {
        log.debug("按工序汇总工时, dto={}", dto);
        return Result.success(laborSummaryService.summaryByProcess(dto));
    }

    @GetMapping("/byWorker/dateRange")
    public Result<List<LaborSummaryDTO>> summaryByWorkerDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        log.debug("按日期范围按工人汇总, startDate={}, endDate={}", startDate, endDate);
        return Result.success(laborSummaryService.summaryByWorker(startDate, endDate));
    }

    @GetMapping("/byOrder/dateRange")
    public Result<List<LaborSummaryDTO>> summaryByOrderDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        log.debug("按日期范围按订单汇总, startDate={}, endDate={}", startDate, endDate);
        return Result.success(laborSummaryService.summaryByOrder(startDate, endDate));
    }

    @GetMapping("/byProcess/dateRange")
    public Result<List<LaborSummaryDTO>> summaryByProcessDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        log.debug("按日期范围按工序汇总, startDate={}, endDate={}", startDate, endDate);
        return Result.success(laborSummaryService.summaryByProcess(startDate, endDate));
    }

    @GetMapping("/today/realtime")
    public Result<List<LaborSummaryDTO>> getTodayRealtimeSummary() {
        log.debug("获取今日实时工时汇总");
        return Result.success(laborSummaryService.getTodayRealtimeSummary());
    }

    @GetMapping("/worker/{workerId}/date/{date}")
    public Result<LaborSummaryDTO> summaryByWorkerAndDate(
            @PathVariable Long workerId,
            @PathVariable LocalDate date) {
        log.debug("查询工人指定日期工时汇总, workerId={}, date={}", workerId, date);
        return Result.success(laborSummaryService.summaryByWorkerAndDate(workerId, date));
    }

    @GetMapping("/compare/workOrder")
    public Result<List<WorkOrderCompareVO>> compareWorkOrder(
            @RequestParam(required = false) Long orderId) {
        log.debug("排产与实际工时对比, orderId={}", orderId);
        return Result.success(laborSummaryService.compareWorkOrder(orderId));
    }
}
