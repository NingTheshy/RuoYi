package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.entity.SysUser;

import java.util.List;

/**
 * 用户业务服务接口
 * <p>
 * 定义用户管理的业务方法，包括 CRUD、密码管理、状态管理和角色分配。
 * </p>
 *
 * @author NingTheshy
 */
public interface ISysUserService {

    /**
     * 根据用户名查询用户
     *
     * @param userName 用户名
     * @return 用户实体，不存在时返回 null
     */
    SysUser getUserByUserName(String userName);

    /**
     * 根据 ID 查询用户详情
     *
     * @param userId 用户 ID
     * @return 用户实体
     */
    SysUser getUserById(Long userId);

    /**
     * 查询用户列表（支持数据权限过滤）
     *
     * @param user 查询条件
     * @return 用户列表
     */
    List<SysUser> getUserList(SysUser user);

    /**
     * 分页查询用户列表（支持数据权限过滤）
     *
     * @param page  分页参数
     * @param query 查询条件
     * @return 分页结果
     */
    Page<SysUser> getUserPage(Page<SysUser> page, SysUser query);

    /**
     * 新增用户
     * <p>密码自动 BCrypt 加密，校验用户名唯一性</p>
     *
     * @param user 用户实体
     * @return 影响行数
     */
    int createUser(SysUser user);

    /**
     * 修改用户
     *
     * @param user 用户实体（必须包含 userId）
     * @return 影响行数
     */
    int updateUser(SysUser user);

    /**
     * 批量删除用户
     * <p>超级管理员（userId=1）不可删除，采用逻辑删除</p>
     *
     * @param userIds 用户 ID 数组
     * @return 影响行数
     */
    int deleteUserByIds(Long[] userIds);

    /**
     * 重置用户密码
     *
     * @param userId   用户 ID
     * @param password 新密码（明文，内部会 BCrypt 加密）
     * @return 影响行数
     */
    int resetPassword(Long userId, String password);

    /**
     * 切换用户状态
     * <p>超级管理员（userId=1）不可停用</p>
     *
     * @param userId 用户 ID
     * @param status 目标状态（"0"=正常, "1"=停用）
     * @return 影响行数
     */
    int updateUserStatus(Long userId, String status);

    /**
     * 更新用户登录信息
     *
     * @param userId  用户 ID
     * @param loginIp 登录 IP
     * @return 影响行数
     */
    int updateUserLoginInfo(Long userId, String loginIp);

    /**
     * 分配默认角色
     * <p>注册新用户时自动调用，分配 DEFAULT_ROLE_ID（普通角色）</p>
     *
     * @param userId 用户 ID
     */
    void assignDefaultRole(Long userId);

    /**
     * 更新用户角色
     * <p>先删除旧角色关联，再批量插入新角色关联。超级管理员不可修改。</p>
     *
     * @param userId  用户 ID
     * @param roleIds 角色 ID 数组
     */
    void updateUserRoles(Long userId, Long[] roleIds);

    /**
     * 查询用户的角色 ID 列表
     * <p>用于用户编辑时，前端回显已勾选的角色</p>
     *
     * @param userId 用户 ID
     * @return 角色 ID 列表
     */
    List<Long> getUserRoleIds(Long userId);
}
