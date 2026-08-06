package com.ruoyi.admin.web.monitor;

import com.ruoyi.common.core.domain.PageResult;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.dto.req.SysJobLogQueryReq;
import com.ruoyi.system.domain.dto.resp.SysJobLogResp;
import com.ruoyi.system.service.SysJobLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "任务日志管理")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/monitor/job/log")
public class SysJobLogController {

    private final SysJobLogService jobLogService;

    public SysJobLogController(SysJobLogService jobLogService) {
        this.jobLogService = jobLogService;
    }

    @Operation(summary = "分页查询任务日志列表")
    @PreAuthorize("hasAuthority('monitor:job:log:list')")
    @GetMapping("/list")
    public R<PageResult<SysJobLogResp>> list(@Valid @ParameterObject SysJobLogQueryReq queryReq,
                                              @Parameter(description = "页码", example = "1")
                                              @RequestParam(defaultValue = "1") Integer pageNum,
                                              @Parameter(description = "每页条数", example = "10")
                                              @RequestParam(defaultValue = "10") Integer pageSize) {
        return R.ok(jobLogService.getJobLogPage(queryReq, pageNum, pageSize));
    }

    @Operation(summary = "删除任务日志")
    @PreAuthorize("hasAuthority('monitor:job:remove')")
    @DeleteMapping("/{jobLogIds}")
    public R<Void> remove(@PathVariable Long[] jobLogIds) {
        jobLogService.deleteJobLogByIds(jobLogIds);
        return R.ok();
    }

    @Operation(summary = "清空任务日志")
    @PreAuthorize("hasAuthority('monitor:job:remove')")
    @DeleteMapping("/clean")
    public R<Void> clean() {
        jobLogService.cleanJobLog();
        return R.ok();
    }
}
