package com.ruoyi.system.domain.dto.req;

import com.ruoyi.common.core.constant.Constants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "新增部门请求")
public class SysDeptCreateReq {
    @Schema(description = "父部门 ID", example = "0")
    private Long parentId;

    @Schema(description = "部门名称", example = "研发部")
    @NotBlank(message = "部门名称不能为空")
    @Size(max = 30, message = "部门名称长度不能超过 30 个字符")
    private String deptName;

    @Schema(description = "显示顺序", example = "1")
    @NotNull(message = "显示顺序不能为空")
    private Integer orderNum;

    @Schema(description = "负责人", example = "张三")
    @Size(max = 20, message = "负责人名称长度不能超过 20 个字符")
    private String leader;

    @Schema(description = "联系电话", example = "13800138000")
    @Size(max = 11, message = "联系电话长度不能超过 11 个字符")
    private String phone;

    @Schema(description = "邮箱", example = "dept@example.com")
    @Email(message = "邮箱格式不正确")
    @Size(max = 50, message = "邮箱长度不能超过 50 个字符")
    private String email;

    @Schema(description = "状态，0正常 1停用", example = "0")
    @Pattern(regexp = Constants.STATUS_REGEX, message = "状态只能是0或1")
    private String status;

    @Schema(description = "备注")
    @Size(max = 500, message = "备注长度不能超过 500 个字符")
    private String remark;
}
