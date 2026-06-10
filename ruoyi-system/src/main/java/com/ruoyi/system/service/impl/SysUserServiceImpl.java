package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.annotation.DataScope;
import com.ruoyi.common.core.constant.Constants;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.system.domain.entity.SysUser;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public SysUser selectUserByUserName(String userName) {
        return baseMapper.selectUserByUserName(userName);
    }

    @Override
    public SysUser selectUserById(Long userId) {
        return getById(userId);
    }

    @Override
    @DataScope(alias = "", userIdColumn = "user_id", deptIdColumn = "dept_id")
    public List<SysUser> selectUserList(SysUser user) {
        return baseMapper.selectUserList(user);
    }

    @Override
    @DataScope(alias = "", userIdColumn = "user_id", deptIdColumn = "dept_id")
    public Page<SysUser> selectUserPage(Page<SysUser> page, SysUser query) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getUserName()), SysUser::getUserName, query.getUserName())
                .like(StringUtils.hasText(query.getStatus()), SysUser::getStatus, query.getStatus())
                .eq(query.getDeptId() != null, SysUser::getDeptId, query.getDeptId())
                .orderByAsc(SysUser::getCreateTime);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional
    public int insertUser(SysUser user) {
        SysUser existing = baseMapper.selectUserByUserName(user.getUserName());
        if (existing != null) {
            throw new ServiceException("用户名'" + user.getUserName() + "'已存在");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setDelFlag(Constants.DEL_FLAG_NORMAL);
        return save(user) ? 1 : 0;
    }

    @Override
    @Transactional
    public int updateUser(SysUser user) {
        SysUser existing = getById(user.getUserId());
        if (existing == null) {
            throw new ServiceException("用户不存在");
        }
        return updateById(user) ? 1 : 0;
    }

    @Override
    @Transactional
    public int deleteUserByIds(Long[] userIds) {
        Arrays.stream(userIds).forEach(userId -> {
            if (userId == 1L) {
                throw new ServiceException("不允许删除超级管理员");
            }
        });
        return removeByIds(Arrays.asList(userIds)) ? userIds.length : 0;
    }

    @Override
    @Transactional
    public int resetPassword(Long userId, String password) {
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setPassword(passwordEncoder.encode(password));
        return updateById(user) ? 1 : 0;
    }

    @Override
    @Transactional
    public int updateUserStatus(Long userId, String status) {
        if (userId == 1L) {
            throw new ServiceException("不允许停用超级管理员");
        }
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setStatus(status);
        return updateById(user) ? 1 : 0;
    }

    @Override
    public int updateUserLoginInfo(Long userId, String loginIp) {
        return baseMapper.updateUserLoginInfo(userId, loginIp);
    }
}
