package com.ruoyi.admin.web.auth;

import com.ruoyi.common.core.constant.Constants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.security.config.RuoyiProperties;
import com.ruoyi.system.domain.dto.req.LoginReq;
import com.ruoyi.system.domain.dto.req.RegisterReq;
import com.ruoyi.system.domain.dto.resp.AuthInfoResp;
import com.ruoyi.system.domain.dto.resp.LoginResp;
import com.ruoyi.system.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@Tag(name = "认证接口")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    private final RuoyiProperties ruoyiProperties;

    public AuthController(AuthService authService,
                          RuoyiProperties ruoyiProperties) {
        this.authService = authService;
        this.ruoyiProperties = ruoyiProperties;
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public R<Void> register(@Valid @RequestBody RegisterReq registerBody) {
        log.info("[注册] 收到注册请求: username={}, nickname={}, email={}",
                registerBody.getUsername(), registerBody.getNickname(), registerBody.getEmail());
        Long userId = authService.register(registerBody);
        log.info("[注册] 用户创建成功: userId={}, username={}", userId, registerBody.getUsername());
        log.info("[注册] 默认角色分配成功: userId={}, roleId={}", userId, Constants.DEFAULT_ROLE_ID);

        return R.ok();
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public R<LoginResp> login(@Valid @RequestBody LoginReq loginBody, HttpServletRequest request) {
        log.info("[登录] 收到登录请求: username={}, IP={}", loginBody.getUsername(), request.getRemoteAddr());
        LoginResp loginResp = authService.login(loginBody, request.getRemoteAddr());
        log.info("[登录] 认证成功: username={}", loginBody.getUsername());
        return R.ok(loginResp);
    }

    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public R<Void> logout(HttpServletRequest request) {
        authService.logout(request.getHeader(ruoyiProperties.getSecurity().getTokenHeader()));
        return R.ok();
    }

    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "获取当前登录用户信息")
    @GetMapping("/info")
    public R<AuthInfoResp> getInfo() {
        return R.ok(authService.getAuthInfo());
    }
}
