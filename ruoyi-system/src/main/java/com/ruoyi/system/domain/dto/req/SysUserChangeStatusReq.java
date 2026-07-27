package com.ruoyi.system.domain.dto.req;

import com.ruoyi.common.core.constant.Constants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "切换用户状态请求")
public class SysUserChangeStatusReq {
    @Schema(description = "用户 ID", example = "1")
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @Schema(description = "状态，0正常 1停用", example = "0")
    @NotBlank(message = "状态不能为空")
    @Pattern(regexp = Constants.STATUS_REGEX, message = "状态只能是0或1")
    private String status;
}
