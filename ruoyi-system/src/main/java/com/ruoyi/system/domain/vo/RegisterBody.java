package com.ruoyi.system.domain.vo;

import lombok.Data;

@Data
public class RegisterBody {

    private String username;

    private String password;

    private String nickname;

    private String email;

    private String phonenumber;
}
