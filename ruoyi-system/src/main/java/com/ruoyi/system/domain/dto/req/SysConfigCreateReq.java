package com.ruoyi.system.domain.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "新增参数配置请求")
public class SysConfigCreateReq {

    @Schema(description = "参数名称", example = "系统名称")
    @NotBlank(message = "参数名称不能为空")
    @Size(min = 2, max = 100, message = "参数名称长度必须在 2 到 100 个字符之间")
    private String configName;

    @Schema(description = "参数键名", example = "sysName")
    @NotBlank(message = "参数键名不能为空")
    @Size(min = 2, max = 100, message = "参数键名长度必须在 2 到 100 个字符之间")
    private String configKey;

    @Schema(description = "参数键值", example = "RuoYi")
    @NotBlank(message = "参数键值不能为空")
    @Size(max = 500, message = "参数键值长度不能超过 500 个字符")
    private String configValue;

    @Schema(description = "系统内置（Y是 N否）", example = "N")
    @Pattern(regexp = "^[YN]$", message = "系统内置只能是Y或N")
    private String configType;

    @Schema(description = "状态，0正常 1停用", example = "0")
    @Pattern(regexp = "^[01]$", message = "状态只能是0或1")
    private String status;

    @Schema(description = "备注")
    @Size(max = 500, message = "备注长度不能超过 500 个字符")
    private String remark;
}
