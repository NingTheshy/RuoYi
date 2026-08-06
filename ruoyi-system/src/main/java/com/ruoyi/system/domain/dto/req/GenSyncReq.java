package com.ruoyi.system.domain.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "同步数据库表请求")
public class GenSyncReq {

    @NotEmpty(message = "表名称列表不能为空")
    @Schema(description = "表名称列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> tableNames;
}
