package com.laborhours.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.laborhours.entity.ProdOrder;
import com.laborhours.mapper.ProdOrderMapper;
import com.laborhours.service.ProdOrderService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ProdOrderServiceImpl extends ServiceImpl<ProdOrderMapper, ProdOrder> implements ProdOrderService {

    @Override
    public List<ProdOrder> listAll() {
        return list();
    }

    @Override
    public IPage<ProdOrder> pageList(Integer pageNum, Integer pageSize, ProdOrder order) {
        LambdaQueryWrapper<ProdOrder> wrapper = new LambdaQueryWrapper<>();
        if (order != null) {
            if (StringUtils.hasText(order.getOrderNo())) {
                wrapper.like(ProdOrder::getOrderNo, order.getOrderNo());
            }
            if (StringUtils.hasText(order.getOrderName())) {
                wrapper.like(ProdOrder::getOrderName, order.getOrderName());
            }
            if (StringUtils.hasText(order.getProductName())) {
                wrapper.like(ProdOrder::getProductName, order.getProductName());
            }
            if (order.getStatus() != null) {
                wrapper.eq(ProdOrder::getStatus, order.getStatus());
            }
        }
        wrapper.orderByDesc(ProdOrder::getCreateTime);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public ProdOrder getById(Long id) {
        return super.getById(id);
    }

    @Override
    public boolean add(ProdOrder order) {
        return save(order);
    }

    @Override
    public boolean update(ProdOrder order) {
        return updateById(order);
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }
}
