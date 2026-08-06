package com.ruoyi.system.service;

import com.ruoyi.common.core.domain.PageResult;
import com.ruoyi.system.domain.dto.req.SysOperLogQueryReq;
import com.ruoyi.system.domain.dto.resp.SysOperLogResp;
import com.ruoyi.system.domain.entity.SysOperLog;

public interface SysOperLogService {
    PageResult<SysOperLogResp> getOperLogPage(SysOperLogQueryReq queryReq, Integer pageNum, Integer pageSize);
    SysOperLogResp getOperLogById(Long operId);
    int deleteOperLogByIds(Long[] operIds);
    int cleanOperLog();
    void saveOperLog(SysOperLog operLog);
}
