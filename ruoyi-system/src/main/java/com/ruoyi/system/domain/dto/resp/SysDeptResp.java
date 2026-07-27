package com.ruoyi.system.domain.dto.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "部门响应")
public class SysDeptResp {
    @Schema(description = "部门 ID", example = "100")
    private Long deptId;
    @Schema(description = "父部门 ID", example = "0")
    private Long parentId;
    @Schema(description = "祖级列表")
    private String ancestors;
    @Schema(description = "部门名称", example = "研发部")
    private String deptName;
    @Schema(description = "显示顺序", example = "1")
    private Integer orderNum;
    @Schema(description = "负责人")
    private String leader;
    @Schema(description = "联系电话")
    private String phone;
    @Schema(description = "邮箱")
    private String email;
    @Schema(description = "状态", example = "0")
    private String status;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "子部门列表")
    private List<SysDeptResp> children;
}
