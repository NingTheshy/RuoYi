package com.ruoyi.system.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.dto.SysDeptDTO;
import com.ruoyi.system.domain.dto.SysDeptQueryDTO;
import com.ruoyi.system.domain.entity.SysDept;
import com.ruoyi.system.domain.vo.SysDeptVO;
import com.ruoyi.system.service.ISysDeptService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门管理控制器
 * <p>
 * 提供部门的 CRUD 操作。所有接口使用 DTO 接收请求参数、VO 返回响应数据。
 * </p>
 * <ul>
 *   <li>GET  /system/dept/list     - 查询部门列表（支持树形结构）</li>
 *   <li>GET  /system/dept/{deptId} - 查询部门详情</li>
 *   <li>POST /system/dept          - 新增部门</li>
 *   <li>PUT  /system/dept          - 修改部门</li>
 *   <li>DELETE /system/dept/{deptId} - 删除部门（需无子部门且无用户）</li>
 * </ul>
 *
 * @author NingTheshy
 */
@RestController
@RequestMapping("/system/dept")
public class SysDeptController {

    @Autowired
    private ISysDeptService deptService;

    /**
     * 查询部门列表
     * <p>支持按部门名称和状态筛选，返回平铺列表（前端组装树形结构）</p>
     *
     * @param queryDTO 查询条件 DTO
     * @return 部门 VO 列表
     */
    @PreAuthorize("hasAuthority('system:dept:list')")
    @GetMapping("/list")
    public R<List<SysDeptVO>> list(SysDeptQueryDTO queryDTO) {
        // 将查询 DTO 转换为实体
        SysDept query = new SysDept();
        query.setDeptName(queryDTO.getDeptName());
        query.setStatus(queryDTO.getStatus());

        List<SysDept> depts = deptService.getDeptList(query);
        return R.ok(SysDeptVO.fromEntityList(depts));
    }

    /**
     * 查询部门详情
     *
     * @param deptId 部门 ID
     * @return 部门 VO
     */
    @PreAuthorize("hasAuthority('system:dept:query')")
    @GetMapping("/{deptId}")
    public R<SysDeptVO> getInfo(@PathVariable Long deptId) {
        return R.ok(SysDeptVO.fromEntity(deptService.getDeptById(deptId)));
    }

    /**
     * 新增部门
     * <p>
     * 如果有父部门，会自动拼接 ancestors 路径（如 "0,100,101"）。
     * </p>
     *
     * @param dto 部门新增 DTO（包含 parentId、deptName、orderNum 等）
     * @return 操作结果
     */
    @PreAuthorize("hasAuthority('system:dept:add')")
    @PostMapping
    public R<Void> add(@Valid @RequestBody SysDeptDTO dto) {
        return deptService.createDept(dto.toEntity()) > 0 ? R.ok() : R.fail();
    }

    /**
     * 修改部门
     *
     * @param dto 部门修改 DTO（必须包含 deptId）
     * @return 操作结果
     */
    @PreAuthorize("hasAuthority('system:dept:edit')")
    @PutMapping
    public R<Void> edit(@Valid @RequestBody SysDeptDTO dto) {
        return deptService.updateDept(dto.toEntity()) > 0 ? R.ok() : R.fail();
    }

    /**
     * 删除部门
     * <p>
     * 删除前校验：
     * 1. 不能有子部门
     * 2. 不能有用户属于该部门
     * </p>
     *
     * @param deptId 部门 ID
     * @return 操作结果
     */
    @PreAuthorize("hasAuthority('system:dept:remove')")
    @DeleteMapping("/{deptId}")
    public R<Void> remove(@PathVariable Long deptId) {
        return deptService.deleteDeptById(deptId) > 0 ? R.ok() : R.fail();
    }
}
