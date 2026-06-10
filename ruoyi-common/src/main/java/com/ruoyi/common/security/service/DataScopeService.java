package com.ruoyi.common.security.service;

import com.ruoyi.common.core.domain.DataScopeParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Service
public class DataScopeService {

    private static final Logger log = LoggerFactory.getLogger(DataScopeService.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public DataScopeParams buildDataScopeCondition(String alias, String userIdColumn, String deptIdColumn) {
        try {
            String userId = getCurrentUserId();
            if (userId == null) {
                return new DataScopeParams(null);
            }

            // 查询用户所有角色的 data_scope
            String roleSql = "SELECT r.role_id, r.data_scope FROM sys_role r "
                    + "INNER JOIN sys_user_role ur ON r.role_id = ur.role_id "
                    + "WHERE ur.user_id = ? AND r.status = '0' AND r.del_flag = '0'";
            List<Map<String, Object>> roles = jdbcTemplate.queryForList(roleSql, userId);

            if (roles.isEmpty()) {
                // 无角色，不允许查看任何数据
                return new DataScopeParams("AND 1=0");
            }

            // 检查是否有全部数据权限
            for (Map<String, Object> role : roles) {
                String scope = String.valueOf(role.get("data_scope"));
                if ("1".equals(scope)) {
                    return new DataScopeParams(null); // 不过滤
                }
            }

            // 获取用户部门ID
            Long userDeptId = getUserDeptId(userId);

            // 按 scope 类型构建条件
            StringBuilder condition = new StringBuilder();

            for (Map<String, Object> role : roles) {
                String scope = String.valueOf(role.get("data_scope"));
                switch (scope) {
                    case "2": // 自定义数据权限
                        String customDeptCondition = alias + deptIdColumn + " IN ("
                                + "SELECT rd.dept_id FROM sys_role_dept rd "
                                + "WHERE rd.role_id IN ("
                                + "SELECT ur2.role_id FROM sys_user_role ur2 "
                                + "INNER JOIN sys_role r2 ON ur2.role_id = r2.role_id "
                                + "WHERE ur2.user_id = " + userId + " AND r2.data_scope = '2' "
                                + "AND r2.status = '0' AND r2.del_flag = '0'"
                                + "))";
                        appendOrCondition(condition, customDeptCondition);
                        break;
                    case "3": // 本部门数据权限
                        if (userDeptId != null) {
                            appendOrCondition(condition, alias + deptIdColumn + " = " + userDeptId);
                        }
                        break;
                    case "4": // 本部门及以下数据权限
                        if (userDeptId != null) {
                            String childrenCondition = alias + deptIdColumn + " IN ("
                                    + "SELECT dept_id FROM sys_dept WHERE FIND_IN_SET(" + userDeptId + ", ancestors))";
                            appendOrCondition(condition, childrenCondition);
                        }
                        break;
                    case "5": // 仅本人数据权限
                        appendOrCondition(condition, alias + userIdColumn + " = " + userId);
                        break;
                    default:
                        break;
                }
            }

            if (condition.length() > 0) {
                return new DataScopeParams("AND (" + condition + ")");
            }

            // 未生成有效条件（如 deptId 为 null 导致 dept 相关 scope 都跳过）
            return new DataScopeParams("AND 1=0");

        } catch (Exception e) {
            log.error("数据权限解析异常: {}", e.getMessage());
            // fail closed：查询失败时不泄露数据
            return new DataScopeParams("AND 1=0");
        }
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof String) {
                return (String) principal;
            }
        }
        return null;
    }

    private Long getUserDeptId(String userId) {
        try {
            String sql = "SELECT dept_id FROM sys_user WHERE user_id = ?";
            Long deptId = jdbcTemplate.queryForObject(sql, Long.class, userId);
            return deptId;
        } catch (Exception e) {
            log.warn("获取用户部门ID失败: {}", e.getMessage());
            return null;
        }
    }

    private void appendOrCondition(StringBuilder sb, String condition) {
        if (sb.length() > 0) {
            sb.append(" OR ");
        }
        sb.append(condition);
    }
}
