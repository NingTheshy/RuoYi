package com.ruoyi.system.domain.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "新增定时任务请求")
public class SysJobCreateReq {

    @NotBlank(message = "任务名称不能为空")
    @Size(min = 2, max = 64, message = "任务名称长度必须在 2 到 64 个字符之间")
    private String jobName;

    @Size(max = 64)
    private String jobGroup = "DEFAULT";

    @NotBlank(message = "任务执行类不能为空")
    @Size(max = 255)
    private String jobClassName;

    @NotBlank(message = "Cron表达式不能为空")
    @Size(max = 128)
    private String cronExpression;

    @Pattern(regexp = "^[123]$", message = "错过策略只能是1/2/3")
    private String misfirePolicy = "3";

    @Pattern(regexp = "^[01]$", message = "并发选项只能是0或1")
    private String concurrent = "1";

    @Pattern(regexp = "^[01]$", message = "状态只能是0或1")
    private String status = "0";

    @Size(max = 500)
    private String remark;
}
