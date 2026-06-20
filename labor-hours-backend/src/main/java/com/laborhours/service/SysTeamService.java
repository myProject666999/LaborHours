package com.laborhours.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.laborhours.entity.SysTeam;

import java.util.List;

public interface SysTeamService extends IService<SysTeam> {

    List<SysTeam> listAll();

    IPage<SysTeam> pageList(Integer pageNum, Integer pageSize, SysTeam team);

    SysTeam getById(Long id);

    boolean add(SysTeam team);

    boolean update(SysTeam team);

    boolean delete(Long id);
}
