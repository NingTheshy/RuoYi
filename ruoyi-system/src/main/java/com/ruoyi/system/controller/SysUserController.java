package com.ruoyi.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageResult;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.dto.SysUserDTO;
import com.ruoyi.system.domain.dto.SysUserQueryDTO;
import com.ruoyi.system.domain.entity.SysUser;
import com.ruoyi.system.domain.vo.SysUserVO;
import com.ruoyi.system.service.ISysUserService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理控制器
 * <p>
 * 提供用户的 CRUD 操作、密码重置、状态切换和角色分配。
 * 所有接口使用 DTO 接收请求参数、VO 返回响应数据，不直接暴露实体类。
 * </p>
 * <ul>
 *   <li>GET  /system/user/list      - 分页查询用户列表（支持数据权限）</li>
 *   <li>GET  /system/user/{userId}  - 查询用户详情</li>
 *   <li>POST /system/user           - 新增用户</li>
 *   <li>PUT  /system/user           - 修改用户</li>
 *   <li>DELETE /system/user/{userIds} - 批量删除用户</li>
 *   <li>PUT  /system/user/resetPwd  - 重置密码</li>
 *   <li>PUT  /system/user/changeStatus - 切换用户状态</li>
 *   <li>GET  /system/user/roles/{userId} - 获取用户的角色 ID 列表</li>
 *   <li>PUT  /system/user/roles     - 分配用户角色</li>
 * </ul>
 *
 * @author NingTheshy
 */
@RestController
@RequestMapping("/system/user")
public class SysUserController {

    @Autowired
    private ISysUserService userService;

    /**
     * 分页查询用户列表
     * <p>
     * 支持按用户名、昵称、手机号、状态、部门 ID 筛选。
     * 标注了 {@code @DataScope} 注解，会根据当前用户的数据权限自动过滤结果。
     * </p>
     *
     * @param queryDTO 查询条件 DTO
     * @param pageNum  页码（默认 1）
     * @param pageSize 每页条数（默认 10）
     * @return 分页结果（VO 列表）
     */
    @PreAuthorize("hasAuthority('system:user:list')")
    @GetMapping("/list")
    public R<PageResult<SysUserVO>> list(SysUserQueryDTO queryDTO,
                                         @RequestParam(defaultValue = "1") Integer pageNum,
                                         @RequestParam(defaultValue = "10") Integer pageSize) {
        // 将查询 DTO 转换为实体，传递给 Service 层
        SysUser query = new SysUser();
        query.setUserName(queryDTO.getUserName());
        query.setNickName(queryDTO.getNickName());
        query.setPhonenumber(queryDTO.getPhonenumber());
        query.setStatus(queryDTO.getStatus());
        query.setDeptId(queryDTO.getDeptId());
        query.setBeginTime(queryDTO.getBeginTime());
        query.setEndTime(queryDTO.getEndTime());

        Page<SysUser> page = userService.getUserPage(new Page<>(pageNum, pageSize), query);
        // 将实体列表转换为 VO 列表返回
        return R.ok(new PageResult<>(SysUserVO.fromEntityList(page.getRecords()), page.getTotal()));
    }

    /**
     * 查询用户详情
     *
     * @param userId 用户 ID
     * @return 用户 VO（不包含密码、delFlag 等内部字段）
     */
    @PreAuthorize("hasAuthority('system:user:query')")
    @GetMapping("/{userId}")
    public R<SysUserVO> getInfo(@PathVariable Long userId) {
        return R.ok(SysUserVO.fromEntity(userService.getUserById(userId)));
    }

    /**
     * 新增用户
     * <p>
     * 创建流程：
     * 1. 参数校验（@Valid 触发 Bean Validation）
     * 2. DTO 转换为实体
     * 3. 校验用户名是否已存在
     * 4. 密码 BCrypt 加密
     * </p>
     *
     * @param dto 用户新增 DTO（必须包含 userName、password、nickName）
     * @return 操作结果
     */
    @PreAuthorize("hasAuthority('system:user:add')")
    @PostMapping
    public R<Void> add(@Validated(SysUserDTO.CreateGroup.class) @RequestBody SysUserDTO dto) {
        return userService.createUser(dto.toEntity()) > 0 ? R.ok() : R.fail();
    }

    /**
     * 修改用户
     * <p>只能修改非密码字段，密码重置请使用 resetPwd 接口</p>
     *
     * @param dto 用户修改 DTO（必须包含 userId）
     * @return 操作结果
     */
    @PreAuthorize("hasAuthority('system:user:edit')")
    @PutMapping
    public R<Void> edit(@Validated(SysUserDTO.UpdateGroup.class) @RequestBody SysUserDTO dto) {
        return userService.updateUser(dto.toEntity()) > 0 ? R.ok() : R.fail();
    }

    /**
     * 批量删除用户
     * <p>超级管理员（userId=1）不可删除。采用逻辑删除（del_flag 设为 2）。</p>
     *
     * @param userIds 用户 ID 数组
     * @return 操作结果
     */
    @PreAuthorize("hasAuthority('system:user:remove')")
    @DeleteMapping("/{userIds}")
    public R<Void> remove(@PathVariable Long[] userIds) {
        return userService.deleteUserByIds(userIds) > 0 ? R.ok() : R.fail();
    }

    /**
     * 重置用户密码
     * <p>新密码会经过 BCrypt 加密后存储</p>
     *
     * @param userId   用户 ID
     * @param password 新密码
     * @return 操作结果
     */
    @PreAuthorize("hasAuthority('system:user:resetPwd')")
    @PutMapping("/resetPwd")
    public R<Void> resetPwd(@RequestParam Long userId, @RequestParam String password) {
        return userService.resetPassword(userId, password) > 0 ? R.ok() : R.fail();
    }

    /**
     * 切换用户状态
     * <p>超级管理员（userId=1）不可停用</p>
     *
     * @param userId 用户 ID
     * @param status 目标状态（"0"=正常, "1"=停用）
     * @return 操作结果
     */
    @PreAuthorize("hasAuthority('system:user:edit')")
    @PutMapping("/changeStatus")
    public R<Void> changeStatus(@RequestParam Long userId, @RequestParam String status) {
        return userService.updateUserStatus(userId, status) > 0 ? R.ok() : R.fail();
    }

    /**
     * 获取用户的角色 ID 列表
     * <p>用于用户编辑时，前端回显已勾选的角色</p>
     *
     * @param userId 用户 ID
     * @return 角色 ID 列表
     */
    @PreAuthorize("hasAuthority('system:user:query')")
    @GetMapping("/roles/{userId}")
    public R<List<Long>> getUserRoles(@PathVariable Long userId) {
        return R.ok(userService.getUserRoleIds(userId));
    }

    /**
     * 分配用户角色
     * <p>
     * 先删除用户的所有旧角色关联，再批量插入新的角色关联。
     * 超级管理员（userId=1）的角色不可修改。
     * </p>
     *
     * @param userId  用户 ID
     * @param roleIds 角色 ID 数组
     * @return 操作结果
     */
    @PreAuthorize("hasAuthority('system:user:edit')")
    @PutMapping("/roles")
    public R<Void> assignRoles(@RequestParam Long userId, @RequestBody Long[] roleIds) {
        userService.updateUserRoles(userId, roleIds);
        return R.ok();
    }
}
