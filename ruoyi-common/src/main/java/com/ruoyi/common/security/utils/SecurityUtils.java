package com.ruoyi.common.security.utils;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

/**
 * Spring Security 上下文工具类
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof String principalText && StringUtils.hasText(principalText)) {
            return principalText;
        }
        return null;
    }

    public static Long getRequiredCurrentUserId() {
        String userId = getCurrentUserId();
        if (!StringUtils.hasText(userId)) {
            throw new AuthenticationCredentialsNotFoundException("未认证或认证已过期");
        }
        try {
            return Long.parseLong(userId);
        } catch (NumberFormatException e) {
            throw new AuthenticationCredentialsNotFoundException("当前认证信息无效");
        }
    }

    public static String getCurrentUsernameOrDefault(String defaultValue) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return defaultValue;
        }
        // 优先从 details 读取 username（JwtAuthenticationFilter 将其存入 details）
        // 必须判断类型：未认证时 details 可能是 WebAuthenticationDetails 对象，
        // 其 toString() 会返回超长字符串（74 字符），超出数据库 VARCHAR(64) 限制
        Object details = authentication.getDetails();
        if (details instanceof String username && StringUtils.hasText(username)) {
            return username;
        }
        String username = authentication.getName();
        if (StringUtils.hasText(username) && !"anonymousUser".equals(username)) {
            return username;
        }
        return defaultValue;
    }

    public static String getCurrentUsernameOrAnonymous() {
        return getCurrentUsernameOrDefault("anonymous");
    }

    public static String getCurrentUsernameOrSystem() {
        return getCurrentUsernameOrDefault("system");
    }
}
