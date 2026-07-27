package com.ruoyi.common.security.service;

import com.ruoyi.common.security.config.JwtProperties;
import com.ruoyi.common.security.utils.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Token 管理服务
 * <p>
 * 负责 JWT Token 的创建、验证、查询和删除。
 * 使用 Redis 缓存 Token 实现以下功能：
 * </p>
 * <ul>
 *   <li>单点登录：同一用户登录后旧 Token 自动失效</li>
 *   <li>主动登出：删除 Redis 缓存使 Token 立即失效</li>
 *   <li>Token 校验：双重验证（JWT 签名 + Redis 缓存匹配）</li>
 * </ul>
 *
 * <p>Redis Key 格式：{@code login_tokens:{userId}}，过期时间与 JWT 一致（默认 24 小时）。</p>
 *
 * @author NingTheshy
 */
@Service
public class TokenService {

    private static final Logger log = LoggerFactory.getLogger(TokenService.class);

    /** Redis Key 前缀 */
    private static final String TOKEN_PREFIX = "login_tokens:";

    private final JwtProperties jwtProperties;

    private final JwtTokenProvider jwtTokenProvider;

    private final RedisTemplate<String, Object> redisTemplate;

    public TokenService(JwtProperties jwtProperties,
                        JwtTokenProvider jwtTokenProvider,
                        RedisTemplate<String, Object> redisTemplate) {
        this.jwtProperties = jwtProperties;
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 创建 JWT Token 并缓存到 Redis
     * <p>
     * 如果用户已登录（Redis 中存在旧 Token），新 Token 会覆盖旧 Token，
     * 实现单点登录效果。
     * </p>
     *
     * @param userId   用户 ID
     * @param username 用户名（存入 JWT 用于审计字段填充）
     * @return 生成的 JWT Token 字符串
     */
    public String createToken(String userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        String token = jwtTokenProvider.createToken(claims);
        long tokenExpiration = jwtProperties.getExpiration();
        // 将 Token 缓存到 Redis，过期时间与 JWT 一致
        redisTemplate.opsForValue().set(TOKEN_PREFIX + userId, token, tokenExpiration, TimeUnit.MILLISECONDS);
        log.info("[Token] 生成成功: userId={}, username={}, 过期时间={}ms", userId, username, tokenExpiration);
        return token;
    }

    /**
     * 从 Token 中解析用户 ID
     *
     * @param token JWT Token
     * @return 用户 ID 字符串
     */
    public String getUserIdFromToken(String token) {
        return jwtTokenProvider.getUserId(token);
    }

    /**
     * 从 Token 中解析用户名
     * <p>用于审计字段（createBy、updateBy）填充</p>
     *
     * @param token JWT Token
     * @return 用户名字符串
     */
    public String getUsernameFromToken(String token) {
        return jwtTokenProvider.getUsername(token);
    }

    /**
     * 验证 Token 的有效性
     * <p>
     * 双重验证：
     * 1. JWT 签名和过期时间验证
     * 2. Redis 缓存匹配验证（确保未被登出或覆盖）
     * </p>
     *
     * @param token JWT Token
     * @return true 表示有效
     */
    public boolean validateToken(String token) {
        // 1. 验证 JWT 签名和过期时间
        if (!jwtTokenProvider.validateToken(token)) {
            return false;
        }
        // 2. 从 Token 中解析 userId
        String userId = jwtTokenProvider.getUserId(token);
        if (userId == null) {
            return false;
        }
        // 3. 与 Redis 缓存中的 Token 比较（确保是最新登录的 Token）
        String cachedToken = getTokenByUserId(userId);
        return token.equals(cachedToken);
    }

    /**
     * 删除 Redis 中的 Token 缓存（登出时调用）
     *
     * @param userId 用户 ID
     */
    public void removeToken(String userId) {
        Boolean deleted = redisTemplate.delete(TOKEN_PREFIX + userId);
        log.info("[Token] 清除: userId={}, deleted={}", userId, deleted);
    }

    /**
     * 从 Redis 中获取用户的 Token
     *
     * @param userId 用户 ID
     * @return Token 字符串，不存在时返回 null
     */
    public String getTokenByUserId(String userId) {
        Object token = redisTemplate.opsForValue().get(TOKEN_PREFIX + userId);
        return token != null ? token.toString() : null;
    }
}
