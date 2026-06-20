package com.laborhours.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.laborhours.entity.ProdProcess;
import com.laborhours.mapper.ProdProcessMapper;
import com.laborhours.service.ProdProcessService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ProdProcessServiceImpl extends ServiceImpl<ProdProcessMapper, ProdProcess> implements ProdProcessService {

    @Override
    public List<ProdProcess> listAll() {
        return list();
    }

    @Override
    public IPage<ProdProcess> pageList(Integer pageNum, Integer pageSize, ProdProcess process) {
        LambdaQueryWrapper<ProdProcess> wrapper = new LambdaQueryWrapper<>();
        if (process != null) {
            if (StringUtils.hasText(process.getProcessCode())) {
                wrapper.like(ProdProcess::getProcessCode, process.getProcessCode());
            }
            if (StringUtils.hasText(process.getProcessName())) {
                wrapper.like(ProdProcess::getProcessName, process.getProcessName());
            }
            if (process.getProcessType() != null) {
                wrapper.eq(ProdProcess::getProcessType, process.getProcessType());
            }
            if (process.getStatus() != null) {
                wrapper.eq(ProdProcess::getStatus, process.getStatus());
            }
        }
        wrapper.orderByAsc(ProdProcess::getSortOrder);
        wrapper.orderByDesc(ProdProcess::getCreateTime);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public ProdProcess getById(Long id) {
        return super.getById(id);
    }

    @Override
    public boolean add(ProdProcess process) {
        return save(process);
    }

    @Override
    public boolean update(ProdProcess process) {
        return updateById(process);
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }
}
