package com.ruoyi.system.domain.dto.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "字典数据选项响应")
public class DictDataOptionResp {

    @Schema(description = "字典标签", example = "男")
    private String dictLabel;

    @Schema(description = "字典值", example = "0")
    private String dictValue;
}