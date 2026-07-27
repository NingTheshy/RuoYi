package com.ruoyi.system.convert;

import com.ruoyi.system.domain.dto.req.SysMenuCreateReq;
import com.ruoyi.system.domain.dto.req.SysMenuQueryReq;
import com.ruoyi.system.domain.dto.req.SysMenuUpdateReq;
import com.ruoyi.system.domain.dto.resp.MenuTreeResp;
import com.ruoyi.system.domain.dto.resp.SysMenuResp;
import com.ruoyi.system.domain.entity.SysMenu;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(config = MapStructMapperConfig.class)
public interface SysMenuConvert {
    SysMenu toEntity(SysMenuCreateReq req);
    SysMenu toEntity(SysMenuUpdateReq req);
    SysMenu toEntity(SysMenuQueryReq req);
    SysMenuResp toResp(SysMenu menu);
    List<SysMenuResp> toRespList(List<SysMenu> menus);

    @Mapping(source = "menuId", target = "id")
    @Mapping(source = "menuName", target = "label")
    MenuTreeResp toTreeResp(SysMenu menu);

    List<MenuTreeResp> toTreeRespList(List<SysMenu> menus);
}
