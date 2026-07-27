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
@Schema(description = "修改菜单请求")
public class SysMenuUpdateReq {
    @Schema(description = "菜单 ID", example = "1")
    @NotNull(message = "菜单ID不能为空")
    private Long menuId;

    @Schema(description = "父菜单 ID", example = "0")
    private Long parentId;

    @Schema(description = "菜单名称", example = "测试菜单")
    @NotBlank(message = "菜单名称不能为空")
    @Size(max = 50, message = "菜单名称长度不能超过 50 个字符")
    private String menuName;

    @Schema(description = "显示顺序", example = "1")
    @NotNull(message = "显示顺序不能为空")
    private Integer orderNum;

    @Schema(description = "路由地址", example = "/system/test")
    @Size(max = 200, message = "路由地址长度不能超过 200 个字符")
    private String path;

    @Schema(description = "组件路径", example = "system/test/index")
    @Size(max = 255, message = "组件路径长度不能超过 255 个字符")
    private String component;

    @Schema(description = "路由参数")
    @Size(max = 255, message = "路由参数长度不能超过 255 个字符")
    private String query;

    @Schema(description = "是否外链，0是 1否", example = "1")
    @Min(value = 0, message = "是否外链只能是0或1")
    @Max(value = 1, message = "是否外链只能是0或1")
    private Integer isFrame;

    @Schema(description = "是否缓存，0缓存 1不缓存", example = "0")
    @Min(value = 0, message = "是否缓存只能是0或1")
    @Max(value = 1, message = "是否缓存只能是0或1")
    private Integer isCache;

    @Schema(description = "菜单类型，M目录 C菜单 F按钮", example = "C")
    @NotBlank(message = "菜单类型不能为空")
    @Pattern(regexp = "M|C|F", message = "菜单类型只能是M、C或F")
    private String menuType;

    @Schema(description = "显示状态，0显示 1隐藏", example = "0")
    @Pattern(regexp = Constants.STATUS_REGEX, message = "显示状态只能是0或1")
    private String visible;

    @Schema(description = "菜单状态，0正常 1停用", example = "0")
    @Pattern(regexp = Constants.STATUS_REGEX, message = "菜单状态只能是0或1")
    private String status;

    @Schema(description = "权限标识", example = "system:test:list")
    @Size(max = 100, message = "权限标识长度不能超过 100 个字符")
    private String perms;

    @Schema(description = "菜单图标", example = "user")
    @Size(max = 100, message = "菜单图标长度不能超过 100 个字符")
    private String icon;

    @Schema(description = "备注")
    @Size(max = 500, message = "备注长度不能超过 500 个字符")
    private String remark;
}
