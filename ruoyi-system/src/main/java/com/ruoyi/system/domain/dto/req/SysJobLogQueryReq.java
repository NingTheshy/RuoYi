package com.ruoyi.system.domain.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "任务日志查询请求")
public class SysJobLogQueryReq {

    private String jobName;

    @Pattern(regexp = "^[01]$")
    private String status;
}
