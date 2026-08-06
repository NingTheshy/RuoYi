package com.ruoyi.admin.web.monitor;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.dto.resp.monitor.ServerInfoResp;
import com.ruoyi.system.service.ServerMonitorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "服务监控")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/monitor/server")
public class SysMonitorController {

    private final ServerMonitorService serverMonitorService;

    public SysMonitorController(ServerMonitorService serverMonitorService) {
        this.serverMonitorService = serverMonitorService;
    }

    @Operation(summary = "获取服务器信息")
    @PreAuthorize("hasAuthority('monitor:server')")
    @GetMapping
    public R<ServerInfoResp> getServerInfo() {
        return R.ok(serverMonitorService.getServerInfo());
    }
}
