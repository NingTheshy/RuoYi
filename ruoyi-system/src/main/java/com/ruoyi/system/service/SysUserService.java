package com.ruoyi.system.service;

import com.ruoyi.common.core.domain.PageResult;
import com.ruoyi.system.domain.dto.req.RegisterReq;
import com.ruoyi.system.domain.dto.req.SysUserCreateReq;
import com.ruoyi.system.domain.dto.req.SysUserQueryReq;
import com.ruoyi.system.domain.dto.req.SysUserUpdateReq;
import com.ruoyi.system.domain.dto.resp.SysUserResp;
import com.ruoyi.system.domain.entity.SysUser;

import java.util.List;

/**
 * 用户业务服务接口
 * <p>
 * 定义用户管理的业务方法，包括 CRUD、密码管理和状态管理。
 * </p>
 *
 * @author NingTheshy
 */
public interface SysUserService {

    SysUser getUserEntityByUserName(String userName);

    SysUser getUserEntityById(Long userId);

    SysUserResp getUserDetail(Long userId);

    List<SysUserResp> getUserList(SysUserQueryReq queryReq);

    PageResult<SysUserResp> getUserPage(SysUserQueryReq queryReq, Integer pageNum, Integer pageSize);

    Long registerUser(RegisterReq req);

    int createUser(SysUserCreateReq req);

    int updateUser(SysUserUpdateReq req);

    int deleteUserByIds(Long[] userIds);

    int resetPassword(Long userId, String password);

    int updateUserStatus(Long userId, String status);

    int updateUserLoginInfo(Long userId, String loginIp);
}
