package com.ruoyi.system.domain.dto;

import com.ruoyi.system.domain.entity.SysDept;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * 部门管理 - 请求传输对象
 *
 * <p>用于新增和修改部门接口的请求参数接收，替代直接使用 SysDept 实体类。</p>
 * <p>注意：ancestors（祖级列表）由服务端自动计算，前端无需传递。</p>
 *
 * @author ruoyi
 */
@Data
public class SysDeptDTO {

    /** 部门ID（修改时必填，新增时忽略） */
    private Long deptId;

    /** 父部门ID（0 表示顶级部门） */
    private Long parentId;

    /** 部门名称 */
    @NotBlank(message = "部门名称不能为空")
    @Size(max = 50, message = "部门名称长度不能超过 50 个字符")
    private String deptName;

    /** 显示顺序 */
    @NotNull(message = "显示顺序不能为空")
    private Integer orderNum;

    /** 负责人 */
    @Size(max = 20, message = "负责人名称长度不能超过 20 个字符")
    private String leader;

    /** 联系电话 */
    @Size(max = 11, message = "联系电话长度不能超过 11 个字符")
    private String phone;

    /** 邮箱 */
    @Email(message = "邮箱格式不正确")
    @Size(max = 50, message = "邮箱长度不能超过 50 个字符")
    private String email;

    /** 部门状态（0=正常 1=停用） */
    private String status;

    /** 备注 */
    private String remark;

    /**
     * 转换为实体对象
     *
     * @return SysDept 实体对象
     */
    public SysDept toEntity() {
        SysDept dept = new SysDept();
        dept.setDeptId(this.deptId);
        dept.setParentId(this.parentId);
        dept.setDeptName(this.deptName);
        dept.setOrderNum(this.orderNum);
        dept.setLeader(this.leader);
        dept.setPhone(this.phone);
        dept.setEmail(this.email);
        dept.setStatus(this.status);
        dept.setRemark(this.remark);
        return dept;
    }
}
