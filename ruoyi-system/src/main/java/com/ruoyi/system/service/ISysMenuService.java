package com.ruoyi.system.service;

import com.ruoyi.system.domain.entity.SysMenu;
import com.ruoyi.system.domain.vo.MenuTreeVO;

import java.util.List;
import java.util.Set;

/**
 * 菜单业务服务接口
 * <p>
 * 定义菜单管理的业务方法，包括 CRUD、权限查询和树形结构构建。
 * </p>
 *
 * @author NingTheshy
 */
public interface ISysMenuService {

    /**
     * 查询菜单列表
     *
     * @param menu 查询条件
     * @return 菜单列表
     */
    List<SysMenu> getMenuList(SysMenu menu);

    /**
     * 根据 ID 查询菜单详情
     *
     * @param menuId 菜单 ID
     * @return 菜单实体
     */
    SysMenu getMenuById(Long menuId);

    /**
     * 新增菜单
     *
     * @param menu 菜单实体
     * @return 影响行数
     */
    int createMenu(SysMenu menu);

    /**
     * 修改菜单
     *
     * @param menu 菜单实体（必须包含 menuId）
     * @return 影响行数
     */
    int updateMenu(SysMenu menu);

    /**
     * 删除菜单
     * <p>删除前校验：不能有子菜单</p>
     *
     * @param menuId 菜单 ID
     * @return 影响行数
     */
    int deleteMenuById(Long menuId);

    /**
     * 查询用户的权限标识集合
     * <p>用于 JWT 认证过滤器加载用户权限</p>
     *
     * @param userId 用户 ID
     * @return 权限标识集合
     */
    Set<String> getMenuPermsByUserId(Long userId);

    /**
     * 获取菜单树
     * <p>用于前端角色分配菜单时的树形选择器</p>
     *
     * @return 菜单树列表
     */
    List<MenuTreeVO> getMenuTreeVO();

    /**
     * 查询角色关联的菜单 ID 列表
     * <p>用于角色编辑时，前端回显已勾选的菜单节点</p>
     *
     * @param roleId 角色 ID
     * @return 菜单 ID 列表
     */
    List<Long> getMenuIdsByRoleId(Long roleId);
}
