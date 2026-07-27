package com.ruoyi.system.domain.dto.req;

import com.ruoyi.common.core.constant.Constants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "修改角色请求")
public class SysRoleUpdateReq {
    @Schema(description = "角色 ID", example = "1")
    @NotNull(message = "角色ID不能为空")
    private Long roleId;

    @Schema(description = "角色名称", example = "测试角色")
    @NotBlank(message = "角色名称不能为空")
    @Size(min = 2, max = 30, message = "角色名称长度必须在 2 到 30 个字符之间")
    private String roleName;

    @Schema(description = "权限字符", example = "system:test:list")
    @NotBlank(message = "权限字符不能为空")
    @Size(min = 2, max = 100, message = "权限字符长度必须在 2 到 100 个字符之间")
    private String roleKey;

    @Schema(description = "显示顺序", example = "1")
    @NotNull(message = "显示顺序不能为空")
    private Integer roleSort;

    @Schema(description = "数据范围，1全部 2自定义 3本部门 4本部门及以下 5仅本人", example = "1")
    @Pattern(regexp = Constants.DATA_SCOPE_REGEX, message = "数据范围只能是1到5")
    private String dataScope;

    @Schema(description = "菜单树选择项是否关联，0否 1是", example = "1")
    @Min(value = 0, message = "菜单树选择项是否关联只能是0或1")
    @Max(value = 1, message = "菜单树选择项是否关联只能是0或1")
    private Integer menuCheckStrictly;

    @Schema(description = "部门树选择项是否关联，0否 1是", example = "1")
    @Min(value = 0, message = "部门树选择项是否关联只能是0或1")
    @Max(value = 1, message = "部门树选择项是否关联只能是0或1")
    private Integer deptCheckStrictly;

    @Schema(description = "状态，0正常 1停用", example = "0")
    @Pattern(regexp = Constants.STATUS_REGEX, message = "状态只能是0或1")
    private String status;

    @Schema(description = "已分配菜单 ID 列表")
    private Long[] menuIds;

    @Schema(description = "备注")
    @Size(max = 500, message = "备注长度不能超过 500 个字符")
    private String remark;
}
