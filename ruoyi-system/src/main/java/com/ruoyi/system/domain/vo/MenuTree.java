package com.ruoyi.system.domain.vo;

import com.ruoyi.system.domain.entity.SysMenu;
import lombok.Data;

import java.util.List;

@Data
public class MenuTree {

    private Long id;
    private String label;
    private List<MenuTree> children;

    public static MenuTree fromMenu(SysMenu menu) {
        MenuTree tree = new MenuTree();
        tree.setId(menu.getMenuId());
        tree.setLabel(menu.getMenuName());
        return tree;
    }
}
