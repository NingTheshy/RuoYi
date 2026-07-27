package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.constant.Constants;
import com.ruoyi.common.core.domain.PageResult;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.system.convert.SysRoleConvert;
import com.ruoyi.system.domain.dto.req.SysRoleCreateReq;
import com.ruoyi.system.domain.dto.req.SysRoleQueryReq;
import com.ruoyi.system.domain.dto.req.SysRoleUpdateReq;
import com.ruoyi.system.domain.dto.resp.SysRoleResp;
import com.ruoyi.system.domain.entity.SysRole;
import com.ruoyi.system.mapper.SysRoleMapper;
import com.ruoyi.system.service.SysRoleRelationService;
import com.ruoyi.system.service.SysRoleService;
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
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    private final SysRoleConvert roleConvert;
    private final SysRoleRelationService roleRelationService;

    public SysRoleServiceImpl(SysRoleConvert roleConvert,
                              SysRoleRelationService roleRelationService) {
        this.roleConvert = roleConvert;
        this.roleRelationService = roleRelationService;
    }

    @Override
    public List<SysRoleResp> getRoleList(SysRoleQueryReq queryReq) {
        SysRole role = roleConvert.toEntity(queryReq);
        return roleConvert.toRespList(baseMapper.selectRoleList(role));
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
    public PageResult<SysRoleResp> getRolePage(SysRoleQueryReq queryReq, Integer pageNum, Integer pageSize) {
        SysRole query = roleConvert.toEntity(queryReq);
        Page<SysRole> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getRoleName()), SysRole::getRoleName, query.getRoleName())
                .like(StringUtils.hasText(query.getRoleKey()), SysRole::getRoleKey, query.getRoleKey())
                .eq(StringUtils.hasText(query.getStatus()), SysRole::getStatus, query.getStatus())
                .orderByAsc(SysRole::getRoleSort);
        Page<SysRole> result = baseMapper.selectPage(page, wrapper);
        return new PageResult<>(roleConvert.toRespList(result.getRecords()), result.getTotal());
    }

    @Override
    public SysRoleResp getRoleById(Long roleId) {
        SysRole role = getById(roleId);
        if (role == null) {
            throw new ServiceException(404, "角色不存在");
        }
        return roleConvert.toResp(role);
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
    public int createRole(SysRoleCreateReq req) {
        SysRole role = roleConvert.toEntity(req);
        assertRoleKeyUnique(role.getRoleKey(), null);
        if (!save(role)) {
            throw new ServiceException(500, "创建角色失败");
        }
        roleRelationService.replaceRoleMenus(role.getRoleId(), req.getMenuIds());
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
    public int updateRole(SysRoleUpdateReq req) {
        SysRole existing = getById(req.getRoleId());
        if (existing == null) {
            throw new ServiceException(404, "角色不存在");
        }
        SysRole role = roleConvert.toEntity(req);
        assertRoleKeyUnique(role.getRoleKey(), role.getRoleId());
        if (!updateById(role)) {
            throw new ServiceException(500, "修改角色失败");
        }
        roleRelationService.replaceRoleMenus(role.getRoleId(), req.getMenuIds());
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
        roleRelationService.deleteRoleRelations(roleIds);
        // 再逻辑删除角色
        return removeByIds(Arrays.asList(roleIds)) ? roleIds.length : 0;
    }

    @Override
    public Set<String> getRoleKeysByUserId(Long userId) {
        List<String> roleKeys = baseMapper.selectRoleKeysByUserId(userId);
        return new HashSet<>(roleKeys);
    }

    private void assertRoleKeyUnique(String roleKey, Long excludeRoleId) {
        SysRole existing = getOne(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleKey, roleKey)
                .last("limit 1"));
        if (existing != null && !existing.getRoleId().equals(excludeRoleId)) {
            throw new ServiceException(400, "权限字符'" + roleKey + "'已存在");
        }
    }
}
