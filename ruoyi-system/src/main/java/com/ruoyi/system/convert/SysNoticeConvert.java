package com.ruoyi.system.convert;

import com.ruoyi.system.domain.dto.req.SysNoticeCreateReq;
import com.ruoyi.system.domain.dto.req.SysNoticeQueryReq;
import com.ruoyi.system.domain.dto.req.SysNoticeUpdateReq;
import com.ruoyi.system.domain.dto.resp.SysNoticeResp;
import com.ruoyi.system.domain.entity.SysNotice;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapStructMapperConfig.class)
public interface SysNoticeConvert {
    SysNotice toEntity(SysNoticeCreateReq req);
    SysNotice toEntity(SysNoticeUpdateReq req);
    SysNotice toEntity(SysNoticeQueryReq req);
    SysNoticeResp toResp(SysNotice notice);
    List<SysNoticeResp> toRespList(List<SysNotice> notices);
}
