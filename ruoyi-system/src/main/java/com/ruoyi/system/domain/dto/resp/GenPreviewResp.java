package com.ruoyi.system.domain.dto.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
@Schema(description = "预览代码响应")
public class GenPreviewResp {

    @Schema(description = "文件名称列表，key为文件名，value为代码内容")
    private Map<String, String> files;
}
