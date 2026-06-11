package com.ruoyi.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.system.domain.entity.SysRole;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色管理 - 响应视图对象
 *
 * <p>用于角色相关接口的响应数据，过滤掉逻辑删除标识等内部字段。</p>
 *
 * @author ruoyi
 */
@Data
public class SysRoleVO {

    /** 角色ID */
    private Long roleId;

    /** 角色名称 */
    private String roleName;

    /** 角色权限标识 */
    private String roleKey;

    /** 显示顺序 */
    private Integer roleSort;

    /** 数据范围（1=全部 2=自定义 3=本部门 4=本部门及以下 5=仅本人） */
    private String dataScope;

    /** 菜单树选择项是否关联显示（0=父子不互相关联 1=父子互相关联） */
    private Integer menuCheckStrictly;

    /** 部门树选择项是否关联显示（0=父子不互相关联 1=父子互相关联） */
    private Integer deptCheckStrictly;

    /** 角色状态（0=正常 1=停用） */
    private String status;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 从实体对象转换为 VO
     *
     * @param role 角色实体
     * @return 角色VO，实体为 null 时返回 null
     */
    public static SysRoleVO fromEntity(SysRole role) {
        if (role == null) {
            return null;
        }
        SysRoleVO vo = new SysRoleVO();
        vo.setRoleId(role.getRoleId());
        vo.setRoleName(role.getRoleName());
        vo.setRoleKey(role.getRoleKey());
        vo.setRoleSort(role.getRoleSort());
        vo.setDataScope(role.getDataScope());
        vo.setMenuCheckStrictly(role.getMenuCheckStrictly());
        vo.setDeptCheckStrictly(role.getDeptCheckStrictly());
        vo.setStatus(role.getStatus());
        vo.setRemark(role.getRemark());
        vo.setCreateTime(role.getCreateTime());
        return vo;
    }

    /**
     * 批量转换
     *
     * @param roles 角色实体列表
     * @return 角色VO列表
     */
    public static List<SysRoleVO> fromEntityList(List<SysRole> roles) {
        if (roles == null) {
            return List.of();
        }
        return roles.stream().map(SysRoleVO::fromEntity).collect(Collectors.toList());
    }
}
