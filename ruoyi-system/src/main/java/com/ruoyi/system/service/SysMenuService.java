package com.ruoyi.system.service;

import com.ruoyi.system.domain.dto.req.SysMenuCreateReq;
import com.ruoyi.system.domain.dto.req.SysMenuQueryReq;
import com.ruoyi.system.domain.dto.req.SysMenuUpdateReq;
import com.ruoyi.system.domain.dto.resp.MenuTreeResp;
import com.ruoyi.system.domain.dto.resp.SysMenuResp;

import java.util.List;
import java.util.Set;

/**
 * 菜单业务服务接口
 */
public interface SysMenuService {

    List<SysMenuResp> getMenuList(SysMenuQueryReq queryReq);

    SysMenuResp getMenuById(Long menuId);

    int createMenu(SysMenuCreateReq req);

    int updateMenu(SysMenuUpdateReq req);

    int deleteMenuById(Long menuId);

    Set<String> getMenuPermsByUserId(Long userId);

    List<MenuTreeResp> getMenuTreeResp();

    List<Long> getMenuIdsByRoleId(Long roleId);
}
