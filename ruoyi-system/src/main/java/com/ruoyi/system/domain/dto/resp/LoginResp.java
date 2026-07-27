package com.ruoyi.system.domain.dto.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "登录响应")
public class LoginResp {

    @Schema(description = "用户 ID", example = "1")
    private Long userId;

    @Schema(description = "用户名", example = "admin")
    private String userName;

    @Schema(description = "昵称", example = "超级管理员")
    private String nickName;

    @Schema(description = "JWT Token")
    private String token;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "登录时间", example = "2026-06-15 16:30:00")
    private LocalDateTime loginTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "过期时间", example = "2026-06-16 16:30:00")
    private LocalDateTime expireTime;

    @Schema(description = "登录 IP", example = "127.0.0.1")
    private String ip;

    @Schema(description = "登录地址", example = "127.0.0.1")
    private String address;

    @Schema(description = "权限标识列表")
    private List<String> permissions;

    @Schema(description = "角色标识列表")
    private List<String> roles;
}
