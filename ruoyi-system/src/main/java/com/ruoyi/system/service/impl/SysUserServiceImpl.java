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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * 用户业务服务实现类
 * <p>
 * 实现用户的 CRUD 操作、密码管理、状态管理和角色分配，包含以下业务规则：
 * </p>
 * <ul>
 *   <li>用户名唯一性校验</li>
 *   <li>密码 BCrypt 加密存储</li>
 *   <li>超级管理员（ID=1）不可删除/停用/修改角色</li>
 *   <li>逻辑删除（del_flag 设为 2）</li>
 *   <li>用户列表查询支持数据权限过滤</li>
 * </ul>
 *
 * @author NingTheshy
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    private static final Logger log = LoggerFactory.getLogger(SysUserServiceImpl.class);

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public SysUser getUserByUserName(String userName) {
        return baseMapper.selectUserByUserName(userName);
    }

    @Override
    public SysUser getUserById(Long userId) {
        return getById(userId);
    }

    /**
     * 查询用户列表（支持数据权限过滤）
     * <p>@DataScope 注解会根据当前用户角色自动注入 SQL 条件</p>
     */
    @Override
    @DataScope(alias = "", userIdColumn = "user_id", deptIdColumn = "dept_id")
    public List<SysUser> getUserList(SysUser user) {
        return baseMapper.selectUserList(user);
    }

    /**
     * 分页查询用户列表（支持数据权限过滤）
     * <p>
     * 使用 LambdaQueryWrapper 构建查询条件：
     * - userName：模糊匹配
     * - status：精确匹配
     * - deptId：精确匹配
     * - 排序：createTime 升序
     * </p>
     */
    @Override
    @DataScope(alias = "", userIdColumn = "user_id", deptIdColumn = "dept_id")
    public Page<SysUser> getUserPage(Page<SysUser> page, SysUser query) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getUserName()), SysUser::getUserName, query.getUserName())
                .like(StringUtils.hasText(query.getNickName()), SysUser::getNickName, query.getNickName())
                .like(StringUtils.hasText(query.getPhonenumber()), SysUser::getPhonenumber, query.getPhonenumber())
                .eq(StringUtils.hasText(query.getStatus()), SysUser::getStatus, query.getStatus())
                .eq(query.getDeptId() != null, SysUser::getDeptId, query.getDeptId())
                .ge(StringUtils.hasText(query.getBeginTime()), SysUser::getCreateTime, query.getBeginTime())
                .le(StringUtils.hasText(query.getEndTime()), SysUser::getCreateTime, query.getEndTime())
                .orderByAsc(SysUser::getCreateTime);
        return baseMapper.selectPage(page, wrapper);
    }

    /**
     * 新增用户
     * <p>
     * 业务流程：
     * 1. 校验用户名是否已存在
     * 2. 密码 BCrypt 加密
     * 3. 设置默认删除标志（正常）
     * 4. 保存用户
     * </p>
     */
    @Override
    @Transactional
    public int createUser(SysUser user) {
        log.info("[用户] 创建用户: username={}, nickname={}", user.getUserName(), user.getNickName());
        // 校验用户名唯一性
        SysUser existing = baseMapper.selectUserByUserName(user.getUserName());
        if (existing != null) {
            log.warn("[用户] 创建失败: 用户名 '{}' 已存在", user.getUserName());
            throw new ServiceException("用户名'" + user.getUserName() + "'已存在");
        }
        // 密码 BCrypt 加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setDelFlag(Constants.DEL_FLAG_NORMAL);
        boolean success = save(user);
        if (success) {
            log.info("[用户] 创建成功: userId={}, username={}", user.getUserId(), user.getUserName());
        } else {
            log.error("[用户] 创建失败: username={}, 数据库写入异常", user.getUserName());
        }
        return success ? 1 : 0;
    }

    @Override
    @Transactional
    public int updateUser(SysUser user) {
        log.info("[用户] 修改用户: userId={}", user.getUserId());
        SysUser existing = getById(user.getUserId());
        if (existing == null) {
            log.warn("[用户] 修改失败: 用户不存在, userId={}", user.getUserId());
            throw new ServiceException("用户不存在");
        }
        // 清除密码字段，防止通过修改接口存储明文密码
        // 密码修改必须通过 resetPassword 接口（会经过 BCrypt 加密）
        user.setPassword(null);
        boolean success = updateById(user);
        if (success) {
            log.info("[用户] 修改成功: userId={}", user.getUserId());
        } else {
            log.error("[用户] 修改失败: userId={}, 数据库更新异常", user.getUserId());
        }
        return success ? 1 : 0;
    }

    /**
     * 批量删除用户
     * <p>超级管理员（userId=1）不可删除，采用逻辑删除</p>
     */
    @Override
    @Transactional
    public int deleteUserByIds(Long[] userIds) {
        log.info("[用户] 批量删除: userIds={}", Arrays.toString(userIds));
        // 校验：不允许删除超级管理员
        Arrays.stream(userIds).forEach(userId -> {
            if (Constants.SUPER_ADMIN_USER_ID.equals(userId)) {
                log.warn("[用户] 删除失败: 不允许删除超级管理员, userIds={}", Arrays.toString(userIds));
                throw new ServiceException("不允许删除超级管理员");
            }
        });
        boolean success = removeByIds(Arrays.asList(userIds));
        if (success) {
            log.info("[用户] 删除成功: userIds={}", Arrays.toString(userIds));
        } else {
            log.error("[用户] 删除失败: userIds={}, 数据库删除异常", Arrays.toString(userIds));
        }
        return success ? userIds.length : 0;
    }

    /**
     * 重置用户密码
     * <p>新密码经过 BCrypt 加密后存储</p>
     */
    @Override
    @Transactional
    public int resetPassword(Long userId, String password) {
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setPassword(passwordEncoder.encode(password));
        return updateById(user) ? 1 : 0;
    }

    /**
     * 切换用户状态
     * <p>超级管理员（userId=1）不可停用</p>
     */
    @Override
    @Transactional
    public int updateUserStatus(Long userId, String status) {
        if (Constants.SUPER_ADMIN_USER_ID.equals(userId)) {
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

    /**
     * 分配默认角色
     * <p>注册新用户时自动调用，分配 DEFAULT_ROLE_ID（普通角色，ID=2）</p>
     */
    @Override
    @Transactional
    public void assignDefaultRole(Long userId) {
        baseMapper.insertUserRole(userId, Constants.DEFAULT_ROLE_ID);
    }

    /**
     * 更新用户角色
     * <p>
     * 1. 校验：超级管理员（userId=1）的角色不可修改
     * 2. 删除用户的所有旧角色关联
     * 3. 批量插入新的角色关联
     * </p>
     */
    @Override
    @Transactional
    public void updateUserRoles(Long userId, Long[] roleIds) {
        if (Constants.SUPER_ADMIN_USER_ID.equals(userId)) {
            throw new ServiceException("不允许修改超级管理员的角色");
        }
        // 先删除旧角色关联
        baseMapper.deleteUserRoles(userId);
        // 再插入新角色关联
        if (roleIds != null && roleIds.length > 0) {
            for (Long roleId : roleIds) {
                baseMapper.insertUserRole(userId, roleId);
            }
        }
    }

    @Override
    public List<Long> getUserRoleIds(Long userId) {
        return baseMapper.selectUserRoleIds(userId);
    }
}
