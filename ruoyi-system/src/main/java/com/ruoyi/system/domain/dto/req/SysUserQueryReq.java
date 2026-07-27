package com.ruoyi.system.domain.dto.req;

import com.ruoyi.common.core.constant.Constants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@Schema(description = "用户查询请求")
public class SysUserQueryReq {
    @Schema(description = "用户账号", example = "admin")
    @Size(max = 30, message = "用户账号长度不能超过 30 个字符")
    private String userName;

    @Schema(description = "用户昵称", example = "超级管理员")
    @Size(max = 30, message = "用户昵称长度不能超过 30 个字符")
    private String nickName;

    @Schema(description = "手机号", example = "13800138000")
    @Size(max = 11, message = "手机号码长度不能超过 11 个字符")
    private String phonenumber;

    @Schema(description = "状态，0正常 1停用", example = "0")
    @Pattern(regexp = Constants.STATUS_REGEX, message = "状态只能是0或1")
    private String status;

    @Schema(description = "部门 ID", example = "100")
    private Long deptId;

    @Schema(description = "开始时间，格式 yyyy-MM-dd HH:mm:ss", example = "2024-01-01 00:00:00")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime beginTime;

    @Schema(description = "结束时间，格式 yyyy-MM-dd HH:mm:ss", example = "2024-12-31 23:59:59")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}
