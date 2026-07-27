package com.ruoyi.system.domain.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "更新字典类型请求")
public class SysDictTypeUpdateReq {

    @Schema(description = "字典类型ID", example = "1")
    @NotNull(message = "字典类型ID不能为空")
    private Long dictId;

    @Schema(description = "字典名称", example = "用户性别")
    @NotBlank(message = "字典名称不能为空")
    @Size(min = 2, max = 100, message = "字典名称长度必须在 2 到 100 个字符之间")
    private String dictName;

    @Schema(description = "字典类型", example = "sys_user_sex")
    @NotBlank(message = "字典类型不能为空")
    @Size(min = 2, max = 100, message = "字典类型长度必须在 2 到 100 个字符之间")
    private String dictType;

    @Schema(description = "状态，0正常 1停用", example = "0")
    @Pattern(regexp = "^[01]$", message = "状态只能是0或1")
    private String status;

    @Schema(description = "备注")
    @Size(max = 500, message = "备注长度不能超过 500 个字符")
    private String remark;
}