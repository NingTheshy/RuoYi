package com.ruoyi.system.convert;

import com.ruoyi.system.domain.dto.req.SysRoleCreateReq;
import com.ruoyi.system.domain.dto.req.SysRoleQueryReq;
import com.ruoyi.system.domain.dto.req.SysRoleUpdateReq;
import com.ruoyi.system.domain.dto.resp.SysRoleResp;
import com.ruoyi.system.domain.entity.SysRole;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapStructMapperConfig.class)
public interface SysRoleConvert {
    SysRole toEntity(SysRoleCreateReq req);
    SysRole toEntity(SysRoleUpdateReq req);
    SysRole toEntity(SysRoleQueryReq req);
    SysRoleResp toResp(SysRole role);
    List<SysRoleResp> toRespList(List<SysRole> roles);
}
