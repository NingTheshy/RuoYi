package com.ruoyi.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageResult;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.entity.SysRole;
import com.ruoyi.system.service.ISysMenuService;
import com.ruoyi.system.service.ISysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system/role")
public class SysRoleController {

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysMenuService menuService;

    @PreAuthorize("hasAuthority('system:role:list')")
    @GetMapping("/list")
    public R<PageResult<SysRole>> list(SysRole role,
                                       @RequestParam(defaultValue = "1") Integer pageNum,
                                       @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<SysRole> page = roleService.selectRolePage(new Page<>(pageNum, pageSize), role);
        return R.ok(new PageResult<>(page.getRecords(), page.getTotal()));
    }

    @PreAuthorize("hasAuthority('system:role:query')")
    @GetMapping("/{roleId}")
    public R<SysRole> getInfo(@PathVariable Long roleId) {
        return R.ok(roleService.selectRoleById(roleId));
    }

    @PreAuthorize("hasAuthority('system:role:add')")
    @PostMapping
    public R<Void> add(@RequestBody SysRole role,
                        @RequestParam(required = false) Long[] menuIds) {
        return roleService.insertRole(role, menuIds) > 0 ? R.ok() : R.fail();
    }

    @PreAuthorize("hasAuthority('system:role:edit')")
    @PutMapping
    public R<Void> edit(@RequestBody SysRole role,
                         @RequestParam(required = false) Long[] menuIds) {
        return roleService.updateRole(role, menuIds) > 0 ? R.ok() : R.fail();
    }

    @PreAuthorize("hasAuthority('system:role:remove')")
    @DeleteMapping("/{roleIds}")
    public R<Void> remove(@PathVariable Long[] roleIds) {
        return roleService.deleteRoleByIds(roleIds) > 0 ? R.ok() : R.fail();
    }

    @GetMapping("/roleMenuTreeselect/{roleId}")
    public R<List<Long>> roleMenuTreeselect(@PathVariable Long roleId) {
        return R.ok(menuService.selectMenuIdsByRoleId(roleId));
    }
}
