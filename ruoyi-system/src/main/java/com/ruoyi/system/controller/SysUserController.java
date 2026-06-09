package com.ruoyi.system.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.common.core.domain.PageResult;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.entity.SysUser;
import com.ruoyi.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/system/user")
public class SysUserController {

    @Autowired
    private ISysUserService userService;

    @PreAuthorize("hasAuthority('system:user:list')")
    @GetMapping("/list")
    public R<PageResult<SysUser>> list(SysUser user,
                                        @RequestParam(defaultValue = "1") Integer pageNum,
                                        @RequestParam(defaultValue = "10") Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        java.util.List<SysUser> list = userService.selectUserList(user);
        PageInfo<SysUser> pageInfo = new PageInfo<>(list);
        return R.ok(new PageResult<>(list, pageInfo.getTotal()));
    }

    @PreAuthorize("hasAuthority('system:user:query')")
    @GetMapping("/{userId}")
    public R<SysUser> getInfo(@PathVariable Long userId) {
        return R.ok(userService.selectUserById(userId));
    }

    @PreAuthorize("hasAuthority('system:user:add')")
    @PostMapping
    public R<Void> add(@RequestBody SysUser user) {
        return userService.insertUser(user) > 0 ? R.ok() : R.fail();
    }

    @PreAuthorize("hasAuthority('system:user:edit')")
    @PutMapping
    public R<Void> edit(@RequestBody SysUser user) {
        return userService.updateUser(user) > 0 ? R.ok() : R.fail();
    }

    @PreAuthorize("hasAuthority('system:user:remove')")
    @DeleteMapping("/{userIds}")
    public R<Void> remove(@PathVariable Long[] userIds) {
        return userService.deleteUserByIds(userIds) > 0 ? R.ok() : R.fail();
    }

    @PreAuthorize("hasAuthority('system:user:resetPwd')")
    @PutMapping("/resetPwd")
    public R<Void> resetPwd(@RequestBody SysUser user) {
        return userService.resetPassword(user.getUserId(), user.getPassword()) > 0 ? R.ok() : R.fail();
    }

    @PreAuthorize("hasAuthority('system:user:edit')")
    @PutMapping("/changeStatus")
    public R<Void> changeStatus(@RequestBody SysUser user) {
        return userService.updateUserStatus(user.getUserId(), user.getStatus()) > 0 ? R.ok() : R.fail();
    }
}
