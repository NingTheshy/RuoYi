package com.ruoyi.system.service;

import com.ruoyi.system.domain.dto.resp.SysOnlineResp;

import java.time.LocalDateTime;
import java.util.List;

public interface SysOnlineService {
    List<SysOnlineResp> getOnlineList();
    int forceLogout(String tokenId);
    void storeOnlineUser(Long userId, String userName, String nickName, String deptName,
                         String loginIp, LocalDateTime loginTime);
    void removeOnlineUser(Long userId);
}
