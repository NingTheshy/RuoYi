package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.SysRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysRoleMapper extends BaseMapper<SysRole> {

    List<SysRole> selectRoleList(SysRole role);

    int deleteRoleMenuByRoleIds(Long[] roleIds);

    int insertRoleMenu(@Param("roleId") Long roleId, @Param("menuIds") Long[] menuIds);

    List<String> selectRoleKeysByUserId(Long userId);
}
