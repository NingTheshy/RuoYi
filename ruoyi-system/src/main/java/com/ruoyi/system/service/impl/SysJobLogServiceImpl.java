package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.PageResult;
import com.ruoyi.system.convert.SysJobLogConvert;
import com.ruoyi.system.domain.dto.req.SysJobLogQueryReq;
import com.ruoyi.system.domain.dto.resp.SysJobLogResp;
import com.ruoyi.system.domain.entity.SysJobLog;
import com.ruoyi.system.mapper.SysJobLogMapper;
import com.ruoyi.system.service.SysJobLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;

@Service
public class SysJobLogServiceImpl extends ServiceImpl<SysJobLogMapper, SysJobLog> implements SysJobLogService {

    private final SysJobLogConvert jobLogConvert;

    public SysJobLogServiceImpl(SysJobLogConvert jobLogConvert) {
        this.jobLogConvert = jobLogConvert;
    }

    @Override
    public PageResult<SysJobLogResp> getJobLogPage(SysJobLogQueryReq queryReq, Integer pageNum, Integer pageSize) {
        Page<SysJobLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysJobLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(queryReq.getJobName()), SysJobLog::getJobName, queryReq.getJobName())
                .eq(StringUtils.hasText(queryReq.getStatus()), SysJobLog::getStatus, queryReq.getStatus())
                .orderByDesc(SysJobLog::getJobLogId);
        Page<SysJobLog> result = baseMapper.selectPage(page, wrapper);
        return new PageResult<>(jobLogConvert.toRespList(result.getRecords()), result.getTotal());
    }

    @Override
    @Transactional
    public int deleteJobLogByIds(Long[] jobLogIds) {
        return removeByIds(Arrays.asList(jobLogIds)) ? jobLogIds.length : 0;
    }

    @Override
    @Transactional
    public int cleanJobLog() {
        long count = count();
        if (count == 0) {
            return 0;
        }
        return baseMapper.delete(null);
    }

    @Override
    @Transactional
    public void saveJobLog(SysJobLog jobLog) {
        save(jobLog);
    }
}
