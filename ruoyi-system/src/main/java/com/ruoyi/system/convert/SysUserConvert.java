package com.ruoyi.system.convert;

import com.ruoyi.system.domain.dto.req.SysUserCreateReq;
import com.ruoyi.system.domain.dto.req.SysUserQueryReq;
import com.ruoyi.system.domain.dto.req.SysUserUpdateReq;
import com.ruoyi.system.domain.dto.resp.SysUserResp;
import com.ruoyi.system.domain.entity.SysUser;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapStructMapperConfig.class, uses = SysRoleConvert.class)
public interface SysUserConvert {
    SysUser toEntity(SysUserCreateReq req);
    SysUser toEntity(SysUserUpdateReq req);
    SysUser toEntity(SysUserQueryReq req);
    SysUserResp toResp(SysUser user);
    List<SysUserResp> toRespList(List<SysUser> users);
}
