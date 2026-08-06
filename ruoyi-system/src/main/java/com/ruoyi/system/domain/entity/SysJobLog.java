package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("sys_job_log")
public class SysJobLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long jobLogId;

    private String jobName;

    private String jobGroup;

    private String jobClassName;

    private String cronExpression;

    private String status;

    private String errorMsg;

    private LocalDateTime jobTime;

    private LocalDateTime createTime;
}
