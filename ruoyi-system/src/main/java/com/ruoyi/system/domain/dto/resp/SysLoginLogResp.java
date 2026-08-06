package com.ruoyi.system.domain.dto.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "登录日志响应")
public class SysLoginLogResp {

    @Schema(description = "日志ID", example = "1")
    private Long infoId;

    @Schema(description = "用户名称", example = "admin")
    private String userName;

    @Schema(description = "IP地址", example = "127.0.0.1")
    private String ipAddr;

    @Schema(description = "登录地点", example = "内网IP")
    private String loginLocation;

    @Schema(description = "浏览器类型", example = "Chrome")
    private String browser;

    @Schema(description = "操作系统", example = "Windows")
    private String os;

    @Schema(description = "登录状态（0成功 1失败）", example = "0")
    private String status;

    @Schema(description = "提示信息", example = "登录成功")
    private String msg;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "登录时间")
    private LocalDateTime loginTime;
}
