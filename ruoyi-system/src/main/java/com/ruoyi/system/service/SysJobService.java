package com.ruoyi.system.service;

import com.ruoyi.common.core.domain.PageResult;
import com.ruoyi.system.domain.dto.req.SysJobCreateReq;
import com.ruoyi.system.domain.dto.req.SysJobQueryReq;
import com.ruoyi.system.domain.dto.req.SysJobUpdateReq;
import com.ruoyi.system.domain.dto.resp.SysJobResp;

public interface SysJobService {
    PageResult<SysJobResp> getJobPage(SysJobQueryReq queryReq, Integer pageNum, Integer pageSize);
    SysJobResp getJobById(Long jobId);
    int createJob(SysJobCreateReq req);
    int updateJob(SysJobUpdateReq req);
    int deleteJobByIds(Long[] jobIds);
    int changeStatus(Long jobId, String status);
    int runJob(Long jobId);
}
