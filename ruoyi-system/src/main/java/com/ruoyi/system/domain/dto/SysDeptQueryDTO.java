package com.ruoyi.system.domain.dto;

import lombok.Data;

/**
 * 部门管理 - 查询条件传输对象
 *
 * <p>用于部门列表查询接口的筛选参数。</p>
 *
 * @author ruoyi
 */
@Data
public class SysDeptQueryDTO {

    /** 部门名称（模糊查询） */
    private String deptName;

    /** 部门状态（0=正常 1=停用） */
    private String status;
}
