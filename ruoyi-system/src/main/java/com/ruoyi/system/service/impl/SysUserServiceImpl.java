package com.ruoyi.system.service.impl;

import com.ruoyi.common.core.constant.Constants;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.system.domain.entity.SysUser;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SysUserServiceImpl implements ISysUserService {

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public SysUser selectUserByUserName(String userName) {
        return userMapper.selectUserByUserName(userName);
    }

    @Override
    public SysUser selectUserById(Long userId) {
        return userMapper.selectUserById(userId);
    }

    @Override
    public List<SysUser> selectUserList(SysUser user) {
        return userMapper.selectUserList(user);
    }

    @Override
    @Transactional
    public int insertUser(SysUser user) {
        SysUser existing = userMapper.selectUserByUserName(user.getUserName());
        if (existing != null) {
            throw new ServiceException("用户名'" + user.getUserName() + "'已存在");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setDelFlag(Constants.DEL_FLAG_NORMAL);
        return userMapper.insertUser(user);
    }

    @Override
    @Transactional
    public int updateUser(SysUser user) {
        SysUser existing = userMapper.selectUserById(user.getUserId());
        if (existing == null) {
            throw new ServiceException("用户不存在");
        }
        return userMapper.updateUser(user);
    }

    @Override
    @Transactional
    public int deleteUserByIds(Long[] userIds) {
        for (Long userId : userIds) {
            if (userId == 1L) {
                throw new ServiceException("不允许删除超级管理员");
            }
        }
        return userMapper.deleteUserByIds(userIds);
    }

    @Override
    @Transactional
    public int resetPassword(Long userId, String password) {
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setPassword(passwordEncoder.encode(password));
        return userMapper.updateUser(user);
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
        return userMapper.updateUser(user);
    }
}
