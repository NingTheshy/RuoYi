package com.ruoyi.system.service.impl;

import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.system.domain.entity.SysRole;
import com.ruoyi.system.mapper.SysRoleMapper;
import com.ruoyi.system.service.ISysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SysRoleServiceImpl implements ISysRoleService {

    @Autowired
    private SysRoleMapper roleMapper;

    @Override
    public List<SysRole> selectRoleList(SysRole role) {
        return roleMapper.selectRoleList(role);
    }

    @Override
    public SysRole selectRoleById(Long roleId) {
        return roleMapper.selectRoleById(roleId);
    }

    @Override
    @Transactional
    public int insertRole(SysRole role, Long[] menuIds) {
        roleMapper.insertRole(role);
        if (menuIds != null && menuIds.length > 0) {
            roleMapper.insertRoleMenu(role.getRoleId(), menuIds);
        }
        return 1;
    }

    @Override
    @Transactional
    public int updateRole(SysRole role, Long[] menuIds) {
        roleMapper.updateRole(role);
        roleMapper.deleteRoleMenuByRoleIds(new Long[]{role.getRoleId()});
        if (menuIds != null && menuIds.length > 0) {
            roleMapper.insertRoleMenu(role.getRoleId(), menuIds);
        }
        return 1;
    }

    @Override
    @Transactional
    public int deleteRoleByIds(Long[] roleIds) {
        for (Long roleId : roleIds) {
            if (roleId == 1L) {
                throw new ServiceException("不允许删除超级管理员角色");
            }
        }
        roleMapper.deleteRoleMenuByRoleIds(roleIds);
        return roleMapper.deleteRoleByIds(roleIds);
    }

    @Override
    public Set<String> selectRoleKeysByUserId(Long userId) {
        List<String> roleKeys = roleMapper.selectRoleKeysByUserId(userId);
        return new HashSet<>(roleKeys);
    }
}
