package com.ruoyi.system.domain.dto.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "通知公告响应")
public class SysNoticeResp {

    @Schema(description = "公告ID", example = "1")
    private Long noticeId;

    @Schema(description = "公告标题", example = "系统升级通知")
    private String noticeTitle;

    @Schema(description = "公告类型（1通知 2公告）", example = "1")
    private String noticeType;

    @Schema(description = "公告内容")
    private String noticeContent;

    @Schema(description = "状态", example = "0")
    private String status;

    @Schema(description = "创建者", example = "admin")
    private String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
