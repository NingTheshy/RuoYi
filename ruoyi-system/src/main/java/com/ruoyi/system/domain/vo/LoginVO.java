package com.ruoyi.system.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * 登录响应体（VO）
 * <p>
 * 用于 POST /auth/login 和 GET /auth/info 接口的响应数据封装。
 * 包含用户基本信息、JWT Token、登录状态和权限数据。
 * </p>
 *
 * <p>前端根据 permissions 和 roles 字段实现：</p>
 * <ul>
 *   <li>动态菜单渲染</li>
 *   <li>按钮级别权限控制（v-if="permissions.includes('system:user:add')"）</li>
 *   <li>角色级别的功能开关</li>
 * </ul>
 *
 * @author NingTheshy
 */
@Data
public class LoginVO {

    /** 用户 ID */
    private Long userId;

    /** 用户名 */
    private String userName;

    /** 昵称 */
    private String nickName;

    /** JWT Token 字符串 */
    private String token;

    /** 登录时间戳（毫秒） */
    private Long loginTime;

    /** Token 过期时间戳（毫秒） */
    private Long expireTime;

    /** 登录 IP */
    private String ip;

    /** 登录地址 */
    private String address;

    /** 权限标识列表（如 ["system:user:list", "system:role:list"]） */
    private List<String> permissions;

    /** 角色标识列表（如 ["admin", "common"]） */
    private List<String> roles;
}
