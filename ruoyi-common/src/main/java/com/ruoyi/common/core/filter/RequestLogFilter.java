package com.ruoyi.common.core.filter;

import com.ruoyi.common.security.utils.SecurityUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 请求日志过滤器
 * <p>
 * 在请求进入时记录入口信息（方法、URI、IP、用户），
 * 请求完成时记录出口信息（状态码、耗时）。
 * </p>
 *
 * @author NingTheshy
 */
@Component
public class RequestLogFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestLogFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();

        // 跳过静态资源
        if (isStaticResource(uri)) {
            filterChain.doFilter(request, response);
            return;
        }

        long startTime = System.currentTimeMillis();
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);

        // 请求进入
        String method = request.getMethod();
        String ip = getClientIp(request);

        // 执行请求
        try {
            filterChain.doFilter(requestWrapper, response);
        } finally {
            // 请求完成
            long duration = System.currentTimeMillis() - startTime;
            int status = response.getStatus();
            String username = getCurrentUsername();

            log.info("[请求] {} {} | 状态={} | 耗时={}ms | 用户={} | IP={}",
                    method, uri, status, duration, username, ip);
        }
    }

    private boolean isStaticResource(String uri) {
        return uri.startsWith("/doc.html")
                || uri.startsWith("/webjars/")
                || uri.startsWith("/swagger-resources/")
                || uri.startsWith("/v3/api-docs")
                || uri.endsWith(".css") || uri.endsWith(".js")
                || uri.endsWith(".ico") || uri.endsWith(".png") || uri.endsWith(".jpg");
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    private String getCurrentUsername() {
        return SecurityUtils.getCurrentUsernameOrAnonymous();
    }
}
