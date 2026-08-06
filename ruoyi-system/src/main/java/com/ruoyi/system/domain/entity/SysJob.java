package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.ruoyi.common.core.constant.Constants;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_job")
public class SysJob extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long jobId;

    private String jobName;

    private String jobGroup;

    private String jobClassName;

    private String cronExpression;

    /** 错过策略（1立即执行 2执行一次 3放弃执行） */
    private String misfirePolicy;

    /** 是否并发（0禁止 1允许） */
    private String concurrent;

    private String status;

    @TableLogic(value = Constants.DEL_FLAG_NORMAL, delval = Constants.DEL_FLAG_DELETED)
    private String delFlag;
}
