package com.ruoyi.system.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.entity.SysMenu;
import com.ruoyi.system.domain.vo.MenuTree;
import com.ruoyi.system.service.ISysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system/menu")
public class SysMenuController {

    @Autowired
    private ISysMenuService menuService;

    @PreAuthorize("hasAuthority('system:menu:list')")
    @GetMapping("/list")
    public R<List<SysMenu>> list(SysMenu menu) {
        return R.ok(menuService.selectMenuList(menu));
    }

    @PreAuthorize("hasAuthority('system:menu:query')")
    @GetMapping("/{menuId}")
    public R<SysMenu> getInfo(@PathVariable Long menuId) {
        return R.ok(menuService.selectMenuById(menuId));
    }

    @PreAuthorize("hasAuthority('system:menu:add')")
    @PostMapping
    public R<Void> add(@RequestBody SysMenu menu) {
        return menuService.insertMenu(menu) > 0 ? R.ok() : R.fail();
    }

    @PreAuthorize("hasAuthority('system:menu:edit')")
    @PutMapping
    public R<Void> edit(@RequestBody SysMenu menu) {
        return menuService.updateMenu(menu) > 0 ? R.ok() : R.fail();
    }

    @PreAuthorize("hasAuthority('system:menu:remove')")
    @DeleteMapping("/{menuId}")
    public R<Void> remove(@PathVariable Long menuId) {
        return menuService.deleteMenuById(menuId) > 0 ? R.ok() : R.fail();
    }

    @GetMapping("/treeselect")
    public R<List<MenuTree>> treeselect() {
        return R.ok(menuService.selectMenuTree());
    }

    @GetMapping("/roleMenuTreeselect/{roleId}")
    public R<List<Long>> roleMenuTreeselect(@PathVariable Long roleId) {
        return R.ok(menuService.selectMenuIdsByRoleId(roleId));
    }
}
