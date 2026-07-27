package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.datascope.annotation.DataScope;
import com.ruoyi.common.core.constant.Constants;
import com.ruoyi.common.core.domain.PageResult;
import com.ruoyi.common.core.enums.StatusFlag;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.system.convert.AuthConvert;
import com.ruoyi.system.convert.SysUserConvert;
import com.ruoyi.system.domain.dto.req.RegisterReq;
import com.ruoyi.system.domain.dto.req.SysUserCreateReq;
import com.ruoyi.system.domain.dto.req.SysUserQueryReq;
import com.ruoyi.system.domain.dto.req.SysUserUpdateReq;
import com.ruoyi.system.domain.dto.resp.SysUserResp;
import com.ruoyi.system.domain.entity.SysUser;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.system.service.SysUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 用户业务服务实现类
 * <p>
 * 实现用户的 CRUD 操作、密码管理和状态管理，包含以下业务规则：
 * </p>
 * <ul>
 *   <li>用户名唯一性校验</li>
 *   <li>密码 BCrypt 加密存储</li>
 *   <li>超级管理员（ID=1）不可删除/停用</li>
 *   <li>逻辑删除（del_flag 设为 2）</li>
 *   <li>用户列表查询支持数据权限过滤</li>
 * </ul>
 *
 * @author NingTheshy
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private static final Logger log = LoggerFactory.getLogger(SysUserServiceImpl.class);

    private final PasswordEncoder passwordEncoder;

    private final SysUserConvert userConvert;

    private final AuthConvert authConvert;

    public SysUserServiceImpl(PasswordEncoder passwordEncoder,
                              SysUserConvert userConvert,
                              AuthConvert authConvert) {
        this.passwordEncoder = passwordEncoder;
        this.userConvert = userConvert;
        this.authConvert = authConvert;
    }

    @Override
    public SysUser getUserEntityByUserName(String userName) {
        return baseMapper.selectUserByUserName(userName);
    }

    @Override
    public SysUser getUserEntityById(Long userId) {
        return getById(userId);
    }

    @Override
    public SysUserResp getUserDetail(Long userId) {
        SysUser user = getById(userId);
        if (user == null) {
            throw new ServiceException(404, "用户不存在");
        }
        return userConvert.toResp(user);
    }

    /**
     * 查询用户列表（支持数据权限过滤）
     * <p>@DataScope 注解会根据当前用户角色自动注入 SQL 条件</p>
     */
    @Override
    @DataScope(alias = "", userIdColumn = "user_id", deptIdColumn = "dept_id")
    public List<SysUserResp> getUserList(SysUserQueryReq queryReq) {
        SysUser user = userConvert.toEntity(queryReq);
        return userConvert.toRespList(baseMapper.selectUserList(user));
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
    public PageResult<SysUserResp> getUserPage(SysUserQueryReq queryReq, Integer pageNum, Integer pageSize) {
        SysUser query = userConvert.toEntity(queryReq);
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getUserName()), SysUser::getUserName, query.getUserName())
                .like(StringUtils.hasText(query.getNickName()), SysUser::getNickName, query.getNickName())
                .like(StringUtils.hasText(query.getPhonenumber()), SysUser::getPhonenumber, query.getPhonenumber())
                .eq(StringUtils.hasText(query.getStatus()), SysUser::getStatus, query.getStatus())
                .eq(query.getDeptId() != null, SysUser::getDeptId, query.getDeptId())
                .ge(query.getBeginTime() != null, SysUser::getCreateTime, query.getBeginTime())
                .le(query.getEndTime() != null, SysUser::getCreateTime, query.getEndTime())
                .orderByAsc(SysUser::getCreateTime);
        Page<SysUser> result = baseMapper.selectPage(page, wrapper);
        return new PageResult<>(userConvert.toRespList(result.getRecords()), result.getTotal());
    }

    @Override
    @Transactional
    public Long registerUser(RegisterReq req) {
        SysUser user = authConvert.toEntity(req);
        user.setStatus(StatusFlag.NORMAL.getCode());
        log.info("[用户] 注册用户: username={}, nickname={}", user.getUserName(), user.getNickName());
        prepareNewUser(user);
        saveNewUser(user, "注册");
        log.info("[用户] 注册成功: userId={}, username={}", user.getUserId(), user.getUserName());
        return user.getUserId();
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
    public int createUser(SysUserCreateReq req) {
        SysUser user = userConvert.toEntity(req);
        log.info("[用户] 创建用户: username={}, nickname={}", user.getUserName(), user.getNickName());
        prepareNewUser(user);
        saveNewUser(user, "创建");
        log.info("[用户] 创建成功: userId={}, username={}", user.getUserId(), user.getUserName());
        return 1;
    }

    @Override
    @Transactional
    public int updateUser(SysUserUpdateReq req) {
        SysUser user = userConvert.toEntity(req);
        log.info("[用户] 修改用户: userId={}", user.getUserId());
        SysUser existing = getById(user.getUserId());
        if (existing == null) {
            log.warn("[用户] 修改失败: 用户不存在, userId={}", user.getUserId());
            throw new ServiceException(404, "用户不存在");
        }
        assertUsernameUnique(user.getUserName(), user.getUserId());
        // 清除密码字段，防止通过修改接口存储明文密码
        // 密码修改必须通过 resetPassword 接口（会经过 BCrypt 加密）
        user.setPassword(null);
        boolean success = updateById(user);
        if (!success) {
            log.error("[用户] 修改失败: userId={}, 数据库更新异常", user.getUserId());
            throw new ServiceException(500, "修改用户失败");
        }
        log.info("[用户] 修改成功: userId={}", user.getUserId());
        return 1;
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
        ensureUserExists(userId);
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setPassword(passwordEncoder.encode(password));
        if (!updateById(user)) {
            throw new ServiceException(500, "重置密码失败");
        }
        return 1;
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
        ensureUserExists(userId);
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setStatus(status);
        if (!updateById(user)) {
            throw new ServiceException(500, "修改用户状态失败");
        }
        return 1;
    }

    @Override
    public int updateUserLoginInfo(Long userId, String loginIp) {
        return baseMapper.updateUserLoginInfo(userId, loginIp, LocalDateTime.now());
    }

    private void prepareNewUser(SysUser user) {
        assertUsernameUnique(user.getUserName(), null);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setDelFlag(Constants.DEL_FLAG_NORMAL);
    }

    private void assertUsernameUnique(String userName, Long excludeUserId) {
        SysUser existing = baseMapper.selectUserByUserName(userName);
        if (existing != null && !existing.getUserId().equals(excludeUserId)) {
            log.warn("[用户] 保存失败: 用户名 '{}' 已存在", userName);
            throw new ServiceException(400, "用户名'" + userName + "'已存在");
        }
    }

    private void saveNewUser(SysUser user, String action) {
        boolean success = save(user);
        if (!success) {
            log.error("[用户] {}失败: username={}, 数据库写入异常", action, user.getUserName());
            throw new ServiceException(500, action + "失败");
        }
    }

    private void ensureUserExists(Long userId) {
        if (getById(userId) == null) {
            throw new ServiceException(404, "用户不存在");
        }
    }
}
