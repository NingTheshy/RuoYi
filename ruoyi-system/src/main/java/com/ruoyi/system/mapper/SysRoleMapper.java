package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.SysRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色数据访问层
 * <p>
 * 继承 MyBatis-Plus 的 {@link BaseMapper}，提供基础 CRUD 方法。
 * 自定义查询方法对应 SysRoleMapper.xml 中的 SQL 语句。
 * </p>
 *
 * @author NingTheshy
 */
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * 查询角色列表（支持条件筛选）
     *
     * @param role 查询条件（可选：roleName、roleKey、status）
     * @return 角色列表，按 roleSort 排序
     */
    List<SysRole> selectRoleList(SysRole role);

    /**
     * 批量删除角色-菜单关联
     * <p>删除指定角色的所有菜单关联记录</p>
     *
     * @param roleIds 角色 ID 数组
     * @return 删除的记录数
     */
    int deleteRoleMenuByRoleIds(Long[] roleIds);

    /**
     * 批量删除用户-角色关联
     * <p>删除指定角色的所有用户关联记录，防止角色删除后产生脏数据</p>
     *
     * @param roleIds 角色 ID 数组
     * @return 删除的记录数
     */
    int deleteUserRoleByRoleIds(Long[] roleIds);

    /**
     * 批量插入角色-菜单关联
     * <p>为指定角色分配多个菜单权限</p>
     *
     * @param roleId  角色 ID
     * @param menuIds 菜单 ID 数组
     * @return 插入的记录数
     */
    int insertRoleMenu(@Param("roleId") Long roleId, @Param("menuIds") Long[] menuIds);

    /**
     * 查询用户的角色标识列表
     * <p>通过 sys_user_role 关联查询用户所有启用角色的 role_key</p>
     *
     * @param userId 用户 ID
     * @return 角色标识列表（如 ["admin", "common"]）
     */
    List<String> selectRoleKeysByUserId(Long userId);
}
