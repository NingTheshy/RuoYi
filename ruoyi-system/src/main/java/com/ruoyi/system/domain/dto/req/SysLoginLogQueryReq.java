package com.ruoyi.system.domain.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "登录日志查询请求")
public class SysLoginLogQueryReq {

    @Schema(description = "用户名称", example = "admin")
    private String userName;

    @Schema(description = "IP地址", example = "127.0.0.1")
    private String ipAddr;

    @Schema(description = "登录状态（0成功 1失败）", example = "0")
    private String status;

    @Schema(description = "开始时间（yyyy-MM-dd）", example = "2026-01-01")
    private String beginTime;

    @Schema(description = "结束时间（yyyy-MM-dd）", example = "2026-12-31")
    private String endTime;
}
