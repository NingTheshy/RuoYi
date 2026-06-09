package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysUserMapper {

    SysUser selectUserByUserName(String userName);

    SysUser selectUserById(Long userId);

    List<SysUser> selectUserList(SysUser user);

    int insertUser(SysUser user);

    int updateUser(SysUser user);

    int deleteUserByIds(Long[] userIds);

    int updateUserLoginInfo(@Param("userId") Long userId, @Param("loginIp") String loginIp);

    List<String> selectRoleKeysByUserId(Long userId);

    List<String> selectPermsByUserId(Long userId);
}
