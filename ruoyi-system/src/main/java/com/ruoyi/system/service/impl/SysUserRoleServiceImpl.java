package com.ruoyi.system.service.impl;

import com.ruoyi.common.core.constant.Constants;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.system.service.SysUserRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户-角色关系服务实现
 */
@Service
public class SysUserRoleServiceImpl implements SysUserRoleService {

    private final SysUserMapper userMapper;

    public SysUserRoleServiceImpl(SysUserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public void assignDefaultRole(Long userId) {
        userMapper.insertUserRole(userId, Constants.DEFAULT_ROLE_ID);
    }

    @Override
    @Transactional
    public void updateUserRoles(Long userId, Long[] roleIds) {
        if (Constants.SUPER_ADMIN_USER_ID.equals(userId)) {
            throw new ServiceException("不允许修改超级管理员的角色");
        }
        userMapper.deleteUserRoles(userId);
        if (roleIds != null && roleIds.length > 0) {
            for (Long roleId : roleIds) {
                userMapper.insertUserRole(userId, roleId);
            }
        }
    }

    @Override
    public List<Long> getUserRoleIds(Long userId) {
        return userMapper.selectUserRoleIds(userId);
    }
}
