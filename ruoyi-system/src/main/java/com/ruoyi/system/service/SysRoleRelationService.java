package com.ruoyi.system.service;

/**
 * 角色关系服务
 */
public interface SysRoleRelationService {

    void replaceRoleMenus(Long roleId, Long[] menuIds);

    void deleteRoleRelations(Long[] roleIds);
}
