package com.ruoyi.admin.web.monitor;

import com.ruoyi.common.core.domain.PageResult;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.dto.req.SysOperLogQueryReq;
import com.ruoyi.system.domain.dto.resp.SysOperLogResp;
import com.ruoyi.system.service.SysOperLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

@Tag(name = "操作日志管理")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/monitor/operlog")
public class SysOperLogController {

    private final SysOperLogService operLogService;

    public SysOperLogController(SysOperLogService operLogService) {
        this.operLogService = operLogService;
    }

    @Operation(summary = "分页查询操作日志列表")
    @PreAuthorize("hasAuthority('monitor:operlog:list')")
    @GetMapping("/list")
    public R<PageResult<SysOperLogResp>> list(@Valid @ParameterObject SysOperLogQueryReq queryReq,
                                               @Parameter(description = "页码", example = "1")
                                               @RequestParam(defaultValue = "1") Integer pageNum,
                                               @Parameter(description = "每页条数", example = "10")
                                               @RequestParam(defaultValue = "10") Integer pageSize) {
        return R.ok(operLogService.getOperLogPage(queryReq, pageNum, pageSize));
    }

    @Operation(summary = "查询操作日志详情")
    @PreAuthorize("hasAuthority('monitor:operlog:query')")
    @GetMapping("/{operId}")
    public R<SysOperLogResp> getInfo(@PathVariable Long operId) {
        return R.ok(operLogService.getOperLogById(operId));
    }

    @Operation(summary = "删除操作日志")
    @PreAuthorize("hasAuthority('monitor:operlog:remove')")
    @DeleteMapping("/{operIds}")
    public R<Void> remove(@PathVariable Long[] operIds) {
        operLogService.deleteOperLogByIds(operIds);
        return R.ok();
    }

    @Operation(summary = "清空操作日志")
    @PreAuthorize("hasAuthority('monitor:operlog:clean')")
    @DeleteMapping("/clean")
    public R<Void> clean() {
        operLogService.cleanOperLog();
        return R.ok();
    }
}
