package com.ruoyi.system.convert;

import com.ruoyi.system.domain.dto.req.RegisterReq;
import com.ruoyi.system.domain.dto.resp.LoginResp;
import com.ruoyi.system.domain.entity.SysUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructMapperConfig.class)
public interface AuthConvert {

    @Mapping(source = "username", target = "userName")
    @Mapping(source = "nickname", target = "nickName")
    SysUser toEntity(RegisterReq req);

    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    LoginResp toLoginResp(SysUser user);
}
