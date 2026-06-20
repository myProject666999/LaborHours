package com.laborhours.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.laborhours.entity.SysWorker;
import com.laborhours.mapper.SysWorkerMapper;
import com.laborhours.service.SysWorkerService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class SysWorkerServiceImpl extends ServiceImpl<SysWorkerMapper, SysWorker> implements SysWorkerService {

    @Override
    public List<SysWorker> listAll() {
        return list();
    }

    @Override
    public IPage<SysWorker> pageList(Integer pageNum, Integer pageSize, SysWorker worker) {
        LambdaQueryWrapper<SysWorker> wrapper = new LambdaQueryWrapper<>();
        if (worker != null) {
            if (StringUtils.hasText(worker.getWorkerNo())) {
                wrapper.like(SysWorker::getWorkerNo, worker.getWorkerNo());
            }
            if (StringUtils.hasText(worker.getWorkerName())) {
                wrapper.like(SysWorker::getWorkerName, worker.getWorkerName());
            }
            if (worker.getTeamId() != null) {
                wrapper.eq(SysWorker::getTeamId, worker.getTeamId());
            }
            if (worker.getStatus() != null) {
                wrapper.eq(SysWorker::getStatus, worker.getStatus());
            }
        }
        wrapper.orderByDesc(SysWorker::getCreateTime);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public SysWorker getById(Long id) {
        return super.getById(id);
    }

    @Override
    public boolean add(SysWorker worker) {
        return save(worker);
    }

    @Override
    public boolean update(SysWorker worker) {
        return updateById(worker);
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }
}
