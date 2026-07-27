package com.ruoyi.common.security.service;

import com.ruoyi.common.core.constant.Constants;
import com.ruoyi.common.core.enums.StatusFlag;
import com.ruoyi.common.core.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 登录认证服务
 * <p>
 * 处理用户登录和登出的业务逻辑：
 * </p>
 * <ul>
 *   <li>登录：校验用户状态 → 校验密码 → 生成 JWT Token</li>
 *   <li>登出：删除 Redis 中的 Token 缓存</li>
 * </ul>
 *
 * <p>密码校验使用 BCrypt {@link PasswordEncoder#matches} 进行安全比较，
 * 即使密码错误也不会泄露哈希值信息。</p>
 *
 * @author NingTheshy
 */
@Service
public class LoginService {

    private static final Logger log = LoggerFactory.getLogger(LoginService.class);

    private final TokenService tokenService;

    private final PasswordEncoder passwordEncoder;

    public LoginService(TokenService tokenService, PasswordEncoder passwordEncoder) {
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 用户登录
     * <p>
     * 校验流程：用户状态 → 密码匹配 → 生成 Token
     * </p>
     *
     * @param username     用户名
     * @param password     明文密码（用户输入）
     * @param userId       用户 ID（数据库主键）
     * @param realPassword 数据库中的 BCrypt 哈希密码
     * @param status       用户状态（"0"=正常, "1"=停用）
     * @return JWT Token 字符串
     * @throws ServiceException 用户停用或密码错误时抛出
     */
    public String login(String username, String password, String userId,
                        String realPassword, String status) {
        log.info("[认证] 开始校验: username={}, userId={}", username, userId);

        // 检查用户状态
        if (StatusFlag.DISABLED.matches(status)) {
            log.warn("[认证] 用户已停用: username={}, status={}", username, status);
            throw new ServiceException(401, "用户已被停用");
        }

        // 校验密码（BCrypt 安全比较）
        if (!passwordEncoder.matches(password, realPassword)) {
            log.warn("[认证] 密码错误: username={}", username);
            throw new ServiceException(401, "密码错误");
        }

        // 生成 JWT Token 并缓存到 Redis（同时存储 userId 和 username 用于审计字段）
        String token = tokenService.createToken(userId, username);
        log.info("[认证] Token 生成成功: userId={}, username={}", userId, username);
        return token;
    }

    /**
     * 用户登出
     * <p>删除 Redis 中的 Token 缓存，使 Token 立即失效</p>
     *
     * @param userId 用户 ID
     */
    public void logout(String userId) {
        tokenService.removeToken(userId);
    }
}
