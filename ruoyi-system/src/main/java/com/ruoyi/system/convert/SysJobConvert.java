package com.ruoyi.system.convert;

import com.ruoyi.system.domain.dto.req.SysJobCreateReq;
import com.ruoyi.system.domain.dto.req.SysJobQueryReq;
import com.ruoyi.system.domain.dto.req.SysJobUpdateReq;
import com.ruoyi.system.domain.dto.resp.SysJobResp;
import com.ruoyi.system.domain.entity.SysJob;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapStructMapperConfig.class)
public interface SysJobConvert {
    SysJob toEntity(SysJobCreateReq req);
    SysJob toEntity(SysJobUpdateReq req);
    SysJob toEntity(SysJobQueryReq req);
    SysJobResp toResp(SysJob job);
    List<SysJobResp> toRespList(List<SysJob> jobs);
}
