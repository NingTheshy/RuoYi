package com.ruoyi.system.domain.vo;

import com.ruoyi.system.domain.entity.SysDept;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 部门管理 - 响应视图对象
 *
 * <p>用于部门相关接口的响应数据，过滤掉逻辑删除标识等内部字段。</p>
 * <p>支持树形结构，通过 {@code children} 字段递归嵌套子部门。</p>
 *
 * @author ruoyi
 */
@Data
public class SysDeptVO {

    /** 部门ID */
    private Long deptId;

    /** 父部门ID */
    private Long parentId;

    /** 祖级列表（用于前端树形组件定位） */
    private String ancestors;

    /** 部门名称 */
    private String deptName;

    /** 显示顺序 */
    private Integer orderNum;

    /** 负责人 */
    private String leader;

    /** 联系电话 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 部门状态（0=正常 1=停用） */
    private String status;

    /** 备注 */
    private String remark;

    /** 子部门列表（递归结构） */
    private List<SysDeptVO> children = new ArrayList<>();

    /**
     * 从实体对象转换为 VO
     *
     * @param dept 部门实体
     * @return 部门VO，实体为 null 时返回 null
     */
    public static SysDeptVO fromEntity(SysDept dept) {
        if (dept == null) {
            return null;
        }
        SysDeptVO vo = new SysDeptVO();
        vo.setDeptId(dept.getDeptId());
        vo.setParentId(dept.getParentId());
        vo.setAncestors(dept.getAncestors());
        vo.setDeptName(dept.getDeptName());
        vo.setOrderNum(dept.getOrderNum());
        vo.setLeader(dept.getLeader());
        vo.setPhone(dept.getPhone());
        vo.setEmail(dept.getEmail());
        vo.setStatus(dept.getStatus());
        vo.setRemark(dept.getRemark());
        // 递归转换子部门
        if (dept.getChildren() != null && !dept.getChildren().isEmpty()) {
            vo.setChildren(dept.getChildren().stream()
                    .map(SysDeptVO::fromEntity)
                    .collect(Collectors.toList()));
        }
        return vo;
    }

    /**
     * 批量转换
     *
     * @param depts 部门实体列表
     * @return 部门VO列表
     */
    public static List<SysDeptVO> fromEntityList(List<SysDept> depts) {
        if (depts == null) {
            return List.of();
        }
        return depts.stream().map(SysDeptVO::fromEntity).collect(Collectors.toList());
    }
}
