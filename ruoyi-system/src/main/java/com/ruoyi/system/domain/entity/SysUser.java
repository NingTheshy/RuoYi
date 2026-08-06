package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ruoyi.common.core.constant.Constants;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户实体类
 * <p>
 * 对应数据库表 sys_user，RBAC 模型中的用户主体。
 * </p>
 *
 * <p>关联关系：</p>
 * <ul>
 *   <li>用户 ↔ 角色：多对多（通过 sys_user_role 中间表）</li>
 *   <li>用户 → 部门：多对一（dept_id 外键）</li>
 * </ul>
 *
 * <p>安全说明：password 字段使用 @JsonIgnore 注解，确保序列化为 JSON 时不会泄露密码哈希。</p>
 *
 * @author NingTheshy
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 用户 ID（自增主键） */
    @TableId(type = IdType.AUTO)
    private Long userId;

    /** 部门 ID（关联 sys_dept.dept_id） */
    private Long deptId;

    /** 用户名（登录账号，唯一） */
    private String userName;

    /** 昵称（显示名称） */
    private String nickName;

    /** 邮箱 */
    private String email;

    /** 手机号 */
    private String phonenumber;

    /** 性别（"0"=男, "1"=女, "2"=未知） */
    private String sex;

    /** 头像地址 */
    private String avatar;

    /** 密码（BCrypt 哈希值，JSON 序列化时忽略） */
    @JsonIgnore
    private String password;

    /** 状态（"0"=正常, "1"=停用） */
    private String status;

    /** 删除标志（"0"=正常, "2"=已删除，MyBatis-Plus 逻辑删除） */
    @TableLogic(value = Constants.DEL_FLAG_NORMAL, delval = Constants.DEL_FLAG_DELETED)
    private String delFlag;

    /** 最后登录 IP */
    private String loginIp;

    /** 最后登录时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime loginDate;

    /** 用户角色列表（非数据库字段，用于关联查询） */
    @TableField(exist = false)
    private List<SysRole> roles;

    /** 部门名称（非数据库字段，用于关联查询） */
    @TableField(exist = false)
    private String deptName;

    /** 查询条件：创建时间-开始（非数据库字段，仅用于查询） */
    @TableField(exist = false)
    private LocalDateTime beginTime;

    /** 查询条件：创建时间-结束（非数据库字段，仅用于查询） */
    @TableField(exist = false)
    private LocalDateTime endTime;
}
