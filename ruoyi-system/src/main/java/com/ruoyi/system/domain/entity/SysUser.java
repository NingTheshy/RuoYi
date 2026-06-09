package com.ruoyi.system.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class SysUser extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long userId;
    private Long deptId;
    private String userName;
    private String nickName;
    private String email;
    private String phonenumber;
    private String sex;
    private String avatar;
    @JsonIgnore
    private String password;
    private String status;
    private String delFlag;
    private String loginIp;
    private Date loginDate;

    private List<SysRole> roles;
}
