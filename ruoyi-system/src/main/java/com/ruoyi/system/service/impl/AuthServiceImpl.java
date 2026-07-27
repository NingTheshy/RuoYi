package com.ruoyi.system.service.impl;

import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.security.config.RuoyiProperties;
import com.ruoyi.common.security.service.LoginService;
import com.ruoyi.common.security.service.TokenService;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.system.domain.dto.req.LoginReq;
import com.ruoyi.system.domain.dto.req.RegisterReq;
import com.ruoyi.system.domain.dto.resp.AuthInfoResp;
import com.ruoyi.system.domain.dto.resp.LoginResp;
import com.ruoyi.system.domain.entity.SysUser;
import com.ruoyi.system.service.AuthService;
import com.ruoyi.system.service.SysUserService;
import com.ruoyi.system.service.SysUserRoleService;
import com.ruoyi.system.service.support.AuthResponseBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 认证应用服务实现
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final LoginService loginService;
    private final TokenService tokenService;
    private final SysUserService userService;
    private final SysUserRoleService userRoleService;
    private final AuthResponseBuilder authResponseBuilder;
    private final RuoyiProperties ruoyiProperties;

    public AuthServiceImpl(LoginService loginService,
                           TokenService tokenService,
                           SysUserService userService,
                           SysUserRoleService userRoleService,
                           AuthResponseBuilder authResponseBuilder,
                           RuoyiProperties ruoyiProperties) {
        this.loginService = loginService;
        this.tokenService = tokenService;
        this.userService = userService;
        this.userRoleService = userRoleService;
        this.authResponseBuilder = authResponseBuilder;
        this.ruoyiProperties = ruoyiProperties;
    }

    @Override
    @Transactional
    public Long register(RegisterReq req) {
        Long userId = userService.registerUser(req);
        userRoleService.assignDefaultRole(userId);
        return userId;
    }

    @Override
    public LoginResp login(LoginReq req, String requestIp) {
        SysUser user = userService.getUserEntityByUserName(req.getUsername());
        if (user == null) {
            throw new ServiceException(401, "用户不存在或密码错误");
        }

        String token = loginService.login(
                req.getUsername(),
                req.getPassword(),
                String.valueOf(user.getUserId()),
                user.getPassword(),
                user.getStatus()
        );

        LoginResp loginResp = authResponseBuilder.buildLoginResp(user, token, requestIp);
        userService.updateUserLoginInfo(user.getUserId(), requestIp);
        return loginResp;
    }

    @Override
    public AuthInfoResp getAuthInfo() {
        Long userId = SecurityUtils.getRequiredCurrentUserId();
        SysUser user = userService.getUserEntityById(userId);
        if (user == null) {
            throw new ServiceException(401, "用户不存在");
        }
        return authResponseBuilder.buildAuthInfoResp(user);
    }

    @Override
    public void logout(String authorizationHeader) {
        if (!StringUtils.hasText(authorizationHeader)) {
            return;
        }
        String tokenPrefix = ruoyiProperties.getSecurity().getTokenPrefix();
        if (!authorizationHeader.startsWith(tokenPrefix)) {
            return;
        }
        String token = authorizationHeader.substring(tokenPrefix.length());
        String userId = tokenService.getUserIdFromToken(token);
        if (userId != null) {
            loginService.logout(userId);
        }
    }
}
