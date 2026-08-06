package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.PageResult;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.system.convert.SysOperLogConvert;
import com.ruoyi.system.domain.dto.req.SysOperLogQueryReq;
import com.ruoyi.system.domain.dto.resp.SysOperLogResp;
import com.ruoyi.system.domain.entity.SysOperLog;
import com.ruoyi.system.mapper.SysOperLogMapper;
import com.ruoyi.system.service.SysOperLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Service
public class SysOperLogServiceImpl extends ServiceImpl<SysOperLogMapper, SysOperLog> implements SysOperLogService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final SysOperLogConvert operLogConvert;

    public SysOperLogServiceImpl(SysOperLogConvert operLogConvert) {
        this.operLogConvert = operLogConvert;
    }

    @Override
    public PageResult<SysOperLogResp> getOperLogPage(SysOperLogQueryReq queryReq, Integer pageNum, Integer pageSize) {
        Page<SysOperLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysOperLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(queryReq.getTitle()), SysOperLog::getTitle, queryReq.getTitle())
                .like(StringUtils.hasText(queryReq.getOperName()), SysOperLog::getOperName, queryReq.getOperName())
                .eq(StringUtils.hasText(queryReq.getBusinessType()), SysOperLog::getBusinessType, queryReq.getBusinessType())
                .eq(StringUtils.hasText(queryReq.getStatus()), SysOperLog::getStatus, parseStatus(queryReq.getStatus()))
                .ge(StringUtils.hasText(queryReq.getBeginTime()), SysOperLog::getOperTime, parseDate(queryReq.getBeginTime()))
                .le(StringUtils.hasText(queryReq.getEndTime()), SysOperLog::getOperTime, parseDate(queryReq.getEndTime()))
                .orderByDesc(SysOperLog::getOperId);
        Page<SysOperLog> result = baseMapper.selectPage(page, wrapper);
        return new PageResult<>(operLogConvert.toRespList(result.getRecords()), result.getTotal());
    }

    @Override
    public SysOperLogResp getOperLogById(Long operId) {
        SysOperLog operLog = getById(operId);
        if (operLog == null) {
            throw new ServiceException(404, "操作日志不存在");
        }
        return operLogConvert.toResp(operLog);
    }

    @Override
    @Transactional
    public int deleteOperLogByIds(Long[] operIds) {
        return removeByIds(Arrays.asList(operIds)) ? operIds.length : 0;
    }

    @Override
    @Transactional
    public int cleanOperLog() {
        long count = count();
        if (count == 0) {
            return 0;
        }
        // 物理删除所有日志（无 del_flag，直接 DELETE）
        return baseMapper.delete(null);
    }

    @Override
    @Transactional
    public void saveOperLog(SysOperLog operLog) {
        save(operLog);
    }

    private Integer parseStatus(String status) {
        try {
            return Integer.parseInt(status);
        } catch (NumberFormatException e) {
            return null;
        }
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
