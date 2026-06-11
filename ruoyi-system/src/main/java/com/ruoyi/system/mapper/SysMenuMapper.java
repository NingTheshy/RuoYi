package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.SysMenu;

import java.util.List;

/**
 * 菜单数据访问层
 * <p>
 * 继承 MyBatis-Plus 的 {@link BaseMapper}，提供基础 CRUD 方法。
 * 自定义查询方法对应 SysMenuMapper.xml 中的 SQL 语句。
 * </p>
 *
 * @author NingTheshy
 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    /**
     * 查询菜单列表（支持条件筛选）
     *
     * @param menu 查询条件（可选：menuName、status、visible）
     * @return 菜单列表，按 parentId 和 orderNum 排序
     */
    List<SysMenu> selectMenuList(SysMenu menu);

    /**
     * 查询用户的权限标识列表
     * <p>
     * 通过 用户→角色→菜单 的关联查询，获取所有启用菜单的 perms 字段。
     * 用于 JWT 认证过滤器加载用户权限。
     * </p>
     *
     * @param userId 用户 ID
     * @return 权限标识列表（如 ["system:user:list", "system:role:list"]）
     */
    List<String> selectMenuPermsByUserId(Long userId);

    /**
     * 查询角色关联的菜单 ID 列表
     * <p>用于角色编辑时，前端回显已勾选的菜单节点</p>
     *
     * @param roleId 角色 ID
     * @return 菜单 ID 列表
     */
    List<Long> selectMenuIdsByRoleId(Long roleId);
}
