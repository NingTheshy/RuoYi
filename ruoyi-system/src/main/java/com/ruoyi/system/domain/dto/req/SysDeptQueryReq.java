package com.ruoyi.system.domain.dto.req;

import com.ruoyi.common.core.constant.Constants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "部门查询请求")
public class SysDeptQueryReq {
    @Schema(description = "部门名称", example = "研发部")
    @Size(max = 30, message = "部门名称长度不能超过 30 个字符")
    private String deptName;

    @Schema(description = "状态，0正常 1停用", example = "0")
    @Pattern(regexp = Constants.STATUS_REGEX, message = "状态只能是0或1")
    private String status;
}
