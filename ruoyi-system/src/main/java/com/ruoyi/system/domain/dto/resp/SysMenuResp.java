package com.ruoyi.system.domain.dto.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "菜单响应")
public class SysMenuResp {
    @Schema(description = "菜单 ID", example = "1")
    private Long menuId;
    @Schema(description = "菜单名称", example = "系统管理")
    private String menuName;
    @Schema(description = "父菜单 ID", example = "0")
    private Long parentId;
    @Schema(description = "显示顺序", example = "1")
    private Integer orderNum;
    @Schema(description = "路由地址")
    private String path;
    @Schema(description = "组件路径")
    private String component;
    @Schema(description = "路由参数")
    private String query;
    @Schema(description = "是否外链", example = "1")
    private Integer isFrame;
    @Schema(description = "是否缓存", example = "0")
    private Integer isCache;
    @Schema(description = "菜单类型", example = "C")
    private String menuType;
    @Schema(description = "显示状态", example = "0")
    private String visible;
    @Schema(description = "菜单状态", example = "0")
    private String status;
    @Schema(description = "权限标识")
    private String perms;
    @Schema(description = "菜单图标")
    private String icon;
    @Schema(description = "备注")
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "子菜单列表")
    private List<SysMenuResp> children;
}
