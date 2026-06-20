package com.laborhours.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.laborhours.dto.LaborSummaryDTO;
import com.laborhours.entity.LaborDailySummary;
import com.laborhours.vo.OrderLaborSummaryVO;
import com.laborhours.vo.ProcessLaborSummaryVO;
import com.laborhours.vo.WorkOrderCompareVO;
import com.laborhours.vo.WorkerLaborSummaryVO;

import java.time.LocalDate;
import java.util.List;

public interface LaborSummaryService extends IService<LaborDailySummary> {

    List<LaborDailySummary> listAll();

    IPage<LaborDailySummary> pageList(Integer pageNum, Integer pageSize, LaborDailySummary summary);

    LaborDailySummary getById(Long id);

    boolean add(LaborDailySummary summary);

    boolean update(LaborDailySummary summary);

    boolean delete(Long id);

    List<WorkerLaborSummaryVO> summaryByWorker(LaborSummaryDTO dto);

    List<OrderLaborSummaryVO> summaryByOrder(LaborSummaryDTO dto);

    List<ProcessLaborSummaryVO> summaryByProcess(LaborSummaryDTO dto);

    List<WorkOrderCompareVO> compareWorkOrder(Long orderId);

    List<LaborSummaryDTO> summaryByWorker(LocalDate startDate, LocalDate endDate);

    List<LaborSummaryDTO> summaryByOrder(LocalDate startDate, LocalDate endDate);

    List<LaborSummaryDTO> summaryByProcess(LocalDate startDate, LocalDate endDate);

    List<LaborSummaryDTO> getTodayRealtimeSummary();

    LaborSummaryDTO summaryByWorkerAndDate(Long workerId, LocalDate date);
}
