package com.ruoyi.common.security.service;

import com.ruoyi.common.security.utils.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class TokenService {

    private static final String TOKEN_PREFIX = "login_tokens:";
    private static final long TOKEN_EXPIRATION = 86400000L;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public String createToken(String userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        String token = jwtTokenProvider.createToken(claims);
        redisTemplate.opsForValue().set(TOKEN_PREFIX + userId, token, TOKEN_EXPIRATION, TimeUnit.MILLISECONDS);
        return token;
    }

    public String getUserIdFromToken(String token) {
        return jwtTokenProvider.getUserId(token);
    }

    public boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }

    public void removeToken(String userId) {
        redisTemplate.delete(TOKEN_PREFIX + userId);
    }

    public String getTokenByUserId(String userId) {
        Object token = redisTemplate.opsForValue().get(TOKEN_PREFIX + userId);
        return token != null ? token.toString() : null;
    }
}
