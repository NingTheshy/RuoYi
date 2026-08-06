package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.SysConfig;

public interface SysConfigMapper extends BaseMapper<SysConfig> {

    SysConfig selectConfigByKey(String configKey);
}
