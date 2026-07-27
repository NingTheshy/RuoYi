package com.ruoyi.system.convert;

import com.ruoyi.system.domain.dto.req.SysDictTypeCreateReq;
import com.ruoyi.system.domain.dto.req.SysDictTypeQueryReq;
import com.ruoyi.system.domain.dto.req.SysDictTypeUpdateReq;
import com.ruoyi.system.domain.dto.resp.SysDictTypeResp;
import com.ruoyi.system.domain.entity.SysDictType;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapStructMapperConfig.class)
public interface SysDictTypeConvert {
    SysDictType toEntity(SysDictTypeCreateReq req);
    SysDictType toEntity(SysDictTypeUpdateReq req);
    SysDictType toEntity(SysDictTypeQueryReq req);
    SysDictTypeResp toResp(SysDictType dictType);
    List<SysDictTypeResp> toRespList(List<SysDictType> dictTypes);
}