package com.ruoyi.system.domain.dto.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "当前登录用户信息响应")
public class AuthInfoResp {

    @Schema(description = "当前登录用户")
    private SysUserResp user;

    @Schema(description = "角色标识列表")
    private List<String> roles;

    @Schema(description = "权限标识列表")
    private List<String> permissions;
}
