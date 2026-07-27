package com.ruoyi.system.service.impl;

import com.ruoyi.common.security.service.PermissionService;
import com.ruoyi.system.service.SysMenuService;
import com.ruoyi.system.service.SysRoleService;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * 权限查询服务实现
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    private final SysMenuService menuService;
    private final SysRoleService roleService;

    public PermissionServiceImpl(SysMenuService menuService,
                                 SysRoleService roleService) {
        this.menuService = menuService;
        this.roleService = roleService;
    }

    @Override
    public Set<String> getPermsByUserId(Long userId) {
        return menuService.getMenuPermsByUserId(userId);
    }

    @Override
    public Set<String> getRoleKeysByUserId(Long userId) {
        return roleService.getRoleKeysByUserId(userId);
    }
}
