package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单实体类
 * <p>
 * 对应数据库表 sys_menu，支持三种类型：
 * </p>
 * <ul>
 *   <li>M - 目录：一级菜单容器，配置 path 和 component</li>
 *   <li>C - 菜单：页面路由，配置 path、component 和 perms</li>
 *   <li>F - 按钮：操作权限，只配置 perms（如 "system:user:add"）</li>
 * </ul>
 *
 * <p>权限控制流程：</p>
 * <ol>
 *   <li>菜单的 perms 字段存储权限标识（如 "system:user:list"）</li>
 *   <li>通过 sys_role_menu 关联角色和菜单</li>
 *   <li>Controller 使用 @PreAuthorize("hasAuthority('system:user:list')") 进行权限校验</li>
 * </ol>
 *
 * @author NingTheshy
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
public class SysMenu extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 菜单 ID（自增主键） */
    @TableId(type = IdType.AUTO)
    private Long menuId;

    /** 菜单名称 */
    private String menuName;

    /** 父菜单 ID（顶级菜单为 0） */
    private Long parentId;

    /** 显示顺序 */
    private Integer orderNum;

    /** 路由地址（如 "/system/user"） */
    private String path;

    /** 组件路径（如 "system/user/index"） */
    private String component;

    /** 路由参数 */
    private String query;

    /** 是否外链（0=否, 1=是） */
    private Integer isFrame;

    /** 是否缓存（0=缓存, 1=不缓存） */
    private Integer isCache;

    /** 菜单类型（M=目录, C=菜单, F=按钮） */
    private String menuType;

    /** 是否显示（0=显示, 1=隐藏） */
    private String visible;

    /** 状态（"0"=正常, "1"=停用） */
    private String status;

    /** 权限标识（如 "system:user:list"，按钮类型必填） */
    private String perms;

    /** 菜单图标 */
    private String icon;

    /** 删除标志（"0"=正常, "2"=已删除，MyBatis-Plus 逻辑删除） */
    @TableLogic(value = "0", delval = "2")
    private String delFlag;

    /** 子菜单列表（非数据库字段，用于树形结构返回） */
    @TableField(exist = false)
    private List<SysMenu> children = new ArrayList<>();
}
