package com.ruoyi.system.domain.dto.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "字典类型响应")
public class SysDictTypeResp {

    @Schema(description = "字典类型ID", example = "1")
    private Long dictId;

    @Schema(description = "字典名称", example = "用户性别")
    private String dictName;

    @Schema(description = "字典类型", example = "sys_user_sex")
    private String dictType;

    @Schema(description = "状态", example = "0")
    private String status;

    @Schema(description = "备注")
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}