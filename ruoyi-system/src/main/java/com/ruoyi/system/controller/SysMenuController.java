package com.ruoyi.system.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.dto.SysMenuDTO;
import com.ruoyi.system.domain.dto.SysMenuQueryDTO;
import com.ruoyi.system.domain.entity.SysMenu;
import com.ruoyi.system.domain.vo.MenuTreeVO;
import com.ruoyi.system.domain.vo.SysMenuVO;
import com.ruoyi.system.service.ISysMenuService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理控制器
 * <p>
 * 提供菜单的 CRUD 操作和树形查询。所有接口使用 DTO 接收请求参数、VO 返回响应数据。
 * </p>
 * <ul>
 *   <li>GET  /system/menu/list     - 查询菜单列表（平铺）</li>
 *   <li>GET  /system/menu/{menuId} - 查询菜单详情</li>
 *   <li>POST /system/menu          - 新增菜单</li>
 *   <li>PUT  /system/menu          - 修改菜单</li>
 *   <li>DELETE /system/menu/{menuId} - 删除菜单（需无子菜单）</li>
 *   <li>GET  /system/menu/treeselect - 获取菜单树（用于角色分配菜单）</li>
 *   <li>GET  /system/menu/roleMenuTreeVOselect/{roleId} - 获取角色已分配的菜单 ID 列表</li>
 * </ul>
 *
 * @author NingTheshy
 */
@RestController
@RequestMapping("/system/menu")
public class SysMenuController {

    @Autowired
    private ISysMenuService menuService;

    /**
     * 查询菜单列表（平铺结构）
     *
     * @param queryDTO 查询条件 DTO
     * @return 菜单 VO 列表
     */
    @PreAuthorize("hasAuthority('system:menu:list')")
    @GetMapping("/list")
    public R<List<SysMenuVO>> list(SysMenuQueryDTO queryDTO) {
        // 将查询 DTO 转换为实体
        SysMenu query = new SysMenu();
        query.setMenuName(queryDTO.getMenuName());
        query.setStatus(queryDTO.getStatus());
        query.setVisible(queryDTO.getVisible());

        List<SysMenu> menus = menuService.getMenuList(query);
        return R.ok(SysMenuVO.fromEntityList(menus));
    }

    /**
     * 查询菜单详情
     *
     * @param menuId 菜单 ID
     * @return 菜单 VO
     */
    @PreAuthorize("hasAuthority('system:menu:query')")
    @GetMapping("/{menuId}")
    public R<SysMenuVO> getInfo(@PathVariable Long menuId) {
        return R.ok(SysMenuVO.fromEntity(menuService.getMenuById(menuId)));
    }

    /**
     * 新增菜单
     * <p>
     * 菜单类型（menuType）：
     * - M：目录（一级菜单）
     * - C：菜单（页面路由）
     * - F：按钮（操作权限，perms 字段必填）
     * </p>
     *
     * @param dto 菜单新增 DTO
     * @return 操作结果
     */
    @PreAuthorize("hasAuthority('system:menu:add')")
    @PostMapping
    public R<Void> add(@Valid @RequestBody SysMenuDTO dto) {
        return menuService.createMenu(dto.toEntity()) > 0 ? R.ok() : R.fail();
    }

    /**
     * 修改菜单
     *
     * @param dto 菜单修改 DTO（必须包含 menuId）
     * @return 操作结果
     */
    @PreAuthorize("hasAuthority('system:menu:edit')")
    @PutMapping
    public R<Void> edit(@Valid @RequestBody SysMenuDTO dto) {
        return menuService.updateMenu(dto.toEntity()) > 0 ? R.ok() : R.fail();
    }

    /**
     * 删除菜单
     * <p>删除前校验：不能有子菜单</p>
     *
     * @param menuId 菜单 ID
     * @return 操作结果
     */
    @PreAuthorize("hasAuthority('system:menu:remove')")
    @DeleteMapping("/{menuId}")
    public R<Void> remove(@PathVariable Long menuId) {
        return menuService.deleteMenuById(menuId) > 0 ? R.ok() : R.fail();
    }

    /**
     * 获取菜单树
     * <p>
     * 返回树形结构的菜单数据（MenuTreeVO），用于前端角色分配菜单时的树形选择器。
     * MenuTreeVO 包含 id（menuId）和 label（menuName）。
     * </p>
     *
     * @return 菜单树列表
     */
    @PreAuthorize("hasAuthority('system:menu:list')")
    @GetMapping("/treeselect")
    public R<List<MenuTreeVO>> treeselect() {
        return R.ok(menuService.getMenuTreeVO());
    }

    /**
     * 获取角色已分配的菜单 ID 列表
     * <p>
     * 用于角色编辑时，前端回显已勾选的菜单节点。
     * </p>
     *
     * @param roleId 角色 ID
     * @return 菜单 ID 列表
     */
    @PreAuthorize("hasAuthority('system:role:query')")
    @GetMapping("/roleMenuTreeVOselect/{roleId}")
    public R<List<Long>> roleMenuTreeVOselect(@PathVariable Long roleId) {
        return R.ok(menuService.getMenuIdsByRoleId(roleId));
    }
}
