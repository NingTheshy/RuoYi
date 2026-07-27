package com.ruoyi.system.domain.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "字典数据查询请求")
public class SysDictDataQueryReq {

    @Schema(description = "字典类型", example = "sys_user_sex")
    private String dictType;

    @Schema(description = "字典标签", example = "男")
    private String dictLabel;

    @Schema(description = "状态，0正常 1停用", example = "0")
    @Pattern(regexp = "^[01]$", message = "状态只能是0或1")
    private String status;
}