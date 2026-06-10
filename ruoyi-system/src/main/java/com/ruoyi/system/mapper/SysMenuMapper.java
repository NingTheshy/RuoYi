package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.SysMenu;

import java.util.List;

public interface SysMenuMapper extends BaseMapper<SysMenu> {

    List<SysMenu> selectMenuList(SysMenu menu);

    List<String> selectMenuPermsByUserId(Long userId);

    List<Long> selectMenuIdsByRoleId(Long roleId);
}
