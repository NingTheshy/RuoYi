package com.ruoyi.system.domain.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "代码生成表更新请求")
public class GenTableUpdateReq {

    @NotNull(message = "表ID不能为空")
    @Schema(description = "表ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long tableId;

    @NotBlank(message = "表名称不能为空")
    @Schema(description = "表名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String tableName;

    @Schema(description = "表描述")
    private String tableComment;

    @Schema(description = "实体类名称")
    private String className;

    @Schema(description = "包名称")
    private String packageName;

    @Schema(description = "模块名称")
    private String moduleName;

    @Schema(description = "业务名称")
    private String businessName;

    @Schema(description = "功能名称")
    private String functionName;

    @Schema(description = "功能作者")
    private String functionAuthor;
}
