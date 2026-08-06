package com.ruoyi.admin.web.monitor;

import com.ruoyi.common.core.annotation.OperLog;
import com.ruoyi.common.core.domain.PageResult;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.dto.req.*;
import com.ruoyi.system.domain.dto.resp.SysJobResp;
import com.ruoyi.system.service.SysJobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "定时任务管理")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/monitor/job")
public class SysJobController {

    private final SysJobService jobService;

    public SysJobController(SysJobService jobService) {
        this.jobService = jobService;
    }

    @Operation(summary = "分页查询定时任务列表")
    @PreAuthorize("hasAuthority('monitor:job:list')")
    @GetMapping("/list")
    public R<PageResult<SysJobResp>> list(@Valid @ParameterObject SysJobQueryReq queryReq,
                                           @Parameter(description = "页码", example = "1")
                                           @RequestParam(defaultValue = "1") Integer pageNum,
                                           @Parameter(description = "每页条数", example = "10")
                                           @RequestParam(defaultValue = "10") Integer pageSize) {
        return R.ok(jobService.getJobPage(queryReq, pageNum, pageSize));
    }

    @Operation(summary = "查询定时任务详情")
    @PreAuthorize("hasAuthority('monitor:job:query')")
    @GetMapping("/{jobId}")
    public R<SysJobResp> getInfo(@PathVariable Long jobId) {
        return R.ok(jobService.getJobById(jobId));
    }

    @OperLog(title = "定时任务", businessType = "1")
    @Operation(summary = "新增定时任务")
    @PreAuthorize("hasAuthority('monitor:job:add')")
    @PostMapping
    public R<Void> add(@Valid @RequestBody SysJobCreateReq req) {
        jobService.createJob(req);
        return R.ok();
    }

    @OperLog(title = "定时任务", businessType = "2")
    @Operation(summary = "修改定时任务")
    @PreAuthorize("hasAuthority('monitor:job:edit')")
    @PutMapping
    public R<Void> edit(@Valid @RequestBody SysJobUpdateReq req) {
        jobService.updateJob(req);
        return R.ok();
    }

    @OperLog(title = "定时任务", businessType = "3")
    @Operation(summary = "删除定时任务")
    @PreAuthorize("hasAuthority('monitor:job:remove')")
    @DeleteMapping("/{jobIds}")
    public R<Void> remove(@PathVariable Long[] jobIds) {
        jobService.deleteJobByIds(jobIds);
        return R.ok();
    }

    @OperLog(title = "定时任务", businessType = "2")
    @Operation(summary = "改变任务状态")
    @PreAuthorize("hasAuthority('monitor:job:changeStatus')")
    @PutMapping("/changeStatus")
    public R<Void> changeStatus(@Valid @RequestBody SysJobChangeStatusReq req) {
        jobService.changeStatus(req.getJobId(), req.getStatus());
        return R.ok();
    }

    @OperLog(title = "定时任务", businessType = "1")
    @Operation(summary = "立即执行任务")
    @PreAuthorize("hasAuthority('monitor:job:run')")
    @PostMapping("/run")
    public R<Void> run(@Valid @RequestBody SysJobRunReq req) {
        jobService.runJob(req.getJobId());
        return R.ok();
    }
}
