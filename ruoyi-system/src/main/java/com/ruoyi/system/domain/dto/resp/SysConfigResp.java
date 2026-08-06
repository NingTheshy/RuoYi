package com.ruoyi.system.domain.dto.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "参数配置响应")
public class SysConfigResp {

    @Schema(description = "参数配置ID", example = "1")
    private Long configId;

    @Schema(description = "参数名称", example = "系统名称")
    private String configName;

    @Schema(description = "参数键名", example = "sysName")
    private String configKey;

    @Schema(description = "参数键值", example = "RuoYi")
    private String configValue;

    @Schema(description = "系统内置（Y是 N否）", example = "Y")
    private String configType;

    @Schema(description = "状态", example = "0")
    private String status;

    @Schema(description = "备注")
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
