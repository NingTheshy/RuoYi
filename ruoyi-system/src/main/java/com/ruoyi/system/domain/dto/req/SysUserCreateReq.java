package com.ruoyi.system.domain.dto.req;

import com.ruoyi.common.core.constant.Constants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "新增用户请求")
public class SysUserCreateReq {
    @Schema(description = "所属部门 ID", example = "100")
    private Long deptId;

    @Schema(description = "用户账号", example = "zhangsan")
    @NotBlank(message = "用户账号不能为空")
    @Size(min = 2, max = 30, message = "用户账号长度必须在 2 到 30 个字符之间")
    private String userName;

    @Schema(description = "用户昵称", example = "张三")
    @NotBlank(message = "用户昵称不能为空")
    @Size(max = 30, message = "用户昵称长度不能超过 30 个字符")
    private String nickName;

    @Schema(description = "邮箱", example = "zhangsan@example.com")
    @Email(message = "邮箱格式不正确")
    @Size(max = 50, message = "邮箱长度不能超过 50 个字符")
    private String email;

    @Schema(description = "手机号", example = "13800138000")
    @Size(max = 11, message = "手机号码长度不能超过 11 个字符")
    private String phonenumber;

    @Schema(description = "性别，0男 1女 2未知", example = "0")
    @Pattern(regexp = Constants.USER_SEX_REGEX, message = "性别只能是0、1或2")
    private String sex;

    @Schema(description = "头像地址")
    @Size(max = 100, message = "头像地址长度不能超过 100 个字符")
    private String avatar;

    @Schema(description = "登录密码", example = "123456")
    @NotBlank(message = "用户密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在 6 到 20 个字符之间")
    private String password;

    @Schema(description = "状态，0正常 1停用", example = "0")
    @Pattern(regexp = Constants.STATUS_REGEX, message = "状态只能是0或1")
    private String status;

    @Schema(description = "备注")
    @Size(max = 500, message = "备注长度不能超过 500 个字符")
    private String remark;
}
