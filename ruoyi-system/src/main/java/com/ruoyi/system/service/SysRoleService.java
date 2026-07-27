package com.ruoyi.system.service;

import com.ruoyi.common.core.domain.PageResult;
import com.ruoyi.system.domain.dto.req.SysRoleCreateReq;
import com.ruoyi.system.domain.dto.req.SysRoleQueryReq;
import com.ruoyi.system.domain.dto.req.SysRoleUpdateReq;
import com.ruoyi.system.domain.dto.resp.SysRoleResp;
import java.util.List;
import java.util.Set;

/**
 * 角色业务服务接口
 */
public interface SysRoleService {

    List<SysRoleResp> getRoleList(SysRoleQueryReq queryReq);

    PageResult<SysRoleResp> getRolePage(SysRoleQueryReq queryReq, Integer pageNum, Integer pageSize);

    SysRoleResp getRoleById(Long roleId);

    int createRole(SysRoleCreateReq req);

    int updateRole(SysRoleUpdateReq req);

    int deleteRoleByIds(Long[] roleIds);

    Set<String> getRoleKeysByUserId(Long userId);
}
