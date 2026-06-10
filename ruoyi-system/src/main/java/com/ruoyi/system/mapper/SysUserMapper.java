package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysUserMapper extends BaseMapper<SysUser> {

    SysUser selectUserByUserName(String userName);

    List<SysUser> selectUserList(SysUser user);

    int updateUserLoginInfo(@Param("userId") Long userId, @Param("loginIp") String loginIp);

    List<String> selectRoleKeysByUserId(Long userId);

    List<String> selectPermsByUserId(Long userId);

    int insertUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);
}
