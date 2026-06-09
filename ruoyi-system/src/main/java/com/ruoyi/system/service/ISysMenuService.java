package com.ruoyi.system.service;

import com.ruoyi.system.domain.entity.SysMenu;
import com.ruoyi.system.domain.vo.MenuTree;

import java.util.List;
import java.util.Set;

public interface ISysMenuService {

    List<SysMenu> selectMenuList(SysMenu menu);

    SysMenu selectMenuById(Long menuId);

    int insertMenu(SysMenu menu);

    int updateMenu(SysMenu menu);

    int deleteMenuById(Long menuId);

    Set<String> selectMenuPermsByUserId(Long userId);

    List<MenuTree> selectMenuTree();

    List<Long> selectMenuIdsByRoleId(Long roleId);
}
