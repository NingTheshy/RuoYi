package com.ruoyi.system.domain.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "参数配置查询请求")
public class SysConfigQueryReq {

    @Schema(description = "参数名称", example = "系统名称")
    private String configName;

    @Schema(description = "参数键名", example = "sysName")
    private String configKey;

    @Schema(description = "系统内置（Y是 N否）", example = "Y")
    private String configType;

    @Schema(description = "状态，0正常 1停用", example = "0")
    @Pattern(regexp = "^[01]$", message = "状态只能是0或1")
    private String status;
}
