package com.ruoyi.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.system.domain.entity.SysUser;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户管理 - 响应视图对象
 *
 * <p>用于用户相关接口的响应数据，过滤掉密码、逻辑删除标识、登录IP等内部字段。</p>
 *
 * @author ruoyi
 */
@Data
public class SysUserVO {

    /** 用户ID */
    private Long userId;

    /** 部门ID */
    private Long deptId;

    /** 用户账号 */
    private String userName;

    /** 用户昵称 */
    private String nickName;

    /** 用户邮箱 */
    private String email;

    /** 手机号码 */
    private String phonenumber;

    /** 用户性别（0=男 1=女 2=未知） */
    private String sex;

    /** 头像地址 */
    private String avatar;

    /** 帐号状态（0=正常 1=停用） */
    private String status;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 角色列表（简要信息） */
    private List<SysRoleVO> roles;

    /**
     * 从实体对象转换为 VO
     *
     * @param user 用户实体
     * @return 用户VO，实体为 null 时返回 null
     */
    public static SysUserVO fromEntity(SysUser user) {
        if (user == null) {
            return null;
        }
        SysUserVO vo = new SysUserVO();
        vo.setUserId(user.getUserId());
        vo.setDeptId(user.getDeptId());
        vo.setUserName(user.getUserName());
        vo.setNickName(user.getNickName());
        vo.setEmail(user.getEmail());
        vo.setPhonenumber(user.getPhonenumber());
        vo.setSex(user.getSex());
        vo.setAvatar(user.getAvatar());
        vo.setStatus(user.getStatus());
        vo.setRemark(user.getRemark());
        vo.setCreateTime(user.getCreateTime());
        // 角色列表转换（排除内部字段）
        if (user.getRoles() != null) {
            vo.setRoles(user.getRoles().stream()
                    .map(SysRoleVO::fromEntity)
                    .collect(Collectors.toList()));
        }
        return vo;
    }

    /**
     * 批量转换
     *
     * @param users 用户实体列表
     * @return 用户VO列表
     */
    public static List<SysUserVO> fromEntityList(List<SysUser> users) {
        if (users == null) {
            return List.of();
        }
        return users.stream().map(SysUserVO::fromEntity).collect(Collectors.toList());
    }
}
