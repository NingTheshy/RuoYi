package com.ruoyi.system.domain.dto;

import com.ruoyi.system.domain.entity.SysRole;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * 角色管理 - 请求传输对象
 *
 * <p>用于新增和修改角色接口的请求参数接收，替代原来的 RoleVO（继承 SysRole 的方式）。</p>
 * <p>包含角色基本信息 + 关联的菜单ID数组（menuIds），实现角色-菜单关联的一体化传递。</p>
 *
 * @author ruoyi
 */
@Data
public class SysRoleDTO {

    /** 角色ID（修改时必填，新增时忽略） */
    private Long roleId;

    /** 角色名称 */
    @NotBlank(message = "角色名称不能为空")
    @Size(min = 2, max = 20, message = "角色名称长度必须在 2 到 20 个字符之间")
    private String roleName;

    /** 角色权限标识（如 admin、common） */
    @NotBlank(message = "权限字符不能为空")
    @Size(min = 2, max = 20, message = "权限字符长度必须在 2 到 20 个字符之间")
    private String roleKey;

    /** 显示顺序 */
    @NotNull(message = "显示顺序不能为空")
    private Integer roleSort;

    /** 数据范围（1=全部 2=自定义 3=本部门 4=本部门及以下 5=仅本人） */
    private String dataScope;

    /** 菜单树选择项是否关联显示（0=父子不互相关联 1=父子互相关联） */
    private Integer menuCheckStrictly;

    /** 部门树选择项是否关联显示（0=父子不互相关联 1=父子互相关联） */
    private Integer deptCheckStrictly;

    /** 角色状态（0=正常 1=停用） */
    private String status;

    /** 关联的菜单ID数组（用于角色-菜单权限绑定） */
    private Long[] menuIds;

    /** 备注 */
    private String remark;

    /**
     * 转换为实体对象
     *
     * <p>注意：menuIds 不会设置到实体中，需要在 Controller 中单独处理。</p>
     *
     * @return SysRole 实体对象
     */
    public SysRole toEntity() {
        SysRole role = new SysRole();
        role.setRoleId(this.roleId);
        role.setRoleName(this.roleName);
        role.setRoleKey(this.roleKey);
        role.setRoleSort(this.roleSort);
        role.setDataScope(this.dataScope);
        role.setMenuCheckStrictly(this.menuCheckStrictly);
        role.setDeptCheckStrictly(this.deptCheckStrictly);
        role.setStatus(this.status);
        role.setRemark(this.remark);
        return role;
    }
}
