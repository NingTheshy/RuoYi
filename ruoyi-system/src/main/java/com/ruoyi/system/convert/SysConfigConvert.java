package com.ruoyi.system.convert;

import com.ruoyi.system.domain.dto.req.SysConfigCreateReq;
import com.ruoyi.system.domain.dto.req.SysConfigQueryReq;
import com.ruoyi.system.domain.dto.req.SysConfigUpdateReq;
import com.ruoyi.system.domain.dto.resp.SysConfigResp;
import com.ruoyi.system.domain.entity.SysConfig;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapStructMapperConfig.class)
public interface SysConfigConvert {
    SysConfig toEntity(SysConfigCreateReq req);
    SysConfig toEntity(SysConfigUpdateReq req);
    SysConfig toEntity(SysConfigQueryReq req);
    SysConfigResp toResp(SysConfig config);
    List<SysConfigResp> toRespList(List<SysConfig> configs);
}
