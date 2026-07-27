package com.ruoyi.common.security.config;

import com.ruoyi.common.core.filter.RequestLogFilter;
import com.ruoyi.common.security.filter.JwtAuthenticationFilter;
import com.ruoyi.common.security.handler.CustomAccessDeniedHandler;
import com.ruoyi.common.security.handler.CustomAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Spring Security 安全配置
 * <p>
 * 核心安全配置类，负责：
 * </p>
 * <ul>
 *   <li>禁用 CSRF（REST API 使用 JWT，无需 CSRF 保护）</li>
 *   <li>配置 CORS（跨域资源共享），支持前端和微信小程序的跨域请求</li>
 *   <li>设置无状态会话（不使用 HttpSession，完全依赖 JWT）</li>
 *   <li>定义公开接口（登录、注册、登出、Swagger 文档）</li>
 *   <li>注册 JWT 认证过滤器</li>
 *   <li>启用方法级权限控制（{@code @PreAuthorize} 注解）</li>
 * </ul>
 *
 * @author NingTheshy
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final RequestLogFilter requestLogFilter;

    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    private final CustomAccessDeniedHandler accessDeniedHandler;

    private final RuoyiProperties ruoyiProperties;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          RequestLogFilter requestLogFilter,
                          CustomAuthenticationEntryPoint authenticationEntryPoint,
                          CustomAccessDeniedHandler accessDeniedHandler,
                          RuoyiProperties ruoyiProperties) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.requestLogFilter = requestLogFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
        this.ruoyiProperties = ruoyiProperties;
    }

    /**
     * 密码编码器（BCrypt 强哈希算法）
     *
     * @return BCryptPasswordEncoder 实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 安全过滤器链配置
     *
     * @param http HttpSecurity 构建器
     * @return 配置好的 SecurityFilterChain
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 禁用 CSRF（REST API + JWT 方案无需 CSRF）
            .csrf(AbstractHttpConfigurer::disable)
            // 启用 CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // 无状态会话（不创建 HttpSession）
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // URL 权限配置
            .authorizeHttpRequests(auth -> auth
                // 公开接口：登录、注册、登出
                .requestMatchers("/auth/login", "/auth/logout", "/auth/register").permitAll()
                // 公开接口：Swagger/Knife4j 文档
                .requestMatchers("/doc.html", "/webjars/**", "/swagger-resources/**", "/v3/api-docs/**").permitAll()
                // 其他所有请求需要认证
                .anyRequest().authenticated()
            )
            // 未认证和权限不足时返回统一 JSON 格式
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
            )
            // 请求日志过滤器（最先执行，记录完整请求生命周期）
            .addFilterBefore(requestLogFilter, UsernamePasswordAuthenticationFilter.class)
            // 在 UsernamePasswordAuthenticationFilter 之前插入 JWT 过滤器
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS 配置源
     * <p>
     * 允许指定来源的跨域请求，支持前端（localhost:3000/5173）和微信小程序。
     * </p>
     *
     * @return CorsConfigurationSource 配置实例
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        List<String> allowedOrigins = ruoyiProperties.getCors().getAllowedOrigins();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        // 预检请求缓存 1 小时
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
