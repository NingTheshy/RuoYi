package com.ruoyi.admin.web.system;

import com.ruoyi.common.core.domain.PageResult;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.dto.req.SysUserAssignRolesReq;
import com.ruoyi.system.domain.dto.req.SysUserChangeStatusReq;
import com.ruoyi.system.domain.dto.req.SysUserCreateReq;
import com.ruoyi.system.domain.dto.req.SysUserQueryReq;
import com.ruoyi.system.domain.dto.req.SysUserResetPasswordReq;
import com.ruoyi.system.domain.dto.req.SysUserUpdateReq;
import com.ruoyi.system.domain.dto.resp.SysUserResp;
import com.ruoyi.system.service.SysUserRoleService;
import com.ruoyi.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理控制器
 */
@Tag(name = "用户管理")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/system/user")
public class SysUserController {

    private final SysUserService userService;
    private final SysUserRoleService userRoleService;

    public SysUserController(SysUserService userService,
                             SysUserRoleService userRoleService) {
        this.userService = userService;
        this.userRoleService = userRoleService;
    }

    @Operation(summary = "分页查询用户列表")
    @PreAuthorize("hasAuthority('system:user:list')")
    @GetMapping("/list")
    public R<PageResult<SysUserResp>> list(@Valid @ParameterObject SysUserQueryReq queryReq,
                                           @Parameter(description = "页码", example = "1")
                                           @RequestParam(defaultValue = "1") Integer pageNum,
                                           @Parameter(description = "每页条数", example = "10")
                                           @RequestParam(defaultValue = "10") Integer pageSize) {
        return R.ok(userService.getUserPage(queryReq, pageNum, pageSize));
    }

    @Operation(summary = "查询用户详情")
    @PreAuthorize("hasAuthority('system:user:query')")
    @GetMapping("/{userId}")
    public R<SysUserResp> getInfo(@PathVariable Long userId) {
        return R.ok(userService.getUserDetail(userId));
    }

    @Operation(summary = "新增用户")
    @PreAuthorize("hasAuthority('system:user:add')")
    @PostMapping
    public R<Void> add(@Valid @RequestBody SysUserCreateReq req) {
        userService.createUser(req);
        return R.ok();
    }

    @Operation(summary = "修改用户")
    @PreAuthorize("hasAuthority('system:user:edit')")
    @PutMapping
    public R<Void> edit(@Valid @RequestBody SysUserUpdateReq req) {
        userService.updateUser(req);
        return R.ok();
    }

    @Operation(summary = "批量删除用户")
    @PreAuthorize("hasAuthority('system:user:remove')")
    @DeleteMapping("/{userIds}")
    public R<Void> remove(@PathVariable Long[] userIds) {
        userService.deleteUserByIds(userIds);
        return R.ok();
    }

    @Operation(summary = "重置用户密码")
    @PreAuthorize("hasAuthority('system:user:resetPwd')")
    @PutMapping("/resetPwd")
    public R<Void> resetPwd(@Valid @RequestBody SysUserResetPasswordReq req) {
        userService.resetPassword(req.getUserId(), req.getPassword());
        return R.ok();
    }

    @Operation(summary = "切换用户状态")
    @PreAuthorize("hasAuthority('system:user:edit')")
    @PutMapping("/changeStatus")
    public R<Void> changeStatus(@Valid @RequestBody SysUserChangeStatusReq req) {
        userService.updateUserStatus(req.getUserId(), req.getStatus());
        return R.ok();
    }

    @Operation(summary = "获取用户角色列表")
    @PreAuthorize("hasAuthority('system:user:query')")
    @GetMapping("/roles/{userId}")
    public R<List<Long>> getUserRoles(@PathVariable Long userId) {
        return R.ok(userRoleService.getUserRoleIds(userId));
    }

    @Operation(summary = "分配用户角色")
    @PreAuthorize("hasAuthority('system:user:edit')")
    @PutMapping("/roles")
    public R<Void> assignRoles(@Valid @RequestBody SysUserAssignRolesReq req) {
        userRoleService.updateUserRoles(req.getUserId(), req.getRoleIds());
        return R.ok();
    }
}
