package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.entity.SysMenu;
import com.ruoyi.system.domain.vo.MenuTree;
import com.ruoyi.system.mapper.SysMenuMapper;
import com.ruoyi.system.service.ISysMenuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements ISysMenuService {

    @Override
    public List<SysMenu> selectMenuList(SysMenu menu) {
        return baseMapper.selectMenuList(menu);
    }

    @Override
    public SysMenu selectMenuById(Long menuId) {
        return getById(menuId);
    }

    @Override
    @Transactional
    public int insertMenu(SysMenu menu) {
        return save(menu) ? 1 : 0;
    }

    @Override
    @Transactional
    public int updateMenu(SysMenu menu) {
        return updateById(menu) ? 1 : 0;
    }

    @Override
    @Transactional
    public int deleteMenuById(Long menuId) {
        return removeById(menuId) ? 1 : 0;
    }

    @Override
    public Set<String> selectMenuPermsByUserId(Long userId) {
        List<String> perms = baseMapper.selectMenuPermsByUserId(userId);
        return new HashSet<>(perms);
    }

    @Override
    public List<MenuTree> selectMenuTree() {
        List<SysMenu> menus = baseMapper.selectMenuList(new SysMenu());
        return buildMenuTree(menus);
    }

    @Override
    public List<Long> selectMenuIdsByRoleId(Long roleId) {
        return baseMapper.selectMenuIdsByRoleId(roleId);
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
