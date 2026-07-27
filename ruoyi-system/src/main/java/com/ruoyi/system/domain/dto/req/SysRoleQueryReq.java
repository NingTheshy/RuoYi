package com.ruoyi.system.domain.dto.req;

import com.ruoyi.common.core.constant.Constants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "角色查询请求")
public class SysRoleQueryReq {
    @Schema(description = "角色名称", example = "管理员")
    @Size(max = 30, message = "角色名称长度不能超过 30 个字符")
    private String roleName;

    @Schema(description = "权限字符", example = "admin")
    @Size(max = 100, message = "权限字符长度不能超过 100 个字符")
    private String roleKey;

    @Schema(description = "状态，0正常 1停用", example = "0")
    @Pattern(regexp = Constants.STATUS_REGEX, message = "状态只能是0或1")
    private String status;
}
