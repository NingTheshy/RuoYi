package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.system.convert.SysMenuConvert;
import com.ruoyi.system.domain.dto.req.SysMenuCreateReq;
import com.ruoyi.system.domain.dto.req.SysMenuQueryReq;
import com.ruoyi.system.domain.dto.req.SysMenuUpdateReq;
import com.ruoyi.system.domain.dto.resp.MenuTreeResp;
import com.ruoyi.system.domain.dto.resp.SysMenuResp;
import com.ruoyi.system.domain.entity.SysMenu;
import com.ruoyi.system.mapper.SysMenuMapper;
import com.ruoyi.system.service.SysMenuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 菜单业务服务实现类
 * <p>
 * 实现菜单的 CRUD 操作和树形结构构建，包含以下业务规则：
 * </p>
 * <ul>
 *   <li>删除菜单前校验：不能有子菜单</li>
 *   <li>菜单树构建使用 O(n) 算法（groupingBy + 递归）</li>
 *   <li>权限查询返回 Set 去重</li>
 * </ul>
 *
 * @author NingTheshy
 */

@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    private final SysMenuConvert menuConvert;

    public SysMenuServiceImpl(SysMenuConvert menuConvert) {
        this.menuConvert = menuConvert;
    }

    @Override
    public List<SysMenuResp> getMenuList(SysMenuQueryReq queryReq) {
        SysMenu menu = menuConvert.toEntity(queryReq);
        return menuConvert.toRespList(baseMapper.selectMenuList(menu));
    }

    @Override
    public SysMenuResp getMenuById(Long menuId) {
        SysMenu menu = getById(menuId);
        if (menu == null) {
            throw new ServiceException(404, "菜单不存在");
        }
        return menuConvert.toResp(menu);
    }

    @Override
    @Transactional
    public int createMenu(SysMenuCreateReq req) {
        SysMenu menu = menuConvert.toEntity(req);
        return save(menu) ? 1 : 0;
    }

    @Override
    @Transactional
    public int updateMenu(SysMenuUpdateReq req) {
        SysMenu menu = menuConvert.toEntity(req);
        return updateById(menu) ? 1 : 0;
    }

    /**
     * 删除菜单
     * <p>删除前校验：不能有子菜单（parent_id = menuId 的记录数 > 0）</p>
     */
    @Override
    @Transactional
    public int deleteMenuById(Long menuId) {
        Long childCount = count(new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getParentId, menuId));
        if (childCount > 0) {
            throw new ServiceException("存在子菜单，不允许删除");
        }
        return removeById(menuId) ? 1 : 0;
    }

    @Override
    public Set<String> getMenuPermsByUserId(Long userId) {
        List<String> perms = baseMapper.selectMenuPermsByUserId(userId);
        return new HashSet<>(perms);
    }

    /**
     * 获取菜单树
     * <p>
     * 构建流程：
     * 1. 查询所有菜单（平铺列表）
     * 2. 按 parentId 分组（O(n) 时间复杂度）
     * 3. 递归构建树形结构
     * </p>
     */
    @Override
    public List<MenuTreeResp> getMenuTreeResp() {
        List<SysMenu> menus = baseMapper.selectMenuList(new SysMenu());
        return buildMenuTreeResp(menus);
    }

    @Override
    public List<Long> getMenuIdsByRoleId(Long roleId) {
        return baseMapper.selectMenuIdsByRoleId(roleId);
    }

    /**
     * 构建菜单树（O(n) 算法）
     * <p>
     * 使用 Collectors.groupingBy 按 parentId 分组，
     * 避免嵌套循环的 O(n²) 时间复杂度。
     * </p>
     *
     * @param menus 平铺的菜单列表
     * @return 树形结构的 MenuTreeResp 列表
     */
    private List<MenuTreeResp> buildMenuTreeResp(List<SysMenu> menus) {
        // 按 parentId 分组，O(n) 时间复杂度
        Map<Long, List<SysMenu>> parentMap = menus.stream()
                .collect(Collectors.groupingBy(SysMenu::getParentId));
        // 从根节点（parentId=0）开始递归构建
        return buildChildren(0L, parentMap);
    }

    /**
     * 递归构建子节点
     *
     * @param parentId  父节点 ID
     * @param parentMap parentId → 子菜单列表 的映射
     * @return 子节点的 MenuTreeResp 列表
     */
    private List<MenuTreeResp> buildChildren(Long parentId, Map<Long, List<SysMenu>> parentMap) {
        List<SysMenu> children = parentMap.getOrDefault(parentId, Collections.emptyList());
        return children.stream().map(menu -> {
            MenuTreeResp tree = menuConvert.toTreeResp(menu);
            tree.setChildren(buildChildren(menu.getMenuId(), parentMap));
            return tree;
        }).collect(Collectors.toList());
    }
}
