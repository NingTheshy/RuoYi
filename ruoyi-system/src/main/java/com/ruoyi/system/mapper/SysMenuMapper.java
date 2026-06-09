package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysMenuMapper {

    List<SysMenu> selectMenuList(SysMenu menu);

    SysMenu selectMenuById(Long menuId);

    int insertMenu(SysMenu menu);

    int updateMenu(SysMenu menu);

    int deleteMenuById(Long menuId);

    List<String> selectMenuPermsByUserId(Long userId);

    List<Long> selectMenuIdsByRoleId(Long roleId);
}
