package com.ruoyi.system.convert;

import com.ruoyi.system.domain.dto.req.SysPostCreateReq;
import com.ruoyi.system.domain.dto.req.SysPostQueryReq;
import com.ruoyi.system.domain.dto.req.SysPostUpdateReq;
import com.ruoyi.system.domain.dto.resp.SysPostResp;
import com.ruoyi.system.domain.entity.SysPost;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapStructMapperConfig.class)
public interface SysPostConvert {
    SysPost toEntity(SysPostCreateReq req);
    SysPost toEntity(SysPostUpdateReq req);
    SysPost toEntity(SysPostQueryReq req);
    SysPostResp toResp(SysPost post);
    List<SysPostResp> toRespList(List<SysPost> posts);
}