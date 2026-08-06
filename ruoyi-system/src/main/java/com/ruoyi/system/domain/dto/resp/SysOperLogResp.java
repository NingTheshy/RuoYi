package com.ruoyi.system.domain.dto.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "操作日志响应")
public class SysOperLogResp {

    @Schema(description = "日志ID", example = "1")
    private Long operId;

    @Schema(description = "操作模块", example = "用户管理")
    private String title;

    @Schema(description = "业务类型", example = "2")
    private String businessType;

    @Schema(description = "方法名称", example = "com.ruoyi.admin.web.system.SysUserController.update")
    private String method;

    @Schema(description = "请求方式", example = "PUT")
    private String requestMethod;

    @Schema(description = "操作人员", example = "admin")
    private String operName;

    @Schema(description = "部门名称", example = "研发部门")
    private String deptName;

    @Schema(description = "请求URL", example = "/system/user")
    private String operUrl;

    @Schema(description = "主机地址", example = "127.0.0.1")
    private String operIp;

    @Schema(description = "操作地点", example = "内网IP")
    private String operLocation;

    @Schema(description = "请求参数")
    private String operParam;

    @Schema(description = "返回参数")
    private String jsonResult;

    @Schema(description = "操作状态（0正常 1异常）", example = "0")
    private Integer status;

    @Schema(description = "错误信息")
    private String errorMsg;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "操作时间")
    private LocalDateTime operTime;
}
