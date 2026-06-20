package com.laborhours.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.laborhours.entity.SysTeam;
import com.laborhours.mapper.SysTeamMapper;
import com.laborhours.service.SysTeamService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class SysTeamServiceImpl extends ServiceImpl<SysTeamMapper, SysTeam> implements SysTeamService {

    @Override
    public List<SysTeam> listAll() {
        return list();
    }

    @Override
    public IPage<SysTeam> pageList(Integer pageNum, Integer pageSize, SysTeam team) {
        LambdaQueryWrapper<SysTeam> wrapper = new LambdaQueryWrapper<>();
        if (team != null) {
            if (StringUtils.hasText(team.getTeamNo())) {
                wrapper.like(SysTeam::getTeamNo, team.getTeamNo());
            }
            if (StringUtils.hasText(team.getTeamName())) {
                wrapper.like(SysTeam::getTeamName, team.getTeamName());
            }
            if (StringUtils.hasText(team.getWorkshop())) {
                wrapper.like(SysTeam::getWorkshop, team.getWorkshop());
            }
            if (team.getStatus() != null) {
                wrapper.eq(SysTeam::getStatus, team.getStatus());
            }
        }
        wrapper.orderByDesc(SysTeam::getCreateTime);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public SysTeam getById(Long id) {
        return super.getById(id);
    }

    @Override
    public boolean add(SysTeam team) {
        return save(team);
    }

    @Override
    public boolean update(SysTeam team) {
        return updateById(team);
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }
}
