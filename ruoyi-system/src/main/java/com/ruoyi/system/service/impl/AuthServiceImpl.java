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
import com.ruoyi.system.service.SysLoginLogService;
import com.ruoyi.system.service.SysOnlineService;
import com.ruoyi.system.service.SysUserService;
import com.ruoyi.system.service.SysUserRoleService;
import com.ruoyi.system.service.support.AuthResponseBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

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
    private final SysLoginLogService loginLogService;
    private final SysOnlineService onlineService;

    public AuthServiceImpl(LoginService loginService,
                           TokenService tokenService,
                           SysUserService userService,
                           SysUserRoleService userRoleService,
                           AuthResponseBuilder authResponseBuilder,
                           RuoyiProperties ruoyiProperties,
                           SysLoginLogService loginLogService,
                           SysOnlineService onlineService) {
        this.loginService = loginService;
        this.tokenService = tokenService;
        this.userService = userService;
        this.userRoleService = userRoleService;
        this.authResponseBuilder = authResponseBuilder;
        this.ruoyiProperties = ruoyiProperties;
        this.loginLogService = loginLogService;
        this.onlineService = onlineService;
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
        try {
            SysUser user = userService.getUserEntityByUserName(req.getUsername());
            if (user == null) {
                loginLogService.recordLoginLog(req.getUsername(), requestIp, "1", "用户不存在或密码错误");
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
            loginLogService.recordLoginLog(req.getUsername(), requestIp, "0", "登录成功");
            // 存储在线用户信息
            onlineService.storeOnlineUser(user.getUserId(), user.getUserName(), user.getNickName(),
                    user.getDeptName(), requestIp, LocalDateTime.now());
            return loginResp;
        } catch (ServiceException e) {
            // 记录登录失败日志（避免重复记录已在上方处理的场景）
            if (!"用户不存在或密码错误".equals(e.getMessage())) {
                loginLogService.recordLoginLog(req.getUsername(), requestIp, "1", e.getMessage());
            }
            throw e;
        }
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
            onlineService.removeOnlineUser(Long.parseLong(userId));
        }
    }
}
