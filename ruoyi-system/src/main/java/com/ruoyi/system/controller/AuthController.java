package com.ruoyi.system.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.security.service.LoginService;
import com.ruoyi.common.security.service.TokenService;
import com.ruoyi.system.domain.entity.SysUser;
import com.ruoyi.system.domain.vo.LoginBody;
import com.ruoyi.system.domain.vo.LoginUser;
import com.ruoyi.system.domain.vo.RegisterBody;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.system.service.ISysMenuService;
import com.ruoyi.system.service.ISysRoleService;
import com.ruoyi.system.service.ISysUserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private SysUserMapper userMapper;

    @PostMapping("/register")
    @Transactional
    public R<Void> register(@RequestBody RegisterBody registerBody) {
        if (registerBody.getUsername() == null || registerBody.getUsername().isBlank()) {
            return R.fail("用户名不能为空");
        }
        if (registerBody.getPassword() == null || registerBody.getPassword().isBlank()) {
            return R.fail("密码不能为空");
        }
        if (registerBody.getNickname() == null || registerBody.getNickname().isBlank()) {
            return R.fail("昵称不能为空");
        }

        SysUser user = new SysUser();
        user.setUserName(registerBody.getUsername());
        user.setPassword(registerBody.getPassword());
        user.setNickName(registerBody.getNickname());
        user.setEmail(registerBody.getEmail());
        user.setPhonenumber(registerBody.getPhonenumber());
        user.setStatus("0");

        userService.insertUser(user);

        // 分配默认角色（普通角色 roleId=2）
        userMapper.insertUserRole(user.getUserId(), 2L);

        return R.ok();
    }

    @PostMapping("/login")
    public R<LoginUser> login(@RequestBody LoginBody loginBody, HttpServletRequest request) {
        SysUser user = userService.selectUserByUserName(loginBody.getUsername());
        if (user == null) {
            return R.fail(500, "用户不存在");
        }

        String token = loginService.login(
                loginBody.getUsername(),
                loginBody.getPassword(),
                String.valueOf(user.getUserId()),
                user.getPassword(),
                user.getStatus()
        );

        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getUserId());
        loginUser.setUserName(user.getUserName());
        loginUser.setNickName(user.getNickName());
        loginUser.setToken(token);
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(System.currentTimeMillis() + 86400000L);
        loginUser.setIp(request.getRemoteAddr());
        loginUser.setAddress(request.getRemoteAddr());

        Set<String> roleKeys = roleService.selectRoleKeysByUserId(user.getUserId());
        Set<String> perms = menuService.selectMenuPermsByUserId(user.getUserId());

        loginUser.setRoles(new ArrayList<>(roleKeys));
        loginUser.setPermissions(new ArrayList<>(perms));

        userService.updateUserLoginInfo(user.getUserId(), request.getRemoteAddr());

        return R.ok(loginUser);
    }

    @PostMapping("/logout")
    public R<Void> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            String userId = tokenService.getUserIdFromToken(token);
            if (userId != null) {
                loginService.logout(userId);
            }
        }
        return R.ok();
    }

    @GetMapping("/info")
    public R<Map<String, Object>> getInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = (String) authentication.getPrincipal();

        SysUser user = userService.selectUserById(Long.parseLong(userId));
        if (user == null) {
            return R.fail(500, "用户不存在");
        }

        Set<String> roleKeys = roleService.selectRoleKeysByUserId(user.getUserId());
        Set<String> perms = menuService.selectMenuPermsByUserId(user.getUserId());

        Map<String, Object> info = new HashMap<>();
        info.put("user", user);
        info.put("roles", new ArrayList<>(roleKeys));
        info.put("permissions", new ArrayList<>(perms));

        return R.ok(info);
    }
}
