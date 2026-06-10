package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.system.domain.entity.SysRole;
import com.ruoyi.system.mapper.SysRoleMapper;
import com.ruoyi.system.service.ISysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {

    @Override
    public List<SysRole> selectRoleList(SysRole role) {
        return baseMapper.selectRoleList(role);
    }

    @Override
    public Page<SysRole> selectRolePage(Page<SysRole> page, SysRole query) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getRoleName()), SysRole::getRoleName, query.getRoleName())
                .like(StringUtils.hasText(query.getRoleKey()), SysRole::getRoleKey, query.getRoleKey())
                .like(StringUtils.hasText(query.getStatus()), SysRole::getStatus, query.getStatus())
                .orderByAsc(SysRole::getRoleSort);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public SysRole selectRoleById(Long roleId) {
        return getById(roleId);
    }

    @Override
    @Transactional
    public int insertRole(SysRole role, Long[] menuIds) {
        save(role);
        if (menuIds != null && menuIds.length > 0) {
            baseMapper.insertRoleMenu(role.getRoleId(), menuIds);
        }
        return 1;
    }

    @Override
    @Transactional
    public int updateRole(SysRole role, Long[] menuIds) {
        updateById(role);
        baseMapper.deleteRoleMenuByRoleIds(new Long[]{role.getRoleId()});
        if (menuIds != null && menuIds.length > 0) {
            baseMapper.insertRoleMenu(role.getRoleId(), menuIds);
        }
        return 1;
    }

    @Override
    @Transactional
    public int deleteRoleByIds(Long[] roleIds) {
        Arrays.stream(roleIds).forEach(roleId -> {
            if (roleId == 1L) {
                throw new ServiceException("不允许删除超级管理员角色");
            }
        });
        baseMapper.deleteRoleMenuByRoleIds(roleIds);
        return removeByIds(Arrays.asList(roleIds)) ? roleIds.length : 0;
    }

    @Override
    public Set<String> selectRoleKeysByUserId(Long userId) {
        List<String> roleKeys = baseMapper.selectRoleKeysByUserId(userId);
        return new HashSet<>(roleKeys);
    }
}
