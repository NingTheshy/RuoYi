package com.ruoyi.system.domain.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "通知公告查询请求")
public class SysNoticeQueryReq {

    @Schema(description = "公告标题", example = "系统升级")
    private String noticeTitle;

    @Schema(description = "公告类型（1通知 2公告）", example = "1")
    @Pattern(regexp = "^[12]$", message = "公告类型只能是1或2")
    private String noticeType;

    @Schema(description = "创建者", example = "admin")
    private String createBy;

    @Schema(description = "状态，0正常 1停用", example = "0")
    @Pattern(regexp = "^[01]$", message = "状态只能是0或1")
    private String status;
}
