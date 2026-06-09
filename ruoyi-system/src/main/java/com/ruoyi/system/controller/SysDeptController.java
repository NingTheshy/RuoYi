package com.ruoyi.system.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.entity.SysDept;
import com.ruoyi.system.service.ISysDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system/dept")
public class SysDeptController {

    @Autowired
    private ISysDeptService deptService;

    @PreAuthorize("hasAuthority('system:dept:list')")
    @GetMapping("/list")
    public R<List<SysDept>> list(SysDept dept) {
        return R.ok(deptService.selectDeptList(dept));
    }

    @PreAuthorize("hasAuthority('system:dept:query')")
    @GetMapping("/{deptId}")
    public R<SysDept> getInfo(@PathVariable Long deptId) {
        return R.ok(deptService.selectDeptById(deptId));
    }

    @PreAuthorize("hasAuthority('system:dept:add')")
    @PostMapping
    public R<Void> add(@RequestBody SysDept dept) {
        return deptService.insertDept(dept) > 0 ? R.ok() : R.fail();
    }

    @PreAuthorize("hasAuthority('system:dept:edit')")
    @PutMapping
    public R<Void> edit(@RequestBody SysDept dept) {
        return deptService.updateDept(dept) > 0 ? R.ok() : R.fail();
    }

    @PreAuthorize("hasAuthority('system:dept:remove')")
    @DeleteMapping("/{deptId}")
    public R<Void> remove(@PathVariable Long deptId) {
        return deptService.deleteDeptById(deptId) > 0 ? R.ok() : R.fail();
    }
}
