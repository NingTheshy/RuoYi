package com.ruoyi.system.domain.dto.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "字典数据响应")
public class SysDictDataResp {

    @Schema(description = "字典数据ID", example = "1")
    private Long dictCode;

    @Schema(description = "排序号", example = "1")
    private Integer dictSort;

    @Schema(description = "字典标签", example = "男")
    private String dictLabel;

    @Schema(description = "字典值", example = "0")
    private String dictValue;

    @Schema(description = "字典类型", example = "sys_user_sex")
    private String dictType;

    @Schema(description = "CSS样式类")
    private String cssClass;

    @Schema(description = "表格样式类")
    private String listClass;

    @Schema(description = "是否默认", example = "Y")
    private String isDefault;

    @Schema(description = "状态", example = "0")
    private String status;
}