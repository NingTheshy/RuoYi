package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.ruoyi.common.core.constant.Constants;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_config")
public class SysConfig extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long configId;

    private String configName;

    private String configKey;

    private String configValue;

    /** 系统内置（Y是 N否） */
    private String configType;

    private String status;

    @TableLogic(value = Constants.DEL_FLAG_NORMAL, delval = Constants.DEL_FLAG_DELETED)
    private String delFlag;
}
