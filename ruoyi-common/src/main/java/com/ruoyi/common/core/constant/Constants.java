package com.ruoyi.common.core.constant;

/**
 * 系统全局常量类
 * <p>
 * 集中管理整个系统中使用的常量值，包括：
 * </p>
 * <ul>
 *   <li>Token 相关常量 - JWT 前缀、请求头名称、默认过期时间</li>
 *   <li>状态常量 - 正常/停用状态标识</li>
 *   <li>删除标志常量 - 逻辑删除标识（0=正常, 2=已删除）</li>
 *   <li>菜单类型常量 - 目录(M)/菜单(C)/按钮(F)</li>
 *   <li>系统预设角色/用户 ID - 超级管理员、默认普通角色</li>
 * </ul>
 *
 * @author NingTheshy
 */
public class Constants {

    // ==================== Token 相关 ====================

    /** JWT Token 前缀，Bearer 认证方案 */
    public static final String TOKEN_PREFIX = "Bearer ";

    /** HTTP 请求头中的 Token 键名 */
    public static final String TOKEN_HEADER = "Authorization";

    /** Token 默认过期时间（毫秒），默认 24 小时 */
    public static final long TOKEN_EXPIRATION = 86400000L;

    // ==================== 状态常量 ====================

    /** 正常状态 */
    public static final String STATUS_NORMAL = "0";

    /** 停用状态 */
    public static final String STATUS_DISABLE = "1";

    /** 通用启停状态校验表达式 */
    public static final String STATUS_REGEX = "0|1";

    // ==================== 删除标志 ====================

    /** 未删除（正常） */
    public static final String DEL_FLAG_NORMAL = "0";

    /** 已删除（逻辑删除） */
    public static final String DEL_FLAG_DELETED = "2";

    /** 逻辑删除标志校验表达式 */
    public static final String DEL_FLAG_REGEX = "0|2";

    /** 用户性别校验表达式 */
    public static final String USER_SEX_REGEX = "0|1|2";

    /** 数据权限范围校验表达式 */
    public static final String DATA_SCOPE_REGEX = "1|2|3|4|5";

    // ==================== 菜单类型 ====================

    /** 目录类型（一级菜单） */
    public static final String MENU_TYPE_DIRECTORY = "M";

    /** 菜单类型（页面路由） */
    public static final String MENU_TYPE_MENU = "C";

    /** 按钮类型（操作权限） */
    public static final String MENU_TYPE_BUTTON = "F";

    // ==================== 系统预设 ID ====================

    /** 默认普通角色 ID（注册用户自动分配） */
    public static final Long DEFAULT_ROLE_ID = 2L;

    /** 超级管理员用户 ID（不可删除/停用） */
    public static final Long SUPER_ADMIN_USER_ID = 1L;

    /** 超级管理员角色 ID（不可删除） */
    public static final Long SUPER_ADMIN_ROLE_ID = 1L;
}
