package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.constant.Constants;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.system.domain.entity.SysRole;
import com.ruoyi.system.mapper.SysRoleMapper;
import com.ruoyi.system.service.ISysRoleService;
import org.springframework.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 角色业务服务实现类
 * <p>
 * 实现角色的 CRUD 操作和角色-菜单关联管理，包含以下业务规则：
 * </p>
 * <ul>
 *   <li>超级管理员角色（ID=1）不可删除</li>
 *   <li>角色创建/编辑时同步管理角色-菜单关联（先删后插）</li>
 *   <li>分页查询支持角色名、角色标识、状态的模糊/精确筛选</li>
 * </ul>
 *
 * @author NingTheshy
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {

    @Override
    public List<SysRole> getRoleList(SysRole role) {
        return baseMapper.selectRoleList(role);
    }

    /**
     * 分页查询角色列表
     * <p>
     * 使用 LambdaQueryWrapper 构建查询条件：
     * - roleName：模糊匹配
     * - roleKey：模糊匹配
     * - status：精确匹配
     * - 排序：roleSort 升序
     * </p>
     */
    @Override
    public Page<SysRole> getRolePage(Page<SysRole> page, SysRole query) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getRoleName()), SysRole::getRoleName, query.getRoleName())
                .like(StringUtils.hasText(query.getRoleKey()), SysRole::getRoleKey, query.getRoleKey())
                .eq(StringUtils.hasText(query.getStatus()), SysRole::getStatus, query.getStatus())
                .orderByAsc(SysRole::getRoleSort);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public SysRole getRoleById(Long roleId) {
        return getById(roleId);
    }

    /**
     * 新增角色（含菜单关联）
     * <p>
     * 1. 保存角色实体（获取自增 ID）
     * 2. 如果有 menuIds，批量插入角色-菜单关联
     * </p>
     */
    @Override
    @Transactional
    public int createRole(SysRole role, Long[] menuIds) {
        save(role);
        if (menuIds != null && menuIds.length > 0) {
            baseMapper.insertRoleMenu(role.getRoleId(), menuIds);
        }
        return 1;
    }

    /**
     * 修改角色（含菜单关联）
     * <p>
     * 1. 更新角色实体
     * 2. 删除旧的角色-菜单关联
     * 3. 如果有 menuIds，批量插入新的角色-菜单关联
     * </p>
     */
    @Override
    @Transactional
    public int updateRole(SysRole role, Long[] menuIds) {
        updateById(role);
        // 先删除旧的菜单关联
        baseMapper.deleteRoleMenuByRoleIds(new Long[]{role.getRoleId()});
        // 再插入新的菜单关联
        if (menuIds != null && menuIds.length > 0) {
            baseMapper.insertRoleMenu(role.getRoleId(), menuIds);
        }
        return 1;
    }

    /**
     * 批量删除角色
     * <p>
     * 业务规则：
     * - 超级管理员角色（ID=1）不可删除
     * - 删除前先清除角色-菜单关联
     * - 使用逻辑删除（del_flag 设为 2）
     * </p>
     */
    @Override
    @Transactional
    public int deleteRoleByIds(Long[] roleIds) {
        // 校验：不允许删除超级管理员角色
        Arrays.stream(roleIds).forEach(roleId -> {
            if (Constants.SUPER_ADMIN_ROLE_ID.equals(roleId)) {
                throw new ServiceException("不允许删除超级管理员角色");
            }
        });
        // 先删除角色-菜单关联
        baseMapper.deleteRoleMenuByRoleIds(roleIds);
        // 删除用户-角色关联（防止角色删除后产生脏数据）
        baseMapper.deleteUserRoleByRoleIds(roleIds);
        // 再逻辑删除角色
        return removeByIds(Arrays.asList(roleIds)) ? roleIds.length : 0;
    }

    @Override
    public Set<String> getRoleKeysByUserId(Long userId) {
        List<String> roleKeys = baseMapper.selectRoleKeysByUserId(userId);
        return new HashSet<>(roleKeys);
    }
}
