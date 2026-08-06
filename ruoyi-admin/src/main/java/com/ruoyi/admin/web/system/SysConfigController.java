package com.ruoyi.admin.web.system;

import com.ruoyi.common.core.domain.PageResult;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.dto.req.SysConfigCreateReq;
import com.ruoyi.system.domain.dto.req.SysConfigQueryReq;
import com.ruoyi.system.domain.dto.req.SysConfigUpdateReq;
import com.ruoyi.system.domain.dto.resp.SysConfigResp;
import com.ruoyi.system.service.SysConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

@Tag(name = "参数配置管理")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/system/config")
public class SysConfigController {

    private final SysConfigService configService;

    public SysConfigController(SysConfigService configService) {
        this.configService = configService;
    }

    @Operation(summary = "分页查询参数配置列表")
    @PreAuthorize("hasAuthority('system:config:list')")
    @GetMapping("/list")
    public R<PageResult<SysConfigResp>> list(@Valid @ParameterObject SysConfigQueryReq queryReq,
                                              @Parameter(description = "页码", example = "1")
                                              @RequestParam(defaultValue = "1") Integer pageNum,
                                              @Parameter(description = "每页条数", example = "10")
                                              @RequestParam(defaultValue = "10") Integer pageSize) {
        return R.ok(configService.getConfigPage(queryReq, pageNum, pageSize));
    }

    @Operation(summary = "查询参数配置详情")
    @PreAuthorize("hasAuthority('system:config:query')")
    @GetMapping("/{configId}")
    public R<SysConfigResp> getInfo(@PathVariable Long configId) {
        return R.ok(configService.getConfigById(configId));
    }

    @Operation(summary = "按Key获取参数值")
    @GetMapping("/key/{configKey}")
    public R<String> getConfigKey(@PathVariable String configKey) {
        return R.ok(configService.getConfigValueByKey(configKey));
    }

    @Operation(summary = "新增参数配置")
    @PreAuthorize("hasAuthority('system:config:add')")
    @PostMapping
    public R<Void> add(@Valid @RequestBody SysConfigCreateReq req) {
        configService.createConfig(req);
        return R.ok();
    }

    @Operation(summary = "修改参数配置")
    @PreAuthorize("hasAuthority('system:config:edit')")
    @PutMapping
    public R<Void> edit(@Valid @RequestBody SysConfigUpdateReq req) {
        configService.updateConfig(req);
        return R.ok();
    }

    @Operation(summary = "批量删除参数配置")
    @PreAuthorize("hasAuthority('system:config:remove')")
    @DeleteMapping("/{configIds}")
    public R<Void> remove(@PathVariable Long[] configIds) {
        configService.deleteConfigByIds(configIds);
        return R.ok();
    }
}
