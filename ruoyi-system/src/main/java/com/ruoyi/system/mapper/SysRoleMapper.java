package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysRoleMapper {

    List<SysRole> selectRoleList(SysRole role);

    SysRole selectRoleById(Long roleId);

    int insertRole(SysRole role);

    int updateRole(SysRole role);

    int deleteRoleByIds(Long[] roleIds);

    int deleteRoleMenuByRoleIds(Long[] roleIds);

    int insertRoleMenu(@Param("roleId") Long roleId, @Param("menuIds") Long[] menuIds);

    List<String> selectRoleKeysByUserId(Long userId);
}
