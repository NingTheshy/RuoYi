package com.ruoyi.system.domain.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "分配用户角色请求")
public class SysUserAssignRolesReq {
    @Schema(description = "用户 ID", example = "1")
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 角色ID数组；允许传空数组以清空当前用户的所有角色。
     */
    @Schema(description = "角色 ID 数组，允许传空数组以清空当前用户的所有角色")
    private Long[] roleIds;
}
