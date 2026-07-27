package com.ruoyi.system.service.impl;

import com.ruoyi.system.mapper.SysRoleMapper;
import com.ruoyi.system.service.SysRoleRelationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 角色关系服务实现
 */
@Service
public class SysRoleRelationServiceImpl implements SysRoleRelationService {

    private final SysRoleMapper roleMapper;

    public SysRoleRelationServiceImpl(SysRoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    @Override
    @Transactional
    public void replaceRoleMenus(Long roleId, Long[] menuIds) {
        roleMapper.deleteRoleMenuByRoleIds(new Long[]{roleId});
        if (menuIds != null && menuIds.length > 0) {
            roleMapper.insertRoleMenu(roleId, menuIds);
        }
    }

    @Override
    @Transactional
    public void deleteRoleRelations(Long[] roleIds) {
        roleMapper.deleteRoleMenuByRoleIds(roleIds);
        roleMapper.deleteUserRoleByRoleIds(roleIds);
    }
}
