package com.ruoyi.system.service;

import com.ruoyi.system.domain.entity.SysUser;

import java.util.List;

public interface ISysUserService {

    SysUser selectUserByUserName(String userName);

    SysUser selectUserById(Long userId);

    List<SysUser> selectUserList(SysUser user);

    int insertUser(SysUser user);

    int updateUser(SysUser user);

    int deleteUserByIds(Long[] userIds);

    int resetPassword(Long userId, String password);

    int updateUserStatus(Long userId, String status);
}
