package com.ruoyi.system.domain.dto;

import lombok.Data;

/**
 * 菜单管理 - 查询条件传输对象
 *
 * <p>用于菜单列表查询接口的筛选参数。</p>
 *
 * @author ruoyi
 */
@Data
public class SysMenuQueryDTO {

    /** 菜单名称（模糊查询） */
    private String menuName;

    /** 菜单状态（0=正常 1=停用） */
    private String status;

    /** 菜单显示状态（0=显示 1=隐藏） */
    private String visible;
}
