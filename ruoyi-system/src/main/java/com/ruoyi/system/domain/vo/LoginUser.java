package com.ruoyi.system.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class LoginUser {

    private Long userId;
    private String userName;
    private String nickName;
    private String token;
    private Long loginTime;
    private Long expireTime;
    private String ip;
    private String address;
    private List<String> permissions;
    private List<String> roles;
}
