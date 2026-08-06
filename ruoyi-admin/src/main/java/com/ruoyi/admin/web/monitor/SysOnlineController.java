package com.ruoyi.admin.web.monitor;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.dto.resp.SysOnlineResp;
import com.ruoyi.system.service.SysOnlineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "在线用户管理")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/monitor/online")
public class SysOnlineController {

    private final SysOnlineService onlineService;

    public SysOnlineController(SysOnlineService onlineService) {
        this.onlineService = onlineService;
    }

    @Operation(summary = "查询在线用户列表")
    @PreAuthorize("hasAuthority('monitor:online:list')")
    @GetMapping("/list")
    public R<List<SysOnlineResp>> list() {
        return R.ok(onlineService.getOnlineList());
    }

    @Operation(summary = "强制下线用户")
    @PreAuthorize("hasAuthority('monitor:online:remove')")
    @DeleteMapping("/{tokenId}")
    public R<Void> forceLogout(@PathVariable String tokenId) {
        onlineService.forceLogout(tokenId);
        return R.ok();
    }
}
