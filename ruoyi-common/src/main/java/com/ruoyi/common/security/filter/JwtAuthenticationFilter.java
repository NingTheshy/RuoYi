package com.ruoyi.common.security.filter;

import com.ruoyi.common.security.service.PermissionService;
import com.ruoyi.common.security.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String TOKEN_HEADER = "Authorization";

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PermissionService permissionService;

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
        try {
            // 1. 提取 Token
            String token = resolveToken(request);
            // 2. 验证 Token 有效性（JWT 签名 + Redis 缓存）
            if (StringUtils.hasText(token) && tokenService.validateToken(token)) {
                // 3. 从 Token 中解析 userId 和 username
                String userId = tokenService.getUserIdFromToken(token);
                String username = tokenService.getUsernameFromToken(token);
                log.info("[Token校验] 校验成功: userId={}, username={}, URI={}", userId, username, request.getRequestURI());

                List<SimpleGrantedAuthority> authorities = new ArrayList<>();

                // 4. 加载用户权限标识（如 system:user:list）
                Set<String> perms = permissionService.getPermsByUserId(Long.parseLong(userId));
                for (String perm : perms) {
                    authorities.add(new SimpleGrantedAuthority(perm));
                }

                // 4. 加载用户角色标识（如 ROLE_admin，需加 ROLE_ 前缀）
                Set<String> roleKeys = permissionService.getRoleKeysByUserId(Long.parseLong(userId));
                for (String roleKey : roleKeys) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + roleKey));
                }

                // 5. 构建认证对象并设置到 SecurityContextHolder
                //    principal = userId, details = username（用于审计字段填充）
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, authorities);
                authentication.setDetails(username);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else if (StringUtils.hasText(token)) {
                log.warn("[Token校验] 校验失败: Token 无效或已过期, URI={}", request.getRequestURI());
            }
        } catch (Exception e) {
            // JWT 解析异常（签名错误、过期等）可以安全忽略，请求会以未认证身份继续
            // 但数据库/Redis 等基础设施异常应该向上抛出，避免静默丢失权限
            if (e instanceof io.jsonwebtoken.JwtException
                    || e instanceof IllegalArgumentException) {
                log.warn("[Token校验] JWT 解析异常: {}, URI={}", e.getMessage(), request.getRequestURI());
            } else {
                log.error("[Token校验] 系统异常: {}, URI={}", e.getMessage(), request.getRequestURI(), e);
                throw e;
            }
        }
        // 继续执行过滤器链（无论是否认证成功都放行，由后续的授权机制决定是否 403）
        filterChain.doFilter(request, response);
    }

    /**
     * 从请求头中提取 JWT Token
     * <p>格式：Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...</p>
     *
     * @param request HTTP 请求
     * @return Token 字符串（不含 Bearer 前缀），无效时返回 null
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(TOKEN_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}
