package com.ruoyi.system.domain.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "操作日志查询请求")
public class SysOperLogQueryReq {

    @Schema(description = "操作模块", example = "用户管理")
    private String title;

    @Schema(description = "操作人员", example = "admin")
    private String operName;

    @Schema(description = "业务类型（0其他 1新增 2修改 3删除）", example = "1")
    private String businessType;

    @Schema(description = "操作状态（0正常 1异常）", example = "0")
    private String status;

    @Schema(description = "开始时间（yyyy-MM-dd）", example = "2026-01-01")
    private String beginTime;

    @Schema(description = "结束时间（yyyy-MM-dd）", example = "2026-12-31")
    private String endTime;
}
