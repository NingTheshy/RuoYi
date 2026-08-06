package com.ruoyi.system.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.system.domain.dto.resp.SysOnlineResp;
import com.ruoyi.system.service.SysOnlineService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class SysOnlineServiceImpl implements SysOnlineService {

    private static final String ONLINE_USER_KEY_PREFIX = "login_user:";
    private static final long EXPIRE_HOURS = 24;

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SysOnlineServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public List<SysOnlineResp> getOnlineList() {
        Set<String> keys = redisTemplate.keys(ONLINE_USER_KEY_PREFIX + "*");
        if (keys == null || keys.isEmpty()) {
            return new ArrayList<>();
        }
        List<SysOnlineResp> result = new ArrayList<>();
        for (String key : keys) {
            Object value = redisTemplate.opsForValue().get(key);
            if (value != null) {
                try {
                    Map<String, Object> map = objectMapper.convertValue(value, new TypeReference<>() {});
                    SysOnlineResp resp = new SysOnlineResp();
                    resp.setTokenId(key);
                    resp.setUserId(((Number) map.get("userId")).longValue());
                    resp.setUserName((String) map.get("userName"));
                    resp.setNickName((String) map.get("nickName"));
                    resp.setDeptName((String) map.get("deptName"));
                    resp.setLoginIp((String) map.get("loginIp"));

                    Number loginTimeMillis = (Number) map.get("loginTimeMillis");
                    if (loginTimeMillis != null) {
                        resp.setLoginTime(LocalDateTime.ofInstant(
                                java.time.Instant.ofEpochMilli(loginTimeMillis.longValue()),
                                ZoneId.systemDefault()));
                    }

                    Long expireSeconds = redisTemplate.getExpire(key, TimeUnit.SECONDS);
                    if (expireSeconds != null) {
                        resp.setExpireTime(LocalDateTime.now().plusSeconds(expireSeconds));
                    }

                    result.add(resp);
                } catch (Exception e) {
                    // skip invalid entries
                }
            }
        }
        return result;
    }

    @Override
    public int forceLogout(String tokenId) {
        redisTemplate.delete(tokenId);
        return 1;
    }

    @Override
    public void storeOnlineUser(Long userId, String userName, String nickName, String deptName,
                                 String loginIp, LocalDateTime loginTime) {
        String key = ONLINE_USER_KEY_PREFIX + userId;
        Map<String, Object> value = new HashMap<>();
        value.put("userId", userId);
        value.put("userName", userName);
        value.put("nickName", nickName);
        value.put("deptName", deptName);
        value.put("loginIp", loginIp);
        value.put("loginTimeMillis", loginTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        redisTemplate.opsForValue().set(key, value, EXPIRE_HOURS, TimeUnit.HOURS);
    }

    @Override
    public void removeOnlineUser(Long userId) {
        String key = ONLINE_USER_KEY_PREFIX + userId;
        redisTemplate.delete(key);
    }
}
