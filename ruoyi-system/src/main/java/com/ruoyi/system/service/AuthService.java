package com.ruoyi.system.service;

import com.ruoyi.system.domain.dto.req.LoginReq;
import com.ruoyi.system.domain.dto.req.RegisterReq;
import com.ruoyi.system.domain.dto.resp.AuthInfoResp;
import com.ruoyi.system.domain.dto.resp.LoginResp;

/**
 * 认证应用服务
 */
public interface AuthService {

    /**
     * 注册用户并分配默认角色。
     *
     * @param req 注册请求体
     * @return 新用户 ID
     */
    Long register(RegisterReq req);

    /**
     * 执行登录并组装登录响应。
     *
     * @param req       登录请求体
     * @param requestIp 客户端 IP
     * @return 登录响应
     */
    LoginResp login(LoginReq req, String requestIp);

    /**
     * 获取当前登录用户的认证信息。
     *
     * @return 当前登录用户的认证信息
     */
    AuthInfoResp getAuthInfo();

    /**
     * 根据认证请求头执行登出。
     *
     * @param authorizationHeader 请求头中的认证信息
     */
    void logout(String authorizationHeader);
}
