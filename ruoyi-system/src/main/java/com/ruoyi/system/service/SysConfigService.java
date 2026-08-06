package com.ruoyi.system.service;

import com.ruoyi.common.core.domain.PageResult;
import com.ruoyi.system.domain.dto.req.SysConfigCreateReq;
import com.ruoyi.system.domain.dto.req.SysConfigQueryReq;
import com.ruoyi.system.domain.dto.req.SysConfigUpdateReq;
import com.ruoyi.system.domain.dto.resp.SysConfigResp;

public interface SysConfigService {
    PageResult<SysConfigResp> getConfigPage(SysConfigQueryReq queryReq, Integer pageNum, Integer pageSize);
    SysConfigResp getConfigById(Long configId);
    String getConfigValueByKey(String configKey);
    int createConfig(SysConfigCreateReq req);
    int updateConfig(SysConfigUpdateReq req);
    int deleteConfigByIds(Long[] configIds);
    void clearConfigCache(String configKey);
}
