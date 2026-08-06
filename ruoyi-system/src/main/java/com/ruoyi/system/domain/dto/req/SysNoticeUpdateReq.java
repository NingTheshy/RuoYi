package com.ruoyi.system.domain.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "修改通知公告请求")
public class SysNoticeUpdateReq {

    @Schema(description = "公告ID", example = "1")
    @NotNull(message = "公告ID不能为空")
    private Long noticeId;

    @Schema(description = "公告标题", example = "系统升级通知")
    @NotBlank(message = "公告标题不能为空")
    @Size(min = 2, max = 50, message = "公告标题长度必须在 2 到 50 个字符之间")
    private String noticeTitle;

    @Schema(description = "公告类型（1通知 2公告）", example = "1")
    @Pattern(regexp = "^[12]$", message = "公告类型只能是1或2")
    private String noticeType;

    @Schema(description = "公告内容", example = "<p>系统升级内容...</p>")
    @NotBlank(message = "公告内容不能为空")
    private String noticeContent;

    @Schema(description = "状态，0正常 1停用", example = "0")
    @Pattern(regexp = "^[01]$", message = "状态只能是0或1")
    private String status;

    @Schema(description = "备注")
    @Size(max = 500, message = "备注长度不能超过 500 个字符")
    private String remark;
}
