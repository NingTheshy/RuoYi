package com.ruoyi.system.domain.dto.req;

import com.ruoyi.common.core.constant.Constants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "菜单查询请求")
public class SysMenuQueryReq {
    @Schema(description = "菜单名称", example = "系统管理")
    @Size(max = 50, message = "菜单名称长度不能超过 50 个字符")
    private String menuName;

    @Schema(description = "菜单状态，0正常 1停用", example = "0")
    @Pattern(regexp = Constants.STATUS_REGEX, message = "菜单状态只能是0或1")
    private String status;

    @Schema(description = "显示状态，0显示 1隐藏", example = "0")
    @Pattern(regexp = Constants.STATUS_REGEX, message = "显示状态只能是0或1")
    private String visible;
}
