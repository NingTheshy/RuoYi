package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.entity.SysRole;

import java.util.List;
import java.util.Set;

public interface ISysRoleService {

    List<SysRole> selectRoleList(SysRole role);

    Page<SysRole> selectRolePage(Page<SysRole> page, SysRole query);

    SysRole selectRoleById(Long roleId);

    int insertRole(SysRole role, Long[] menuIds);

    int updateRole(SysRole role, Long[] menuIds);

    int deleteRoleByIds(Long[] roleIds);

    Set<String> selectRoleKeysByUserId(Long userId);
}
