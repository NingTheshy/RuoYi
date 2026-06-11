package com.ruoyi.system.domain.dto;

import com.ruoyi.system.domain.entity.SysUser;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * 用户管理 - 请求传输对象
 *
 * <p>用于新增和修改用户接口的请求参数接收，替代直接使用 SysUser 实体类。</p>
 * <p>通过 {@link #toEntity()} 方法转换为实体对象传递给 Service 层。</p>
 *
 * @author ruoyi
 */
@Data
public class SysUserDTO {

    /** 用户ID（修改时必填，新增时忽略） */
    @NotNull(message = "用户ID不能为空", groups = UpdateGroup.class)
    private Long userId;

    /** 部门ID */
    private Long deptId;

    /** 用户账号 */
    @NotBlank(message = "用户账号不能为空", groups = CreateGroup.class)
    @Size(min = 2, max = 20, message = "用户账号长度必须在 2 到 20 个字符之间", groups = {CreateGroup.class, UpdateGroup.class})
    private String userName;

    /** 用户昵称 */
    @NotBlank(message = "用户昵称不能为空")
    @Size(max = 30, message = "用户昵称长度不能超过 30 个字符")
    private String nickName;

    /** 用户邮箱 */
    @Email(message = "邮箱格式不正确")
    @Size(max = 50, message = "邮箱长度不能超过 50 个字符")
    private String email;

    /** 手机号码 */
    @Size(max = 11, message = "手机号码长度不能超过 11 个字符")
    private String phonenumber;

    /** 用户性别（0=男 1=女 2=未知） */
    private String sex;

    /** 头像地址 */
    private String avatar;

    /** 用户密码（新增时必填，修改时不传则不更新） */
    @NotBlank(message = "用户密码不能为空", groups = CreateGroup.class)
    @Size(min = 6, max = 20, message = "密码长度必须在 6 到 20 个字符之间", groups = CreateGroup.class)
    private String password;

    /** 帐号状态（0=正常 1=停用） */
    private String status;

    /** 备注 */
    private String remark;

    /**
     * 转换为实体对象
     *
     * <p>将 DTO 中的业务字段复制到 SysUser 实体中，
     * 不会复制 delFlag、loginIp、loginDate、createBy 等内部字段。</p>
     *
     * @return SysUser 实体对象
     */
    public SysUser toEntity() {
        SysUser user = new SysUser();
        user.setUserId(this.userId);
        user.setDeptId(this.deptId);
        user.setUserName(this.userName);
        user.setNickName(this.nickName);
        user.setEmail(this.email);
        user.setPhonenumber(this.phonenumber);
        user.setSex(this.sex);
        user.setAvatar(this.avatar);
        user.setPassword(this.password);
        user.setStatus(this.status);
        user.setRemark(this.remark);
        return user;
    }

    /** 新增校验分组 */
    public interface CreateGroup {}

    /** 修改校验分组 */
    public interface UpdateGroup {}
}
