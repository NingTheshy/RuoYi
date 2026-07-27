package com.ruoyi.admin.web.system;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.dto.req.SysDeptCreateReq;
import com.ruoyi.system.domain.dto.req.SysDeptQueryReq;
import com.ruoyi.system.domain.dto.req.SysDeptUpdateReq;
import com.ruoyi.system.domain.dto.resp.SysDeptResp;
import com.ruoyi.system.service.SysDeptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门管理控制器
 */
@Tag(name = "部门管理")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/system/dept")
public class SysDeptController {

    private final SysDeptService deptService;

    public SysDeptController(SysDeptService deptService) {
        this.deptService = deptService;
    }

    @Operation(summary = "查询部门列表")
    @PreAuthorize("hasAuthority('system:dept:list')")
    @GetMapping("/list")
    public R<List<SysDeptResp>> list(@Valid @ParameterObject SysDeptQueryReq queryReq) {
        return R.ok(deptService.getDeptList(queryReq));
    }

    @Operation(summary = "查询部门详情")
    @PreAuthorize("hasAuthority('system:dept:query')")
    @GetMapping("/{deptId}")
    public R<SysDeptResp> getInfo(@PathVariable Long deptId) {
        return R.ok(deptService.getDeptById(deptId));
    }

    @Operation(summary = "新增部门")
    @PreAuthorize("hasAuthority('system:dept:add')")
    @PostMapping
    public R<Void> add(@Valid @RequestBody SysDeptCreateReq req) {
        deptService.createDept(req);
        return R.ok();
    }

    @Operation(summary = "修改部门")
    @PreAuthorize("hasAuthority('system:dept:edit')")
    @PutMapping
    public R<Void> edit(@Valid @RequestBody SysDeptUpdateReq req) {
        deptService.updateDept(req);
        return R.ok();
    }

    @Operation(summary = "删除部门")
    @PreAuthorize("hasAuthority('system:dept:remove')")
    @DeleteMapping("/{deptId}")
    public R<Void> remove(@PathVariable Long deptId) {
        deptService.deleteDeptById(deptId);
        return R.ok();
    }
}
