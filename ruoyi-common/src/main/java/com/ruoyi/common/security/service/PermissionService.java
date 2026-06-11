package com.ruoyi.common.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 权限查询服务
 * <p>
 * 使用 JdbcTemplate 直接查询数据库，获取用户的权限标识和角色标识。
 * 主要被 {@link com.ruoyi.common.security.filter.JwtAuthenticationFilter} 调用，
 * 在每次请求时加载当前用户的权限到 Spring Security 上下文。
 * </p>
 *
 * <p>注意：此处使用 JdbcTemplate 而非 MyBatis Mapper，
 * 是因为此服务位于 common 模块，不应依赖 system 模块的 Mapper 接口。</p>
 *
 * @author NingTheshy
 */
@Service
public class PermissionService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取用户的权限标识集合
     * <p>
     * 通过用户-角色-菜单的关联查询，获取所有启用菜单的 perms 字段。
     * </p>
     *
     * @param userId 用户 ID
     * @return 权限标识集合（如 ["system:user:list", "system:role:list"]）
     */
    public Set<String> getPermsByUserId(Long userId) {
        String sql = "SELECT DISTINCT m.perms FROM sys_menu m "
                + "INNER JOIN sys_role_menu rm ON m.menu_id = rm.menu_id "
                + "INNER JOIN sys_user_role ur ON rm.role_id = ur.role_id "
                + "WHERE ur.user_id = ? AND m.perms IS NOT NULL AND m.perms != '' AND m.status = '0'";
        List<String> perms = jdbcTemplate.queryForList(sql, String.class, userId);
        return perms.stream().filter(p -> !p.isEmpty()).collect(Collectors.toSet());
    }

    /**
     * 获取用户的角色标识集合
     * <p>
     * 查询用户关联的所有启用角色的 role_key。
     * </p>
     *
     * @param userId 用户 ID
     * @return 角色标识集合（如 ["admin", "common"]）
     */
    public Set<String> getRoleKeysByUserId(Long userId) {
        String sql = "SELECT DISTINCT r.role_key FROM sys_role r "
                + "INNER JOIN sys_user_role ur ON r.role_id = ur.role_id "
                + "WHERE ur.user_id = ? AND r.status = '0'";
        List<String> roleKeys = jdbcTemplate.queryForList(sql, String.class, userId);
        return roleKeys.stream().filter(r -> !r.isEmpty()).collect(Collectors.toSet());
    }
}
