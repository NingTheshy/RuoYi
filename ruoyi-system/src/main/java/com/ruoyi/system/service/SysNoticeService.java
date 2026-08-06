package com.ruoyi.system.service;

import com.ruoyi.common.core.domain.PageResult;
import com.ruoyi.system.domain.dto.req.SysNoticeCreateReq;
import com.ruoyi.system.domain.dto.req.SysNoticeQueryReq;
import com.ruoyi.system.domain.dto.req.SysNoticeUpdateReq;
import com.ruoyi.system.domain.dto.resp.SysNoticeResp;

public interface SysNoticeService {
    PageResult<SysNoticeResp> getNoticePage(SysNoticeQueryReq queryReq, Integer pageNum, Integer pageSize);
    SysNoticeResp getNoticeById(Long noticeId);
    int createNotice(SysNoticeCreateReq req);
    int updateNotice(SysNoticeUpdateReq req);
    int deleteNoticeByIds(Long[] noticeIds);
}
