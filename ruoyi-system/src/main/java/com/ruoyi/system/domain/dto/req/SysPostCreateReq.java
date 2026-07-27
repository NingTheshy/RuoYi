package com.ruoyi.system.domain.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "新增岗位请求")
public class SysPostCreateReq {

    @Schema(description = "岗位编码", example = "developer")
    @NotBlank(message = "岗位编码不能为空")
    @Size(min = 2, max = 64, message = "岗位编码长度必须在 2 到 64 个字符之间")
    private String postCode;

    @Schema(description = "岗位名称", example = "开发工程师")
    @NotBlank(message = "岗位名称不能为空")
    @Size(min = 2, max = 50, message = "岗位名称长度必须在 2 到 50 个字符之间")
    private String postName;

    @Schema(description = "显示顺序", example = "1")
    private Integer postSort;

    @Schema(description = "状态，0正常 1停用", example = "0")
    @Pattern(regexp = "^[01]$", message = "状态只能是0或1")
    private String status;

    @Schema(description = "备注")
    @Size(max = 500, message = "备注长度不能超过 500 个字符")
    private String remark;
}