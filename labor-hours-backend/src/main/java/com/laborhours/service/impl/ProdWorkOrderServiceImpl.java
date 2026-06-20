package com.laborhours.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.laborhours.entity.ProdWorkOrder;
import com.laborhours.mapper.ProdWorkOrderMapper;
import com.laborhours.service.ProdWorkOrderService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ProdWorkOrderServiceImpl extends ServiceImpl<ProdWorkOrderMapper, ProdWorkOrder> implements ProdWorkOrderService {

    @Override
    public List<ProdWorkOrder> listAll() {
        return list();
    }

    @Override
    public IPage<ProdWorkOrder> pageList(Integer pageNum, Integer pageSize, ProdWorkOrder workOrder) {
        LambdaQueryWrapper<ProdWorkOrder> wrapper = new LambdaQueryWrapper<>();
        if (workOrder != null) {
            if (StringUtils.hasText(workOrder.getWorkOrderNo())) {
                wrapper.like(ProdWorkOrder::getWorkOrderNo, workOrder.getWorkOrderNo());
            }
            if (workOrder.getOrderId() != null) {
                wrapper.eq(ProdWorkOrder::getOrderId, workOrder.getOrderId());
            }
            if (workOrder.getProcessId() != null) {
                wrapper.eq(ProdWorkOrder::getProcessId, workOrder.getProcessId());
            }
            if (workOrder.getAssignWorkerId() != null) {
                wrapper.eq(ProdWorkOrder::getAssignWorkerId, workOrder.getAssignWorkerId());
            }
            if (workOrder.getStatus() != null) {
                wrapper.eq(ProdWorkOrder::getStatus, workOrder.getStatus());
            }
        }
        wrapper.orderByDesc(ProdWorkOrder::getCreateTime);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public ProdWorkOrder getById(Long id) {
        return super.getById(id);
    }

    @Override
    public boolean add(ProdWorkOrder workOrder) {
        return save(workOrder);
    }

    @Override
    public boolean update(ProdWorkOrder workOrder) {
        return updateById(workOrder);
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }
}
