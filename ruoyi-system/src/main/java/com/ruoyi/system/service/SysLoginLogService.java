package com.ruoyi.system.service;

import com.ruoyi.common.core.domain.PageResult;
import com.ruoyi.system.domain.dto.req.SysLoginLogQueryReq;
import com.ruoyi.system.domain.dto.resp.SysLoginLogResp;
import com.ruoyi.system.domain.entity.SysLoginLog;

public interface SysLoginLogService {
    PageResult<SysLoginLogResp> getLoginLogPage(SysLoginLogQueryReq queryReq, Integer pageNum, Integer pageSize);
    int deleteLoginLogByIds(Long[] infoIds);
    int cleanLoginLog();
    void saveLoginLog(SysLoginLog loginLog);
    void recordLoginLog(String userName, String ipAddr, String status, String msg);
}
