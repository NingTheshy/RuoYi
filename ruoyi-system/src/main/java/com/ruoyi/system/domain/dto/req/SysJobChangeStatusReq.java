package com.ruoyi.system.domain.dto.req;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SysJobChangeStatusReq {
    @NotNull(message = "任务ID不能为空")
    private Long jobId;

    @NotNull(message = "状态不能为空")
    private String status;
}
