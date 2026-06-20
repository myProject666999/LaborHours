package com.laborhours.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.laborhours.entity.ProdProcess;

import java.util.List;

public interface ProdProcessService extends IService<ProdProcess> {

    List<ProdProcess> listAll();

    IPage<ProdProcess> pageList(Integer pageNum, Integer pageSize, ProdProcess process);

    ProdProcess getById(Long id);

    boolean add(ProdProcess process);

    boolean update(ProdProcess process);

    boolean delete(Long id);
}
