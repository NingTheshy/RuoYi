package com.ruoyi.system.domain.dto;

import lombok.Data;

/**
 * 用户管理 - 查询条件传输对象
 *
 * <p>用于用户列表查询接口的筛选参数，仅包含前端可传递的查询条件字段。</p>
 *
 * @author ruoyi
 */
@Data
public class SysUserQueryDTO {

    /** 用户账号（模糊查询） */
    private String userName;

    /** 用户昵称（模糊查询） */
    private String nickName;

    /** 手机号码（模糊查询） */
    private String phonenumber;

    /** 帐号状态（0=正常 1=停用） */
    private String status;

    /** 部门ID（查询指定部门及下级部门的用户） */
    private Long deptId;

    /** 创建时间 - 开始（格式：yyyy-MM-dd） */
    private String beginTime;

    /** 创建时间 - 结束（格式：yyyy-MM-dd） */
    private String endTime;
}
