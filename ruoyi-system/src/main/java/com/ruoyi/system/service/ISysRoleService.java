package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.entity.SysRole;

import java.util.List;
import java.util.Set;

/**
 * 角色业务服务接口
 * <p>
 * 定义角色管理的业务方法，包括 CRUD、分页查询和角色-菜单关联操作。
 * </p>
 *
 * @author NingTheshy
 */
public interface ISysRoleService {

    /**
     * 查询角色列表
     *
     * @param role 查询条件
     * @return 角色列表
     */
    List<SysRole> getRoleList(SysRole role);

    /**
     * 分页查询角色列表
     *
     * @param page  分页参数
     * @param query 查询条件
     * @return 分页结果
     */
    Page<SysRole> getRolePage(Page<SysRole> page, SysRole query);

    /**
     * 根据 ID 查询角色详情
     *
     * @param roleId 角色 ID
     * @return 角色实体
     */
    SysRole getRoleById(Long roleId);

    /**
     * 新增角色（含菜单关联）
     *
     * @param role    角色实体
     * @param menuIds 菜单 ID 数组
     * @return 影响行数
     */
    int createRole(SysRole role, Long[] menuIds);

    /**
     * 修改角色（含菜单关联）
     * <p>先更新角色信息，再重建角色-菜单关联</p>
     *
     * @param role    角色实体（必须包含 roleId）
     * @param menuIds 菜单 ID 数组
     * @return 影响行数
     */
    int updateRole(SysRole role, Long[] menuIds);

    /**
     * 批量删除角色
     * <p>超级管理员角色（ID=1）不可删除，同时清除角色-菜单关联</p>
     *
     * @param roleIds 角色 ID 数组
     * @return 影响行数
     */
    int deleteRoleByIds(Long[] roleIds);

    /**
     * 查询用户的角色标识集合
     * <p>用于 JWT 认证过滤器加载用户角色</p>
     *
     * @param userId 用户 ID
     * @return 角色标识集合
     */
    Set<String> getRoleKeysByUserId(Long userId);
}
