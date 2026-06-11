package com.ruoyi.system.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 注册请求体（DTO）
 * <p>
 * 用于 POST /auth/register 接口的请求参数封装。
 * 使用 Jakarta Bean Validation 进行参数校验。
 * </p>
 *
 * @author NingTheshy
 */
@Data
public class RegisterDTO {

    /** 用户名（必填，2-20 个字符） */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 20, message = "用户名长度必须在 2-20 个字符之间")
    private String username;

    /** 密码（必填，6-20 个字符） */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在 6-20 个字符之间")
    private String password;

    /** 昵称（必填，最多 20 个字符） */
    @NotBlank(message = "昵称不能为空")
    @Size(max = 20, message = "昵称长度不能超过 20 个字符")
    private String nickname;

    /** 邮箱（可选，格式校验） */
    @Email(message = "邮箱格式不正确")
    private String email;

    /** 手机号（可选） */
    private String phonenumber;
}
