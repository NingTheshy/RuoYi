package com.ruoyi.system.convert;

import com.ruoyi.system.domain.dto.req.SysDeptCreateReq;
import com.ruoyi.system.domain.dto.req.SysDeptQueryReq;
import com.ruoyi.system.domain.dto.req.SysDeptUpdateReq;
import com.ruoyi.system.domain.dto.resp.SysDeptResp;
import com.ruoyi.system.domain.entity.SysDept;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapStructMapperConfig.class)
public interface SysDeptConvert {
    SysDept toEntity(SysDeptCreateReq req);
    SysDept toEntity(SysDeptUpdateReq req);
    SysDept toEntity(SysDeptQueryReq req);
    SysDeptResp toResp(SysDept dept);
    List<SysDeptResp> toRespList(List<SysDept> depts);
}
