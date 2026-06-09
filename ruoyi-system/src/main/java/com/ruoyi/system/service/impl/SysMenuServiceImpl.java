package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.entity.SysMenu;
import com.ruoyi.system.domain.vo.MenuTree;
import com.ruoyi.system.mapper.SysMenuMapper;
import com.ruoyi.system.service.ISysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SysMenuServiceImpl implements ISysMenuService {

    @Autowired
    private SysMenuMapper menuMapper;

    @Override
    public List<SysMenu> selectMenuList(SysMenu menu) {
        return menuMapper.selectMenuList(menu);
    }

    @Override
    public SysMenu selectMenuById(Long menuId) {
        return menuMapper.selectMenuById(menuId);
    }

    @Override
    @Transactional
    public int insertMenu(SysMenu menu) {
        return menuMapper.insertMenu(menu);
    }

    @Override
    @Transactional
    public int updateMenu(SysMenu menu) {
        return menuMapper.updateMenu(menu);
    }

    @Override
    @Transactional
    public int deleteMenuById(Long menuId) {
        return menuMapper.deleteMenuById(menuId);
    }

    @Override
    public Set<String> selectMenuPermsByUserId(Long userId) {
        List<String> perms = menuMapper.selectMenuPermsByUserId(userId);
        return new HashSet<>(perms);
    }

    @Override
    public List<MenuTree> selectMenuTree() {
        List<SysMenu> menus = menuMapper.selectMenuList(new SysMenu());
        return buildMenuTree(menus);
    }

    @Override
    public List<Long> selectMenuIdsByRoleId(Long roleId) {
        return menuMapper.selectMenuIdsByRoleId(roleId);
    }

    private List<MenuTree> buildMenuTree(List<SysMenu> menus) {
        List<MenuTree> trees = new ArrayList<>();

        for (SysMenu menu : menus) {
            if (menu.getParentId() == 0) {
                trees.add(MenuTree.fromMenu(menu));
            }
        }

        for (MenuTree tree : trees) {
            tree.setChildren(buildMenuChildren(tree.getId(), menus));
        }

        return trees;
    }

    private List<MenuTree> buildMenuChildren(Long parentId, List<SysMenu> menus) {
        List<MenuTree> children = new ArrayList<>();
        for (SysMenu menu : menus) {
            if (menu.getParentId().equals(parentId)) {
                MenuTree child = MenuTree.fromMenu(menu);
                child.setChildren(buildMenuChildren(menu.getMenuId(), menus));
                children.add(child);
            }
        }
        return children;
    }
}
