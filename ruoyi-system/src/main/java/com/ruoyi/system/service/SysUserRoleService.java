package com.ruoyi.system.service;

import java.util.List;

/**
 * 用户-角色关系服务
 */
public interface SysUserRoleService {

    void assignDefaultRole(Long userId);

    void updateUserRoles(Long userId, Long[] roleIds);

    List<Long> getUserRoleIds(Long userId);
}
