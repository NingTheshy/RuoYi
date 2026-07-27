package com.ruoyi.system.domain.dto.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "菜单树节点响应")
public class MenuTreeResp {
    @Schema(description = "节点 ID", example = "1")
    private Long id;

    @Schema(description = "节点名称", example = "系统管理")
    private String label;

    @Schema(description = "子节点列表")
    private List<MenuTreeResp> children;
}
