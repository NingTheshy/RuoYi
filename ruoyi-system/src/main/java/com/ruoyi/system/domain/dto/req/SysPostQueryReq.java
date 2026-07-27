package com.ruoyi.system.domain.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "岗位查询请求")
public class SysPostQueryReq {

    @Schema(description = "岗位名称", example = "管理员")
    private String postName;

    @Schema(description = "岗位编码", example = "admin")
    private String postCode;

    @Schema(description = "状态，0正常 1停用", example = "0")
    @Pattern(regexp = "^[01]$", message = "状态只能是0或1")
    private String status;
}