package com.ruoyi.system.domain.dto.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "角色响应")
public class SysRoleResp {
    @Schema(description = "角色 ID", example = "1")
    private Long roleId;
    @Schema(description = "角色名称", example = "管理员")
    private String roleName;
    @Schema(description = "权限字符", example = "admin")
    private String roleKey;
    @Schema(description = "显示顺序", example = "1")
    private Integer roleSort;
    @Schema(description = "数据范围", example = "1")
    private String dataScope;
    @Schema(description = "菜单树选择项是否关联", example = "1")
    private Integer menuCheckStrictly;
    @Schema(description = "部门树选择项是否关联", example = "1")
    private Integer deptCheckStrictly;
    @Schema(description = "状态", example = "0")
    private String status;
    @Schema(description = "备注")
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
