package com.ruoyi.system.domain.vo;

import com.ruoyi.system.domain.entity.SysMenu;
import lombok.Data;

import java.util.List;

/**
 * 菜单树节点（VO）
 * <p>
 * 用于前端菜单树形选择器的数据结构（如角色分配菜单时的树形控件）。
 * 只包含 id 和 label 两个字段，简化前端渲染。
 * </p>
 *
 * <p>使用方式：</p>
 * <pre>
 * MenuTreeVO tree = MenuTreeVO.fromMenu(sysMenu);
 * tree.setChildren(childTrees);
 * </pre>
 *
 * @author NingTheshy
 */
@Data
public class MenuTreeVO {

    /** 菜单 ID（对应 SysMenu.menuId） */
    private Long id;

    /** 菜单名称（对应 SysMenu.menuName，前端树节点显示文本） */
    private String label;

    /** 子节点列表 */
    private List<MenuTreeVO> children;

    /**
     * 从 SysMenu 实例转换为 MenuTreeVO
     *
     * @param menu 菜单实体
     * @return MenuTreeVO 实例
     */
    public static MenuTreeVO fromMenu(SysMenu menu) {
        MenuTreeVO tree = new MenuTreeVO();
        tree.setId(menu.getMenuId());
        tree.setLabel(menu.getMenuName());
        return tree;
    }
}
