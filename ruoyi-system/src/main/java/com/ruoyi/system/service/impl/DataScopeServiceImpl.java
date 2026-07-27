package com.ruoyi.system.service.impl;

import com.ruoyi.common.datascope.context.DataScopeContext;
import com.ruoyi.common.datascope.service.DataScopeService;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.system.domain.enums.DataScopeType;
import com.ruoyi.system.domain.model.RoleDataScopeModel;
import com.ruoyi.system.mapper.SysRoleMapper;
import com.ruoyi.system.mapper.SysUserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 数据权限服务实现
 */
@Service
public class DataScopeServiceImpl implements DataScopeService {

    private static final Logger log = LoggerFactory.getLogger(DataScopeServiceImpl.class);

    private final SysRoleMapper roleMapper;
    private final SysUserMapper userMapper;

    public DataScopeServiceImpl(SysRoleMapper roleMapper,
                                SysUserMapper userMapper) {
        this.roleMapper = roleMapper;
        this.userMapper = userMapper;
    }

    @Override
    public DataScopeContext buildDataScopeCondition(String alias,
                                                    String userIdColumn,
                                                    String deptIdColumn,
                                                    boolean enableUserScope) {
        try {
            String currentUserId = SecurityUtils.getCurrentUserId();
            if (currentUserId == null || !currentUserId.matches("\\d+")) {
                return DataScopeContext.denyAll();
            }

            Long userId = Long.parseLong(currentUserId);
            List<RoleDataScopeModel> roles = roleMapper.selectRoleDataScopesByUserId(userId);
            if (roles.isEmpty()) {
                return DataScopeContext.denyAll();
            }

            Set<String> conditions = new LinkedHashSet<>();
            Long userDeptId = null;
            for (RoleDataScopeModel role : roles) {
                DataScopeType scopeType = DataScopeType.fromCode(role.getDataScope());
                if (scopeType == null) {
                    continue;
                }

                if (scopeType == DataScopeType.ALL) {
                    return DataScopeContext.allowAll();
                }
                if (needsDeptId(scopeType) && userDeptId == null) {
                    userDeptId = getUserDeptId(userId);
                }
                appendConditionByScope(conditions, alias, userIdColumn, deptIdColumn,
                        enableUserScope, userId, userDeptId, role.getRoleId(), scopeType);
            }

            if (!conditions.isEmpty()) {
                return DataScopeContext.ofCondition(String.join(" OR ", conditions));
            }
            return DataScopeContext.denyAll();
        } catch (RuntimeException e) {
            log.error("数据权限构建异常: alias={}, userIdColumn={}, deptIdColumn={}",
                    alias, userIdColumn, deptIdColumn, e);
            return DataScopeContext.denyAll();
        }
    }

    private Long getUserDeptId(Long userId) {
        Long deptId = userMapper.selectDeptIdByUserId(userId);
        if (deptId == null) {
            log.warn("获取用户部门ID为空: userId={}", userId);
        }
        return deptId;
    }

    private boolean needsDeptId(DataScopeType scopeType) {
        return scopeType == DataScopeType.DEPT || scopeType == DataScopeType.DEPT_AND_CHILD;
    }

    private void appendConditionByScope(Set<String> conditions,
                                        String alias,
                                        String userIdColumn,
                                        String deptIdColumn,
                                        boolean enableUserScope,
                                        Long userId,
                                        Long userDeptId,
                                        Long roleId,
                                        DataScopeType scopeType) {
        switch (scopeType) {
            case CUSTOM:
                addCondition(conditions, buildColumn(alias, deptIdColumn) + " IN ("
                        + "SELECT rd.dept_id FROM sys_role_dept rd WHERE rd.role_id = " + roleId + ")");
                break;
            case DEPT:
                if (userDeptId != null) {
                    addCondition(conditions, buildColumn(alias, deptIdColumn) + " = " + userDeptId);
                }
                break;
            case DEPT_AND_CHILD:
                if (userDeptId != null) {
                    addCondition(conditions, buildColumn(alias, deptIdColumn) + " IN ("
                            + "SELECT dept_id FROM sys_dept WHERE dept_id = " + userDeptId
                            + " OR FIND_IN_SET(" + userDeptId + ", ancestors))");
                }
                break;
            case SELF:
                if (enableUserScope && StringUtils.hasText(userIdColumn)) {
                    addCondition(conditions, buildColumn(alias, userIdColumn) + " = " + userId);
                }
                break;
            default:
                break;
        }
    }

    private String buildColumn(String alias, String column) {
        if (!StringUtils.hasText(alias)) {
            return column;
        }
        return alias.endsWith(".") ? alias + column : alias + "." + column;
    }

    private void addCondition(Set<String> conditions, String condition) {
        if (StringUtils.hasText(condition)) {
            conditions.add(condition);
        }
    }
}
