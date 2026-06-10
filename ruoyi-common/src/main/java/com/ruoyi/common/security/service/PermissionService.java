package com.ruoyi.common.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PermissionService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Set<String> getPermsByUserId(Long userId) {
        String sql = "SELECT DISTINCT m.perms FROM sys_menu m "
                + "INNER JOIN sys_role_menu rm ON m.menu_id = rm.menu_id "
                + "INNER JOIN sys_user_role ur ON rm.role_id = ur.role_id "
                + "WHERE ur.user_id = ? AND m.perms IS NOT NULL AND m.perms != '' AND m.status = '0'";
        List<String> perms = jdbcTemplate.queryForList(sql, String.class, userId);
        return perms.stream().filter(p -> !p.isEmpty()).collect(Collectors.toSet());
    }

    public Set<String> getRoleKeysByUserId(Long userId) {
        String sql = "SELECT DISTINCT r.role_key FROM sys_role r "
                + "INNER JOIN sys_user_role ur ON r.role_id = ur.role_id "
                + "WHERE ur.user_id = ? AND r.status = '0'";
        List<String> roleKeys = jdbcTemplate.queryForList(sql, String.class, userId);
        return roleKeys.stream().filter(r -> !r.isEmpty()).collect(Collectors.toSet());
    }
}
