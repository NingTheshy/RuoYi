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

/**
 * 数据权限服务
 * <p>
 * 根据当前登录用户的角色 data_scope 值，动态构建 SQL 过滤条件。
 * 由 {@link com.ruoyi.common.core.aspect.DataScopeAspect} 切面调用。
 * </p>
 *
 * <p>数据权限级别（sys_role.data_scope）：</p>
 * <ul>
 *   <li>1 - 全部数据：返回 null 条件（不过滤）</li>
 *   <li>2 - 自定义数据：查询 sys_role_dept 获取允许的部门 ID 列表</li>
 *   <li>3 - 本部门数据：只看用户所属部门</li>
 *   <li>4 - 本部门及以下：用户部门 + 所有子部门（通过 ancestors 字段匹配）</li>
 *   <li>5 - 仅本人数据：只看 user_id = 当前用户 ID 的记录</li>
 * </ul>
 *
 * <p>安全策略：用户 ID 必须为纯数字，查询失败时返回 "AND 1=0"（不泄露数据）。</p>
 *
 * @author NingTheshy
 */
@Service
public class DataScopeService {

    private static final Logger log = LoggerFactory.getLogger(DataScopeService.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 构建数据权限 SQL 条件
     *
     * @param alias        表别名前缀（如 "u." 或 ""）
     * @param userIdColumn 用户 ID 列名（scope=5 时使用）
     * @param deptIdColumn 部门 ID 列名（scope=2/3/4 时使用）
     * @return DataScopeParams 包含 SQL 条件片段，null 表示不过滤
     */
    public DataScopeParams buildDataScopeCondition(String alias, String userIdColumn, String deptIdColumn) {
        try {
            // 获取当前登录用户 ID
            String userId = getCurrentUserId();
            if (userId == null || !userId.matches("\\d+")) {
                // 用户 ID 无效，拒绝所有数据（fail closed）
                return new DataScopeParams("AND 1=0");
            }

            // 查询用户所有角色的 data_scope 值
            String roleSql = "SELECT r.role_id, r.data_scope FROM sys_role r "
                    + "INNER JOIN sys_user_role ur ON r.role_id = ur.role_id "
                    + "WHERE ur.user_id = ? AND r.status = '0' AND r.del_flag = '0'";
            List<Map<String, Object>> roles = jdbcTemplate.queryForList(roleSql, userId);

            if (roles.isEmpty()) {
                // 无角色，不允许查看任何数据
                return new DataScopeParams("AND 1=0");
            }

            // 检查是否有全部数据权限（scope=1）
            for (Map<String, Object> role : roles) {
                String scope = String.valueOf(role.get("data_scope"));
                if ("1".equals(scope)) {
                    return new DataScopeParams(null); // 不过滤
                }
            }

            // 获取用户所属部门 ID
            Long userDeptId = getUserDeptId(userId);

            // 按 scope 类型构建 OR 条件
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

    /**
     * 获取当前登录用户的 ID
     * <p>从 Spring Security 上下文中获取</p>
     *
     * @return 用户 ID 字符串，未认证时返回 null
     */
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

    /**
     * 查询用户所属的部门 ID
     *
     * @param userId 用户 ID
     * @return 部门 ID，查询失败时返回 null
     */
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

    /**
     * 追加 OR 条件到 SQL 构建器
     * <p>如果不是第一个条件，先追加 " OR " 分隔符</p>
     *
     * @param sb        SQL 条件构建器
     * @param condition 要追加的 SQL 条件片段
     */
    private void appendOrCondition(StringBuilder sb, String condition) {
        if (sb.length() > 0) {
            sb.append(" OR ");
        }
        sb.append(condition);
    }
}
