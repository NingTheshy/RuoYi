package com.ruoyi.system.domain.entity;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class SysMenu extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long menuId;
    private String menuName;
    private Long parentId;
    private Integer orderNum;
    private String path;
    private String component;
    private String query;
    private Integer isFrame;
    private Integer isCache;
    private String menuType;
    private String visible;
    private String status;
    private String perms;
    private String icon;

    private List<SysMenu> children = new ArrayList<>();
}
