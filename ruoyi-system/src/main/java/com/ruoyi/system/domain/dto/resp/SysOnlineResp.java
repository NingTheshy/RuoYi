package com.ruoyi.system.domain.dto.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "在线用户响应")
public class SysOnlineResp {

    @Schema(description = "Token ID (Redis Key)", example = "login_user:1")
    private String tokenId;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "用户名", example = "admin")
    private String userName;

    @Schema(description = "昵称", example = "若依管理员")
    private String nickName;

    @Schema(description = "部门名称", example = "研发部门")
    private String deptName;

    @Schema(description = "登录IP", example = "127.0.0.1")
    private String loginIp;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "登录时间")
    private LocalDateTime loginTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "过期时间")
    private LocalDateTime expireTime;
}
