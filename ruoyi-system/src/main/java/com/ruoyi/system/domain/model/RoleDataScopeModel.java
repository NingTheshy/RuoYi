package com.ruoyi.system.domain.model;

/**
 * 角色数据权限模型
 */
public class RoleDataScopeModel {

    private Long roleId;

    private String dataScope;

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getDataScope() {
        return dataScope;
    }

    public void setDataScope(String dataScope) {
        this.dataScope = dataScope;
    }
}
