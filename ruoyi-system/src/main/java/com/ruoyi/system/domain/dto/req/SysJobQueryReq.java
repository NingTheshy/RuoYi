package com.ruoyi.system.domain.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "定时任务查询请求")
public class SysJobQueryReq {

    @Schema(description = "任务名称", example = "数据同步")
    private String jobName;

    @Schema(description = "任务分组", example = "DEFAULT")
    private String jobGroup;

    @Schema(description = "状态（0正常 1停用）", example = "0")
    @Pattern(regexp = "^[01]$", message = "状态只能是0或1")
    private String status;
}
