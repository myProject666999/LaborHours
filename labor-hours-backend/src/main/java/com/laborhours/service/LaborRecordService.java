package com.laborhours.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.laborhours.dto.LaborReportDTO;
import com.laborhours.entity.LaborRecord;

import java.time.LocalDateTime;
import java.util.List;

public interface LaborRecordService extends IService<LaborRecord> {

    List<LaborRecord> listAll();

    IPage<LaborRecord> pageList(Integer pageNum, Integer pageSize, LaborRecord record);

    LaborRecord getById(Long id);

    boolean add(LaborRecord record);

    boolean update(LaborRecord record);

    boolean delete(Long id);

    LaborRecord reportLabor(LaborReportDTO dto);

    boolean checkTimeOverlap(Long workerId, LocalDateTime startTime, LocalDateTime endTime, Long excludeId);

    List<LaborRecord> getTodayRecords(Long workerId);
}
