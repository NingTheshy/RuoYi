package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.entity.SysUser;

import java.util.List;

public interface ISysUserService {

    SysUser selectUserByUserName(String userName);

    SysUser selectUserById(Long userId);

    List<SysUser> selectUserList(SysUser user);

    Page<SysUser> selectUserPage(Page<SysUser> page, SysUser query);

    int insertUser(SysUser user);

    int updateUser(SysUser user);

    int deleteUserByIds(Long[] userIds);

    int resetPassword(Long userId, String password);

    int updateUserStatus(Long userId, String status);

    int updateUserLoginInfo(Long userId, String loginIp);
}
