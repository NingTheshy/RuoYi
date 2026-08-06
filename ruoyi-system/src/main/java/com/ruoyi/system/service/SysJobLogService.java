package com.ruoyi.system.service;

import com.ruoyi.common.core.domain.PageResult;
import com.ruoyi.system.domain.dto.req.SysJobLogQueryReq;
import com.ruoyi.system.domain.dto.resp.SysJobLogResp;
import com.ruoyi.system.domain.entity.SysJobLog;

public interface SysJobLogService {
    PageResult<SysJobLogResp> getJobLogPage(SysJobLogQueryReq queryReq, Integer pageNum, Integer pageSize);
    int deleteJobLogByIds(Long[] jobLogIds);
    int cleanJobLog();
    void saveJobLog(SysJobLog jobLog);
}
