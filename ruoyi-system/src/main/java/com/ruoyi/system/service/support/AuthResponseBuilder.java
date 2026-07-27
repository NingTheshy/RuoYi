package com.ruoyi.system.service.support;

import com.ruoyi.common.security.config.JwtProperties;
import com.ruoyi.system.convert.AuthConvert;
import com.ruoyi.system.convert.SysUserConvert;
import com.ruoyi.system.domain.dto.resp.AuthInfoResp;
import com.ruoyi.system.domain.dto.resp.LoginResp;
import com.ruoyi.system.domain.entity.SysUser;
import com.ruoyi.system.service.SysMenuService;
import com.ruoyi.system.service.SysRoleService;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Set;

/**
 * 认证响应构建器
 */
@Component
public class AuthResponseBuilder {

    private final SysRoleService roleService;
    private final SysMenuService menuService;
    private final AuthConvert authConvert;
    private final SysUserConvert userConvert;
    private final JwtProperties jwtProperties;

    public AuthResponseBuilder(SysRoleService roleService,
                               SysMenuService menuService,
                               AuthConvert authConvert,
                               SysUserConvert userConvert,
                               JwtProperties jwtProperties) {
        this.roleService = roleService;
        this.menuService = menuService;
        this.authConvert = authConvert;
        this.userConvert = userConvert;
        this.jwtProperties = jwtProperties;
    }

    public LoginResp buildLoginResp(SysUser user, String token, String requestIp) {
        LoginResp loginResp = authConvert.toLoginResp(user);
        LocalDateTime currentTime = LocalDateTime.now();
        loginResp.setToken(token);
        loginResp.setLoginTime(currentTime);
        loginResp.setExpireTime(currentTime.plus(Duration.ofMillis(jwtProperties.getExpiration())));
        loginResp.setIp(requestIp);
        loginResp.setAddress(requestIp);

        Set<String> roleKeys = roleService.getRoleKeysByUserId(user.getUserId());
        Set<String> perms = menuService.getMenuPermsByUserId(user.getUserId());
        loginResp.setRoles(new ArrayList<>(roleKeys));
        loginResp.setPermissions(new ArrayList<>(perms));
        return loginResp;
    }

    public AuthInfoResp buildAuthInfoResp(SysUser user) {
        Set<String> roleKeys = roleService.getRoleKeysByUserId(user.getUserId());
        Set<String> perms = menuService.getMenuPermsByUserId(user.getUserId());

        AuthInfoResp info = new AuthInfoResp();
        info.setUser(userConvert.toResp(user));
        info.setRoles(new ArrayList<>(roleKeys));
        info.setPermissions(new ArrayList<>(perms));
        return info;
    }
}
