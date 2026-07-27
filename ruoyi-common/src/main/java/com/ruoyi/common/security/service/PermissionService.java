package com.ruoyi.common.security.service;

import java.util.Set;

/**
 * 权限查询服务
 */
public interface PermissionService {

    /**
     * 获取用户的权限标识集合
     *
     * @param userId 用户 ID
     * @return 权限标识集合
     */
    Set<String> getPermsByUserId(Long userId);

    /**
     * 获取用户的角色标识集合
     *
     * @param userId 用户 ID
     * @return 角色标识集合
     */
    Set<String> getRoleKeysByUserId(Long userId);
}
