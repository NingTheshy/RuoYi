package com.ruoyi.admin.web.monitor;

import com.ruoyi.common.core.domain.PageResult;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.dto.req.SysLoginLogQueryReq;
import com.ruoyi.system.domain.dto.resp.SysLoginLogResp;
import com.ruoyi.system.service.SysLoginLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

@Tag(name = "登录日志管理")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/monitor/logininfor")
public class SysLoginLogController {

    private final SysLoginLogService loginLogService;

    public SysLoginLogController(SysLoginLogService loginLogService) {
        this.loginLogService = loginLogService;
    }

    @Operation(summary = "分页查询登录日志列表")
    @PreAuthorize("hasAuthority('monitor:logininfor:list')")
    @GetMapping("/list")
    public R<PageResult<SysLoginLogResp>> list(@Valid @ParameterObject SysLoginLogQueryReq queryReq,
                                                @Parameter(description = "页码", example = "1")
                                                @RequestParam(defaultValue = "1") Integer pageNum,
                                                @Parameter(description = "每页条数", example = "10")
                                                @RequestParam(defaultValue = "10") Integer pageSize) {
        return R.ok(loginLogService.getLoginLogPage(queryReq, pageNum, pageSize));
    }

    @Operation(summary = "删除登录日志")
    @PreAuthorize("hasAuthority('monitor:logininfor:remove')")
    @DeleteMapping("/{infoIds}")
    public R<Void> remove(@PathVariable Long[] infoIds) {
        loginLogService.deleteLoginLogByIds(infoIds);
        return R.ok();
    }

    @Operation(summary = "清空登录日志")
    @PreAuthorize("hasAuthority('monitor:logininfor:clean')")
    @DeleteMapping("/clean")
    public R<Void> clean() {
        loginLogService.cleanLoginLog();
        return R.ok();
    }
}
