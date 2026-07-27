package com.ruoyi.common.security.filter;

import com.ruoyi.common.security.config.RuoyiProperties;
import com.ruoyi.common.security.service.PermissionService;
import com.ruoyi.common.security.service.TokenService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * JWT 认证过滤器
 * <p>
 * 继承 {@link OncePerRequestFilter}，每个请求只执行一次。
 * 负责从请求头中提取 JWT Token，验证其有效性，并将用户信息和权限加载到 Spring Security 上下文中。
 * </p>
 *
 * <p>处理流程：</p>
 * <ol>
 *   <li>从 Authorization 请求头中提取 Bearer Token</li>
 *   <li>调用 {@link TokenService#validateToken} 验证 Token 有效性</li>
 *   <li>从 Token 中解析 userId</li>
 *   <li>通过 {@link PermissionService} 加载用户的权限标识和角色标识</li>
 *   <li>构建 {@link UsernamePasswordAuthenticationToken} 设置到 SecurityContextHolder</li>
 * </ol>
 *
 * <p>后续的 {@code @PreAuthorize} 注解会从 SecurityContextHolder 中读取权限进行校验。</p>
 *
 * @author NingTheshy
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final RuoyiProperties ruoyiProperties;

    private final TokenService tokenService;

    private final PermissionService permissionService;

    public JwtAuthenticationFilter(RuoyiProperties ruoyiProperties,
                                   TokenService tokenService,
                                   PermissionService permissionService) {
        this.ruoyiProperties = ruoyiProperties;
        this.tokenService = tokenService;
        this.permissionService = permissionService;
    }

    /**
     * 过滤器核心逻辑
     *
     * @param request     HTTP 请求
     * @param response    HTTP 响应
     * @param filterChain 过滤器链
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);
        if (StringUtils.hasText(token)) {
            if (!tokenService.validateToken(token)) {
                log.warn("[Token校验] 校验失败: Token 无效或已过期, URI={}", request.getRequestURI());
            } else {
                authenticateRequest(token, request);
            }
        }
        // 继续执行过滤器链（无论是否认证成功都放行，由后续的授权机制决定是否 403）
        filterChain.doFilter(request, response);
    }

    private void authenticateRequest(String token, HttpServletRequest request) {
        try {
            String userId = tokenService.getUserIdFromToken(token);
            String username = tokenService.getUsernameFromToken(token);
            Long userIdValue = Long.parseLong(userId);

            log.info("[Token校验] 校验成功: userId={}, username={}, URI={}", userId, username, request.getRequestURI());

            List<SimpleGrantedAuthority> authorities = new ArrayList<>();

            Set<String> perms = permissionService.getPermsByUserId(userIdValue);
            for (String perm : perms) {
                authorities.add(new SimpleGrantedAuthority(perm));
            }

            Set<String> roleKeys = permissionService.getRoleKeysByUserId(userIdValue);
            for (String roleKey : roleKeys) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + roleKey));
            }

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, authorities);
            authentication.setDetails(username);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("[Token校验] Token 内容非法: {}, URI={}", e.getMessage(), request.getRequestURI());
        }
    }

    /**
     * 从请求头中提取 JWT Token
     * <p>格式：Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...</p>
     *
     * @param request HTTP 请求
     * @return Token 字符串（不含 Bearer 前缀），无效时返回 null
     */
    private String resolveToken(HttpServletRequest request) {
        String tokenHeader = ruoyiProperties.getSecurity().getTokenHeader();
        String tokenPrefix = ruoyiProperties.getSecurity().getTokenPrefix();
        String bearerToken = request.getHeader(tokenHeader);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(tokenPrefix)) {
            return bearerToken.substring(tokenPrefix.length());
        }
        return null;
    }
}
