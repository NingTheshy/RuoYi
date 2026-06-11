package com.ruoyi.system.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录请求体（DTO）
 * <p>
 * 用于 POST /auth/login 接口的请求参数封装。
 * 使用 Jakarta Bean Validation 进行参数校验。
 * </p>
 *
 * @author NingTheshy
 */
@Data
public class LoginDTO {

    /** 用户名（必填） */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /** 密码（必填） */
    @NotBlank(message = "密码不能为空")
    private String password;
}
