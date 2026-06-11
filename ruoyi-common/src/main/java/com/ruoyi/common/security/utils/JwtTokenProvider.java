package com.ruoyi.common.security.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * JWT Token 工具类
 * <p>
 * 基于 jjwt 库实现 JWT Token 的创建、解析和验证。
 * 使用 HMAC-SHA256 签名算法，密钥从配置文件 {@code jwt.secret} 读取。
 * </p>
 *
 * <p>Token 结构：</p>
 * <ul>
 *   <li>Header: {"alg": "HS256", "typ": "JWT"}</li>
 *   <li>Payload: {"userId": "1000", "iat": ..., "exp": ...}</li>
 *   <li>Signature: HMAC-SHA256(base64(header) + "." + base64(payload), secret)</li>
 * </ul>
 *
 * @author NingTheshy
 */
@Component
public class JwtTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);

    /** JWT 签名密钥（从配置文件读取，至少 256 位。生产环境必须覆盖此默认值） */
    @Value("${jwt.secret:DefaultSecretKeyForDevOnlyMustBeOverriddenInProduction123456}")
    private String secret;

    /** Token 过期时间（毫秒，从配置文件读取） */
    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * 获取 HMAC-SHA256 签名密钥
     *
     * @return SecretKey 实例
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 创建 JWT Token
     *
     * @param claims 自定义声明（如 userId）
     * @return JWT Token 字符串
     */
    public String createToken(Map<String, Object> claims) {
        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 解析 JWT Token，返回 Claims
     *
     * @param token JWT Token 字符串
     * @return Claims 对象（包含所有声明）
     * @throws JwtException Token 无效或过期时抛出
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 从 Token 中提取用户 ID
     *
     * @param token JWT Token
     * @return 用户 ID 字符串
     */
    public String getUserId(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", String.class);
    }

    /**
     * 从 Token 中提取用户名
     *
     * @param token JWT Token
     * @return 用户名字符串
     */
    public String getUsername(String token) {
        Claims claims = parseToken(token);
        return claims.get("username", String.class);
    }

    /**
     * 验证 Token 是否有效（签名正确且未过期）
     *
     * @param token JWT Token
     * @return true 表示有效
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("JWT token验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 检查 Token 是否已过期
     *
     * @param token JWT Token
     * @return true 表示已过期
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }
}
