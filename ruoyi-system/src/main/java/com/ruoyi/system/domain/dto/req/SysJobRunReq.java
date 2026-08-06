package com.ruoyi.system.domain.dto.req;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SysJobRunReq {
    @NotNull(message = "任务ID不能为空")
    private Long jobId;
}
