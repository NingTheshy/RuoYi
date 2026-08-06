package com.ruoyi.system.domain.dto.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "任务日志响应")
public class SysJobLogResp {

    private Long jobLogId;
    private String jobName;
    private String jobGroup;
    private String jobClassName;
    private String cronExpression;
    private String status;
    private String errorMsg;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime jobTime;
}
