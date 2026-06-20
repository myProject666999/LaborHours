package com.laborhours.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.laborhours.entity.ProdWorkOrder;

import java.util.List;

public interface ProdWorkOrderService extends IService<ProdWorkOrder> {

    List<ProdWorkOrder> listAll();

    IPage<ProdWorkOrder> pageList(Integer pageNum, Integer pageSize, ProdWorkOrder workOrder);

    ProdWorkOrder getById(Long id);

    boolean add(ProdWorkOrder workOrder);

    boolean update(ProdWorkOrder workOrder);

    boolean delete(Long id);
}
