package com.laborhours.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.laborhours.entity.SysWorker;

import java.util.List;

public interface SysWorkerService extends IService<SysWorker> {

    List<SysWorker> listAll();

    IPage<SysWorker> pageList(Integer pageNum, Integer pageSize, SysWorker worker);

    SysWorker getById(Long id);

    boolean add(SysWorker worker);

    boolean update(SysWorker worker);

    boolean delete(Long id);
}
