package com.ruoyi.system.convert;

import com.ruoyi.system.domain.dto.req.SysDictDataCreateReq;
import com.ruoyi.system.domain.dto.req.SysDictDataQueryReq;
import com.ruoyi.system.domain.dto.req.SysDictDataUpdateReq;
import com.ruoyi.system.domain.dto.resp.DictDataOptionResp;
import com.ruoyi.system.domain.dto.resp.SysDictDataResp;
import com.ruoyi.system.domain.entity.SysDictData;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapStructMapperConfig.class)
public interface SysDictDataConvert {
    SysDictData toEntity(SysDictDataCreateReq req);
    SysDictData toEntity(SysDictDataUpdateReq req);
    SysDictData toEntity(SysDictDataQueryReq req);
    SysDictDataResp toResp(SysDictData dictData);
    List<SysDictDataResp> toRespList(List<SysDictData> dictDataList);
    DictDataOptionResp toOptionResp(SysDictData dictData);
    List<DictDataOptionResp> toOptionRespList(List<SysDictData> dictDataList);
}