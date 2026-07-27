package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.ruoyi.common.core.constant.Constants;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 部门实体类
 * <p>
 * 对应数据库表 sys_dept，采用树形结构管理：
 * </p>
 * <ul>
 *   <li>parentId - 父部门 ID（顶级部门为 0）</li>
 *   <li>ancestors - 祖级列表（如 "0,100,101"，用于快速查询所有子部门）</li>
 *   <li>children - 子部门列表（非数据库字段，用于前端树形展示）</li>
 * </ul>
 *
 * <p>删除策略：逻辑删除（del_flag: 0=正常, 2=已删除）</p>
 *
 * @author NingTheshy
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dept")
public class SysDept extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 部门 ID（自增主键） */
    @TableId(type = IdType.AUTO)
    private Long deptId;

    /** 父部门 ID（顶级部门为 0） */
    private Long parentId;

    /** 祖级列表（如 "0,100,101"，用逗号分隔的祖先部门 ID） */
    private String ancestors;

    /** 部门名称 */
    private String deptName;

    /** 显示顺序（值越小越靠前） */
    private Integer orderNum;

    /** 负责人 */
    private String leader;

    /** 联系电话 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 状态（"0"=正常, "1"=停用） */
    private String status;

    /** 删除标志（"0"=正常, "2"=已删除，MyBatis-Plus 逻辑删除） */
    @TableLogic(value = Constants.DEL_FLAG_NORMAL, delval = Constants.DEL_FLAG_DELETED)
    private String delFlag;

    /** 子部门列表（非数据库字段，用于树形结构返回） */
    @TableField(exist = false)
    private List<SysDept> children = new ArrayList<>();
}
