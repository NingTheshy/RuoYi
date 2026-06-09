package com.ruoyi.system.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class SysDept extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long deptId;
    private Long parentId;
    private String ancestors;
    private String deptName;
    private Integer orderNum;
    private String leader;
    private String phone;
    private String email;
    private String status;
    private String delFlag;

    private List<SysDept> children = new ArrayList<>();
}
