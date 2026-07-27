package com.ruoyi.admin.web.system;

import com.ruoyi.common.core.domain.PageResult;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.dto.req.SysRoleCreateReq;
import com.ruoyi.system.domain.dto.req.SysRoleQueryReq;
import com.ruoyi.system.domain.dto.req.SysRoleUpdateReq;
import com.ruoyi.system.domain.dto.resp.SysRoleResp;
import com.ruoyi.system.service.SysRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

/**
 * 角色管理控制器
 */
@Tag(name = "角色管理")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/system/role")
public class SysRoleController {

    private final SysRoleService roleService;

    public SysRoleController(SysRoleService roleService) {
        this.roleService = roleService;
    }

    @Operation(summary = "分页查询角色列表")
    @PreAuthorize("hasAuthority('system:role:list')")
    @GetMapping("/list")
    public R<PageResult<SysRoleResp>> list(@Valid @ParameterObject SysRoleQueryReq queryReq,
                                           @Parameter(description = "页码", example = "1")
                                           @RequestParam(defaultValue = "1") Integer pageNum,
                                           @Parameter(description = "每页条数", example = "10")
                                           @RequestParam(defaultValue = "10") Integer pageSize) {
        return R.ok(roleService.getRolePage(queryReq, pageNum, pageSize));
    }

    @Operation(summary = "查询角色详情")
    @PreAuthorize("hasAuthority('system:role:query')")
    @GetMapping("/{roleId}")
    public R<SysRoleResp> getInfo(@PathVariable Long roleId) {
        return R.ok(roleService.getRoleById(roleId));
    }

    @Operation(summary = "新增角色")
    @PreAuthorize("hasAuthority('system:role:add')")
    @PostMapping
    public R<Void> add(@Valid @RequestBody SysRoleCreateReq req) {
        roleService.createRole(req);
        return R.ok();
    }

    @Operation(summary = "修改角色")
    @PreAuthorize("hasAuthority('system:role:edit')")
    @PutMapping
    public R<Void> edit(@Valid @RequestBody SysRoleUpdateReq req) {
        roleService.updateRole(req);
        return R.ok();
    }

    @Operation(summary = "批量删除角色")
    @PreAuthorize("hasAuthority('system:role:remove')")
    @DeleteMapping("/{roleIds}")
    public R<Void> remove(@PathVariable Long[] roleIds) {
        roleService.deleteRoleByIds(roleIds);
        return R.ok();
    }
}
