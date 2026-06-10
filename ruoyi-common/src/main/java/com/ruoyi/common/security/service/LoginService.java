package com.ruoyi.common.security.service;

import com.ruoyi.common.core.constant.Constants;
import com.ruoyi.common.core.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    private static final Logger log = LoggerFactory.getLogger(LoginService.class);

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String login(String username, String password, String userId,
                        String realPassword, String status) {
        if (Constants.STATUS_DISABLE.equals(status)) {
            throw new ServiceException("用户已被停用");
        }

        if (!passwordEncoder.matches(password, realPassword)) {
            throw new ServiceException("密码错误");
        }

        return tokenService.createToken(userId);
    }

    public void logout(String userId) {
        tokenService.removeToken(userId);
    }
}
