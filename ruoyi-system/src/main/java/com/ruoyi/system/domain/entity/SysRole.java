package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.ruoyi.common.core.constant.Constants;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色实体类
 * <p>
 * 对应数据库表 sys_role，RBAC 模型的核心实体。
 * </p>
 *
 * <p>数据权限（data_scope）：</p>
 * <ul>
 *   <li>1 - 全部数据权限</li>
 *   <li>2 - 自定义数据权限（通过 sys_role_dept 关联）</li>
 *   <li>3 - 本部门数据权限</li>
 *   <li>4 - 本部门及以下数据权限</li>
 *   <li>5 - 仅本人数据权限</li>
 * </ul>
 *
 * @author NingTheshy
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class SysRole extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 角色 ID（自增主键） */
    @TableId(type = IdType.AUTO)
    private Long roleId;

    /** 角色名称（如 "超级管理员"、"普通角色"） */
    private String roleName;

    /** 角色标识（如 "admin"、"common"，用于 @PreAuthorize 的 ROLE_ 前缀匹配） */
    private String roleKey;

    /** 显示顺序（值越小越靠前） */
    private Integer roleSort;

    /** 数据权限范围（1=全部, 2=自定义, 3=本部门, 4=本部门及以下, 5=仅本人） */
    private String dataScope;

    /** 菜单树选择项是否关联（0=父子独立选择, 1=父子关联选择） */
    private Integer menuCheckStrictly;

    /** 部门树选择项是否关联（0=父子独立选择, 1=父子关联选择） */
    private Integer deptCheckStrictly;

    /** 状态（"0"=正常, "1"=停用） */
    private String status;

    /** 删除标志（"0"=正常, "2"=已删除，MyBatis-Plus 逻辑删除） */
    @TableLogic(value = Constants.DEL_FLAG_NORMAL, delval = Constants.DEL_FLAG_DELETED)
    private String delFlag;
}
