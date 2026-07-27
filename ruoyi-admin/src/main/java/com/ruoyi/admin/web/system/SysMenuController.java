package com.ruoyi.admin.web.system;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.dto.req.SysMenuCreateReq;
import com.ruoyi.system.domain.dto.req.SysMenuQueryReq;
import com.ruoyi.system.domain.dto.req.SysMenuUpdateReq;
import com.ruoyi.system.domain.dto.resp.MenuTreeResp;
import com.ruoyi.system.domain.dto.resp.SysMenuResp;
import com.ruoyi.system.service.SysMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理控制器
 */
@Tag(name = "菜单管理")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/system/menu")
public class SysMenuController {

    private final SysMenuService menuService;

    public SysMenuController(SysMenuService menuService) {
        this.menuService = menuService;
    }

    @Operation(summary = "查询菜单列表")
    @PreAuthorize("hasAuthority('system:menu:list')")
    @GetMapping("/list")
    public R<List<SysMenuResp>> list(@Valid @ParameterObject SysMenuQueryReq queryReq) {
        return R.ok(menuService.getMenuList(queryReq));
    }

    @Operation(summary = "查询菜单详情")
    @PreAuthorize("hasAuthority('system:menu:query')")
    @GetMapping("/{menuId}")
    public R<SysMenuResp> getInfo(@PathVariable Long menuId) {
        return R.ok(menuService.getMenuById(menuId));
    }

    @Operation(summary = "新增菜单")
    @PreAuthorize("hasAuthority('system:menu:add')")
    @PostMapping
    public R<Void> add(@Valid @RequestBody SysMenuCreateReq req) {
        menuService.createMenu(req);
        return R.ok();
    }

    @Operation(summary = "修改菜单")
    @PreAuthorize("hasAuthority('system:menu:edit')")
    @PutMapping
    public R<Void> edit(@Valid @RequestBody SysMenuUpdateReq req) {
        menuService.updateMenu(req);
        return R.ok();
    }

    @Operation(summary = "删除菜单")
    @PreAuthorize("hasAuthority('system:menu:remove')")
    @DeleteMapping("/{menuId}")
    public R<Void> remove(@PathVariable Long menuId) {
        menuService.deleteMenuById(menuId);
        return R.ok();
    }

    @Operation(summary = "获取菜单树")
    @PreAuthorize("hasAuthority('system:menu:list')")
    @GetMapping("/treeselect")
    public R<List<MenuTreeResp>> treeselect() {
        return R.ok(menuService.getMenuTreeResp());
    }

    @Operation(summary = "获取角色已分配菜单列表")
    @PreAuthorize("hasAuthority('system:role:query')")
    @GetMapping({"/role-menu-tree-select/{roleId}", "/roleMenuTreeVOselect/{roleId}"})
    public R<List<Long>> roleMenuTreeSelect(@PathVariable Long roleId) {
        return R.ok(menuService.getMenuIdsByRoleId(roleId));
    }
}
