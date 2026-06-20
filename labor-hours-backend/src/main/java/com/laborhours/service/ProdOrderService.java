package com.laborhours.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.laborhours.entity.ProdOrder;

import java.util.List;

public interface ProdOrderService extends IService<ProdOrder> {

    List<ProdOrder> listAll();

    IPage<ProdOrder> pageList(Integer pageNum, Integer pageSize, ProdOrder order);

    ProdOrder getById(Long id);

    boolean add(ProdOrder order);

    boolean update(ProdOrder order);

    boolean delete(Long id);
}
