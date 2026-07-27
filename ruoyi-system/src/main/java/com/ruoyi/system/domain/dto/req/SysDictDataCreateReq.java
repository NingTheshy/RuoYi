package com.ruoyi.system.domain.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "新增字典数据请求")
public class SysDictDataCreateReq {

    @Schema(description = "排序号", example = "1")
    private Integer dictSort;

    @Schema(description = "字典标签", example = "男")
    @NotBlank(message = "字典标签不能为空")
    @Size(min = 1, max = 100, message = "字典标签长度必须在 1 到 100 个字符之间")
    private String dictLabel;

    @Schema(description = "字典值", example = "0")
    @NotBlank(message = "字典值不能为空")
    @Size(min = 1, max = 100, message = "字典值长度必须在 1 到 100 个字符之间")
    private String dictValue;

    @Schema(description = "字典类型", example = "sys_user_sex")
    @NotBlank(message = "字典类型不能为空")
    private String dictType;

    @Schema(description = "CSS样式类")
    @Size(max = 100, message = "CSS样式类长度不能超过 100 个字符")
    private String cssClass;

    @Schema(description = "表格样式类")
    @Size(max = 100, message = "表格样式类长度不能超过 100 个字符")
    private String listClass;

    @Schema(description = "是否默认，Y是 N否", example = "N")
    @Pattern(regexp = "^[YN]$", message = "是否默认只能是Y或N")
    private String isDefault;

    @Schema(description = "状态，0正常 1停用", example = "0")
    @Pattern(regexp = "^[01]$", message = "状态只能是0或1")
    private String status;

    @Schema(description = "备注")
    @Size(max = 500, message = "备注长度不能超过 500 个字符")
    private String remark;
}