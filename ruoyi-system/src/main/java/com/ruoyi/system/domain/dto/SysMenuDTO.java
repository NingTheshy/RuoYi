package com.ruoyi.system.domain.dto;

import com.ruoyi.system.domain.entity.SysMenu;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * 菜单管理 - 请求传输对象
 *
 * <p>用于新增和修改菜单接口的请求参数接收，替代直接使用 SysMenu 实体类。</p>
 * <p>菜单类型 menuType 的取值：M=目录、C=菜单、F=按钮。</p>
 *
 * @author ruoyi
 */
@Data
public class SysMenuDTO {

    /** 菜单ID（修改时必填，新增时忽略） */
    private Long menuId;

    /** 菜单名称 */
    @NotBlank(message = "菜单名称不能为空")
    @Size(max = 50, message = "菜单名称长度不能超过 50 个字符")
    private String menuName;

    /** 父菜单ID（0 表示顶级菜单） */
    private Long parentId;

    /** 显示顺序 */
    @NotNull(message = "显示顺序不能为空")
    private Integer orderNum;

    /** 路由地址（目录和菜单类型必填） */
    @Size(max = 200, message = "路由地址长度不能超过 200 个字符")
    private String path;

    /** 组件路径（菜单类型必填） */
    @Size(max = 200, message = "组件路径长度不能超过 200 个字符")
    private String component;

    /** 路由参数 */
    @Size(max = 255, message = "路由参数长度不能超过 255 个字符")
    private String query;

    /** 是否外链（0=否 1=是） */
    private Integer isFrame;

    /** 是否缓存（0=缓存 1=不缓存） */
    private Integer isCache;

    /** 菜单类型（M=目录 C=菜单 F=按钮） */
    @NotBlank(message = "菜单类型不能为空")
    private String menuType;

    /** 菜单状态（0=显示 1=隐藏） */
    private String visible;

    /** 菜单状态（0=正常 1=停用） */
    private String status;

    /** 权限标识（如 system:user:list） */
    @Size(max = 100, message = "权限标识长度不能超过 100 个字符")
    private String perms;

    /** 菜单图标 */
    @Size(max = 100, message = "菜单图标长度不能超过 100 个字符")
    private String icon;

    /** 备注 */
    private String remark;

    /**
     * 转换为实体对象
     *
     * @return SysMenu 实体对象
     */
    public SysMenu toEntity() {
        SysMenu menu = new SysMenu();
        menu.setMenuId(this.menuId);
        menu.setMenuName(this.menuName);
        menu.setParentId(this.parentId);
        menu.setOrderNum(this.orderNum);
        menu.setPath(this.path);
        menu.setComponent(this.component);
        menu.setQuery(this.query);
        menu.setIsFrame(this.isFrame);
        menu.setIsCache(this.isCache);
        menu.setMenuType(this.menuType);
        menu.setVisible(this.visible);
        menu.setStatus(this.status);
        menu.setPerms(this.perms);
        menu.setIcon(this.icon);
        menu.setRemark(this.remark);
        return menu;
    }
}
