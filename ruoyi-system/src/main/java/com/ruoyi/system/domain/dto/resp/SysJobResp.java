package com.ruoyi.system.domain.dto.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "定时任务响应")
public class SysJobResp {

    private Long jobId;
    private String jobName;
    private String jobGroup;
    private String jobClassName;
    private String cronExpression;
    private String misfirePolicy;
    private String concurrent;
    private String status;
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
