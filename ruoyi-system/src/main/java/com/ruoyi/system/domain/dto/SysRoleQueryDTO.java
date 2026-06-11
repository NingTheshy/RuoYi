package com.ruoyi.system.domain.dto;

import lombok.Data;

/**
 * 角色管理 - 查询条件传输对象
 *
 * <p>用于角色列表查询接口的筛选参数。</p>
 *
 * @author ruoyi
 */
@Data
public class SysRoleQueryDTO {

    /** 角色名称（模糊查询） */
    private String roleName;

    /** 角色权限标识（模糊查询） */
    private String roleKey;

    /** 角色状态（0=正常 1=停用） */
    private String status;
}
