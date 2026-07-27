package com.ruoyi.system.domain.dto.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "用户响应")
public class SysUserResp {

    @Schema(description = "用户 ID", example = "1")
    private Long userId;

    @Schema(description = "部门 ID", example = "100")
    private Long deptId;

    @Schema(description = "用户名", example = "admin")
    private String userName;

    @Schema(description = "昵称", example = "超级管理员")
    private String nickName;

    @Schema(description = "邮箱", example = "admin@example.com")
    private String email;

    @Schema(description = "手机号", example = "13800138000")
    private String phonenumber;

    @Schema(description = "性别", example = "0")
    private String sex;

    @Schema(description = "头像地址")
    private String avatar;

    @Schema(description = "状态", example = "0")
    private String status;

    @Schema(description = "备注")
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "角色列表")
    private List<SysRoleResp> roles;
}
