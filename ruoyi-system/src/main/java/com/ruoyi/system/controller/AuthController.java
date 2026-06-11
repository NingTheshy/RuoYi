package com.ruoyi.system.controller;

import com.ruoyi.common.core.constant.Constants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.security.service.LoginService;
import com.ruoyi.common.security.service.TokenService;
import com.ruoyi.system.domain.dto.LoginDTO;
import com.ruoyi.system.domain.dto.RegisterDTO;
import com.ruoyi.system.domain.entity.SysUser;
import com.ruoyi.system.domain.vo.LoginVO;
import com.ruoyi.system.domain.vo.SysUserVO;
import com.ruoyi.system.service.ISysMenuService;
import com.ruoyi.system.service.ISysRoleService;
import com.ruoyi.system.service.ISysUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 认证控制器
 * <p>
 * 处理用户认证相关的公开接口，包括：
 * </p>
 * <ul>
 *   <li>POST /auth/register - 用户注册（自动分配默认角色）</li>
 *   <li>POST /auth/login - 用户登录（返回 JWT Token + 用户信息 + 权限列表）</li>
 *   <li>POST /auth/logout - 用户登出（删除 Redis 中的 Token）</li>
 *   <li>GET /auth/info - 获取当前登录用户的详细信息</li>
 * </ul>
 *
 * <p>登录/注册接口无需认证（已在 SecurityConfig 中 permitAll），
 * /info 接口需要携带有效的 JWT Token。</p>
 *
 * @author NingTheshy
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

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

    /** Token 过期时间（从配置文件读取，毫秒） */
    @Value("${jwt.expiration:86400000}")
    private long tokenExpiration;

    /**
     * 用户注册
     * <p>
     * 注册流程：
     * 1. 参数校验（@Valid 触发 Bean Validation）
     * 2. 创建用户（密码 BCrypt 加密）
     * 3. 分配默认角色（DEFAULT_ROLE_ID = 2）
     * </p>
     *
     * @param registerBody 注册请求体（用户名、密码、昵称、邮箱、手机号）
     * @return 注册结果
     */
    @PostMapping("/register")
    @Transactional
    public R<Void> register(@Valid @RequestBody RegisterDTO registerBody) {
        log.info("[注册] 收到注册请求: username={}, nickname={}, email={}",
                registerBody.getUsername(), registerBody.getNickname(), registerBody.getEmail());

        SysUser user = new SysUser();
        user.setUserName(registerBody.getUsername());
        user.setPassword(registerBody.getPassword());
        user.setNickName(registerBody.getNickname());
        user.setEmail(registerBody.getEmail());
        user.setPhonenumber(registerBody.getPhonenumber());
        user.setStatus("0");

        // 创建用户（密码自动 BCrypt 加密）
        userService.createUser(user);
        log.info("[注册] 用户创建成功: userId={}, username={}", user.getUserId(), user.getUserName());

        // 分配默认普通角色
        userService.assignDefaultRole(user.getUserId());
        log.info("[注册] 默认角色分配成功: userId={}, roleId={}", user.getUserId(), Constants.DEFAULT_ROLE_ID);

        return R.ok();
    }

    /**
     * 用户登录
     * <p>
     * 登录流程：
     * 1. 查询用户是否存在
     * 2. 校验用户状态和密码
     * 3. 生成 JWT Token
     * 4. 构建 LoginVO 响应（包含 Token、用户信息、角色列表、权限列表）
     * 5. 记录登录 IP 和时间
     * </p>
     *
     * @param loginBody 登录请求体（用户名、密码）
     * @param request   HTTP 请求（用于获取客户端 IP）
     * @return 登录成功返回 LoginVO，失败返回错误信息
     */
    @PostMapping("/login")
    public R<LoginVO> login(@Valid @RequestBody LoginDTO loginBody, HttpServletRequest request) {
        log.info("[登录] 收到登录请求: username={}, IP={}", loginBody.getUsername(), request.getRemoteAddr());

        // 查询用户
        SysUser user = userService.getUserByUserName(loginBody.getUsername());
        if (user == null) {
            log.warn("[登录] 用户不存在: username={}", loginBody.getUsername());
            return R.fail(401, "用户不存在或密码错误");
        }

        // 登录认证（校验状态 + 密码，成功返回 Token）
        String token = loginService.login(
                loginBody.getUsername(),
                loginBody.getPassword(),
                String.valueOf(user.getUserId()),
                user.getPassword(),
                user.getStatus()
        );
        log.info("[登录] 认证成功: userId={}, username={}", user.getUserId(), user.getUserName());

        // 构建登录响应
        LoginVO loginUser = new LoginVO();
        loginUser.setUserId(user.getUserId());
        loginUser.setUserName(user.getUserName());
        loginUser.setNickName(user.getNickName());
        loginUser.setToken(token);
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(System.currentTimeMillis() + tokenExpiration);
        loginUser.setIp(request.getRemoteAddr());
        loginUser.setAddress(request.getRemoteAddr());

        // 加载用户的角色和权限
        Set<String> roleKeys = roleService.getRoleKeysByUserId(user.getUserId());
        Set<String> perms = menuService.getMenuPermsByUserId(user.getUserId());

        loginUser.setRoles(new ArrayList<>(roleKeys));
        loginUser.setPermissions(new ArrayList<>(perms));
        log.info("[登录] 角色和权限加载完成: roles={}, 权限数量={}", roleKeys, perms.size());

        // 更新登录信息（IP + 时间）
        userService.updateUserLoginInfo(user.getUserId(), request.getRemoteAddr());
        log.info("[登录] 登录信息更新完成: userId={}, loginIp={}", user.getUserId(), request.getRemoteAddr());

        return R.ok(loginUser);
    }

    /**
     * 用户登出
     * <p>从请求头中提取 Token，解析 userId 后删除 Redis 中的 Token 缓存</p>
     *
     * @param request HTTP 请求
     * @return 登出结果
     */
    @PostMapping("/logout")
    public R<Void> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            String userId = tokenService.getUserIdFromToken(token);
            if (userId != null) {
                loginService.logout(userId);
                log.info("[登出] Token 已清除: userId={}", userId);
            }
        }
        return R.ok();
    }

    /**
     * 获取当前登录用户信息
     * <p>
     * 返回用户的详细信息（VO 格式）、角色列表和权限列表，
     * 前端用于动态生成菜单和按钮权限控制。
     * </p>
     *
     * @return 包含 user（SysUserVO）、roles、permissions 的 Map
     */
    @GetMapping("/info")
    public R<Map<String, Object>> getInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = (String) authentication.getPrincipal();
        log.info("[用户信息] 收到请求: userId={}", userId);

        SysUser user = userService.getUserById(Long.parseLong(userId));
        if (user == null) {
            log.warn("[用户信息] 用户不存在: userId={}", userId);
            return R.fail(401, "用户不存在");
        }

        // 加载角色和权限
        Set<String> roleKeys = roleService.getRoleKeysByUserId(user.getUserId());
        Set<String> perms = menuService.getMenuPermsByUserId(user.getUserId());
        log.info("[用户信息] 查询成功: userId={}, username={}, roles={}, 权限数量={}",
                user.getUserId(), user.getUserName(), roleKeys, perms.size());

        // 将实体转换为 VO 返回，避免暴露内部字段
        Map<String, Object> info = new HashMap<>();
        info.put("user", SysUserVO.fromEntity(user));
        info.put("roles", new ArrayList<>(roleKeys));
        info.put("permissions", new ArrayList<>(perms));

        return R.ok(info);
    }
}
