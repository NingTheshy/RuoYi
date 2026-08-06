package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.PageResult;
import com.ruoyi.system.convert.SysLoginLogConvert;
import com.ruoyi.system.domain.dto.req.SysLoginLogQueryReq;
import com.ruoyi.system.domain.dto.resp.SysLoginLogResp;
import com.ruoyi.system.domain.entity.SysLoginLog;
import com.ruoyi.system.mapper.SysLoginLogMapper;
import com.ruoyi.system.service.SysLoginLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Service
public class SysLoginLogServiceImpl extends ServiceImpl<SysLoginLogMapper, SysLoginLog> implements SysLoginLogService {

    private final SysLoginLogConvert loginLogConvert;

    public SysLoginLogServiceImpl(SysLoginLogConvert loginLogConvert) {
        this.loginLogConvert = loginLogConvert;
    }

    @Override
    public PageResult<SysLoginLogResp> getLoginLogPage(SysLoginLogQueryReq queryReq, Integer pageNum, Integer pageSize) {
        Page<SysLoginLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysLoginLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(queryReq.getUserName()), SysLoginLog::getUserName, queryReq.getUserName())
                .like(StringUtils.hasText(queryReq.getIpAddr()), SysLoginLog::getIpAddr, queryReq.getIpAddr())
                .eq(StringUtils.hasText(queryReq.getStatus()), SysLoginLog::getStatus, queryReq.getStatus())
                .ge(StringUtils.hasText(queryReq.getBeginTime()), SysLoginLog::getLoginTime, parseDate(queryReq.getBeginTime()))
                .le(StringUtils.hasText(queryReq.getEndTime()), SysLoginLog::getLoginTime, parseDate(queryReq.getEndTime()))
                .orderByDesc(SysLoginLog::getInfoId);
        Page<SysLoginLog> result = baseMapper.selectPage(page, wrapper);
        return new PageResult<>(loginLogConvert.toRespList(result.getRecords()), result.getTotal());
    }

    @Override
    @Transactional
    public int deleteLoginLogByIds(Long[] infoIds) {
        return removeByIds(Arrays.asList(infoIds)) ? infoIds.length : 0;
    }

    @Override
    @Transactional
    public int cleanLoginLog() {
        long count = count();
        if (count == 0) {
            return 0;
        }
        return baseMapper.delete(null);
    }

    @Override
    @Transactional
    public void saveLoginLog(SysLoginLog loginLog) {
        save(loginLog);
    }

    @Override
    public void recordLoginLog(String userName, String ipAddr, String status, String msg) {
        SysLoginLog loginLog = new SysLoginLog();
        loginLog.setUserName(userName);
        loginLog.setIpAddr(ipAddr);
        loginLog.setStatus(status);
        loginLog.setMsg(msg);
        loginLog.setLoginTime(LocalDateTime.now());
        loginLog.setLoginLocation(isInternalIp(ipAddr) ? "内网IP" : "外网IP");
        loginLog.setBrowser("Unknown");
        loginLog.setOs("Unknown");
        save(loginLog);
    }

    private boolean isInternalIp(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }
        return ip.startsWith("127.") || ip.startsWith("10.") || ip.startsWith("192.168.") || ip.startsWith("172.");
    }

    private LocalDateTime parseDate(String dateStr) {
        try {
            return LocalDateTime.parse(dateStr + " 00:00:00",
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            return null;
        }
    }
}
