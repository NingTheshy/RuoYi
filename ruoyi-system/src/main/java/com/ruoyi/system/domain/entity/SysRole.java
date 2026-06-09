package com.ruoyi.system.domain.entity;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SysRole extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long roleId;
    private String roleName;
    private String roleKey;
    private Integer roleSort;
    private String dataScope;
    private Integer menuCheckStrictly;
    private Integer deptCheckStrictly;
    private String status;
    private String delFlag;
}
