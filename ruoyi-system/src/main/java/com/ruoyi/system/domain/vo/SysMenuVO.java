package com.ruoyi.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.system.domain.entity.SysMenu;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单管理 - 响应视图对象
 *
 * <p>用于菜单相关接口的响应数据，过滤掉逻辑删除标识等内部字段。</p>
 * <p>支持树形结构，通过 {@code children} 字段递归嵌套子菜单。</p>
 *
 * @author ruoyi
 */
@Data
public class SysMenuVO {

    /** 菜单ID */
    private Long menuId;

    /** 菜单名称 */
    private String menuName;

    /** 父菜单ID */
    private Long parentId;

    /** 显示顺序 */
    private Integer orderNum;

    /** 路由地址 */
    private String path;

    /** 组件路径 */
    private String component;

    /** 路由参数 */
    private String query;

    /** 是否外链（0=否 1=是） */
    private Integer isFrame;

    /** 是否缓存（0=缓存 1=不缓存） */
    private Integer isCache;

    /** 菜单类型（M=目录 C=菜单 F=按钮） */
    private String menuType;

    /** 菜单状态（0=显示 1=隐藏） */
    private String visible;

    /** 菜单状态（0=正常 1=停用） */
    private String status;

    /** 权限标识 */
    private String perms;

    /** 菜单图标 */
    private String icon;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 子菜单列表（递归结构） */
    private List<SysMenuVO> children = new ArrayList<>();

    /**
     * 从实体对象转换为 VO
     *
     * @param menu 菜单实体
     * @return 菜单VO，实体为 null 时返回 null
     */
    public static SysMenuVO fromEntity(SysMenu menu) {
        if (menu == null) {
            return null;
        }
        SysMenuVO vo = new SysMenuVO();
        vo.setMenuId(menu.getMenuId());
        vo.setMenuName(menu.getMenuName());
        vo.setParentId(menu.getParentId());
        vo.setOrderNum(menu.getOrderNum());
        vo.setPath(menu.getPath());
        vo.setComponent(menu.getComponent());
        vo.setQuery(menu.getQuery());
        vo.setIsFrame(menu.getIsFrame());
        vo.setIsCache(menu.getIsCache());
        vo.setMenuType(menu.getMenuType());
        vo.setVisible(menu.getVisible());
        vo.setStatus(menu.getStatus());
        vo.setPerms(menu.getPerms());
        vo.setIcon(menu.getIcon());
        vo.setRemark(menu.getRemark());
        vo.setCreateTime(menu.getCreateTime());
        // 递归转换子菜单
        if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
            vo.setChildren(menu.getChildren().stream()
                    .map(SysMenuVO::fromEntity)
                    .collect(Collectors.toList()));
        }
        return vo;
    }

    /**
     * 批量转换
     *
     * @param menus 菜单实体列表
     * @return 菜单VO列表
     */
    public static List<SysMenuVO> fromEntityList(List<SysMenu> menus) {
        if (menus == null) {
            return List.of();
        }
        return menus.stream().map(SysMenuVO::fromEntity).collect(Collectors.toList());
    }
}
