package com.ruoyi.system.domain.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "修改定时任务请求")
public class SysJobUpdateReq {

    @NotNull(message = "任务ID不能为空")
    private Long jobId;

    @NotBlank(message = "任务名称不能为空")
    @Size(min = 2, max = 64)
    private String jobName;

    @Size(max = 64)
    private String jobGroup;

    @NotBlank(message = "任务执行类不能为空")
    @Size(max = 255)
    private String jobClassName;

    @NotBlank(message = "Cron表达式不能为空")
    @Size(max = 128)
    private String cronExpression;

    @Pattern(regexp = "^[123]$")
    private String misfirePolicy;

    @Pattern(regexp = "^[01]$")
    private String concurrent;

    @Pattern(regexp = "^[01]$")
    private String status;

    @Size(max = 500)
    private String remark;
}
