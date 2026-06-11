package com.ruoyi.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageResult;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.dto.SysRoleDTO;
import com.ruoyi.system.domain.dto.SysRoleQueryDTO;
import com.ruoyi.system.domain.entity.SysRole;
import com.ruoyi.system.domain.vo.SysRoleVO;
import com.ruoyi.system.service.ISysRoleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 角色管理控制器
 * <p>
 * 提供角色的 CRUD 操作和分页查询。
 * 所有接口使用 DTO 接收请求参数、VO 返回响应数据。
 * </p>
 * <ul>
 *   <li>GET  /system/role/list      - 分页查询角色列表</li>
 *   <li>GET  /system/role/{roleId}  - 查询角色详情</li>
 *   <li>POST /system/role           - 新增角色（含菜单关联）</li>
 *   <li>PUT  /system/role           - 修改角色（含菜单关联）</li>
 *   <li>DELETE /system/role/{roleIds} - 批量删除角色</li>
 * </ul>
 *
 * <p>角色创建/编辑时，通过 {@link SysRoleDTO} 的 menuIds 字段同时关联菜单权限。</p>
 *
 * @author NingTheshy
 */
@RestController
@RequestMapping("/system/role")
public class SysRoleController {

    @Autowired
    private ISysRoleService roleService;

    /**
     * 分页查询角色列表
     * <p>
     * 支持按角色名称（roleName）、角色标识（roleKey）、状态（status）筛选，
     * 按角色排序号（roleSort）升序排列。
     * </p>
     *
     * @param queryDTO 查询条件 DTO
     * @param pageNum  页码（默认 1）
     * @param pageSize 每页条数（默认 10）
     * @return 分页结果（VO 列表）
     */
    @PreAuthorize("hasAuthority('system:role:list')")
    @GetMapping("/list")
    public R<PageResult<SysRoleVO>> list(SysRoleQueryDTO queryDTO,
                                         @RequestParam(defaultValue = "1") Integer pageNum,
                                         @RequestParam(defaultValue = "10") Integer pageSize) {
        // 将查询 DTO 转换为实体
        SysRole query = new SysRole();
        query.setRoleName(queryDTO.getRoleName());
        query.setRoleKey(queryDTO.getRoleKey());
        query.setStatus(queryDTO.getStatus());

        Page<SysRole> page = roleService.getRolePage(new Page<>(pageNum, pageSize), query);
        // 将实体列表转换为 VO 列表返回
        return R.ok(new PageResult<>(SysRoleVO.fromEntityList(page.getRecords()), page.getTotal()));
    }

    /**
     * 查询角色详情
     *
     * @param roleId 角色 ID
     * @return 角色 VO
     */
    @PreAuthorize("hasAuthority('system:role:query')")
    @GetMapping("/{roleId}")
    public R<SysRoleVO> getInfo(@PathVariable Long roleId) {
        return R.ok(SysRoleVO.fromEntity(roleService.getRoleById(roleId)));
    }

    /**
     * 新增角色
     * <p>
     * 使用 {@link SysRoleDTO} 接收请求体，同时包含角色信息和 menuIds 数组。
     * 创建成功后会建立角色-菜单的关联关系。
     * </p>
     *
     * @param dto 角色 DTO（包含 roleName、roleKey、roleSort、menuIds 等）
     * @return 操作结果
     */
    @PreAuthorize("hasAuthority('system:role:add')")
    @PostMapping
    public R<Void> add(@Valid @RequestBody SysRoleDTO dto) {
        return roleService.createRole(dto.toEntity(), dto.getMenuIds()) > 0 ? R.ok() : R.fail();
    }

    /**
     * 修改角色
     * <p>先更新角色信息，再删除旧的菜单关联并重新插入新的菜单关联</p>
     *
     * @param dto 角色 DTO（必须包含 roleId）
     * @return 操作结果
     */
    @PreAuthorize("hasAuthority('system:role:edit')")
    @PutMapping
    public R<Void> edit(@Valid @RequestBody SysRoleDTO dto) {
        return roleService.updateRole(dto.toEntity(), dto.getMenuIds()) > 0 ? R.ok() : R.fail();
    }

    /**
     * 批量删除角色
     * <p>超级管理员角色（ID=1）不可删除。删除时同时清除角色-菜单关联。</p>
     *
     * @param roleIds 角色 ID 数组
     * @return 操作结果
     */
    @PreAuthorize("hasAuthority('system:role:remove')")
    @DeleteMapping("/{roleIds}")
    public R<Void> remove(@PathVariable Long[] roleIds) {
        return roleService.deleteRoleByIds(roleIds) > 0 ? R.ok() : R.fail();
    }
}
