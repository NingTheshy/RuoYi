package com.ruoyi.system.domain.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "代码生成表查询请求")
public class GenTableQueryReq {

    @Schema(description = "表名称", example = "sys_user")
    private String tableName;

    @Schema(description = "表描述", example = "用户信息表")
    private String tableComment;
}
