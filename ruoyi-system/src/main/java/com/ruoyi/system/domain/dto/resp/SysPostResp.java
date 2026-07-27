package com.ruoyi.system.domain.dto.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "岗位响应")
public class SysPostResp {

    @Schema(description = "岗位ID", example = "1")
    private Long postId;

    @Schema(description = "岗位编码", example = "admin")
    private String postCode;

    @Schema(description = "岗位名称", example = "管理员")
    private String postName;

    @Schema(description = "显示顺序", example = "1")
    private Integer postSort;

    @Schema(description = "状态", example = "0")
    private String status;

    @Schema(description = "备注")
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}