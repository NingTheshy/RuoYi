package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.PageResult;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.system.convert.SysJobConvert;
import com.ruoyi.system.domain.dto.req.SysJobCreateReq;
import com.ruoyi.system.domain.dto.req.SysJobQueryReq;
import com.ruoyi.system.domain.dto.req.SysJobUpdateReq;
import com.ruoyi.system.domain.dto.resp.SysJobResp;
import com.ruoyi.system.domain.entity.SysJob;
import com.ruoyi.system.mapper.SysJobMapper;
import com.ruoyi.system.service.SysJobService;
import com.ruoyi.system.job.JobInvokeUtil;
import com.ruoyi.system.job.QuartzJobExecution;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;

@Service
public class SysJobServiceImpl extends ServiceImpl<SysJobMapper, SysJob> implements SysJobService {

    private static final Logger log = LoggerFactory.getLogger(SysJobServiceImpl.class);

    private final SysJobConvert jobConvert;
    private final JobInvokeUtil jobInvokeUtil;
    private final Scheduler scheduler;

    public SysJobServiceImpl(SysJobConvert jobConvert, JobInvokeUtil jobInvokeUtil) {
        this.jobConvert = jobConvert;
        this.jobInvokeUtil = jobInvokeUtil;
        this.scheduler = initScheduler();
    }

    private Scheduler initScheduler() {
        try {
            StdSchedulerFactory factory = new StdSchedulerFactory();
            Scheduler sched = factory.getScheduler();
            sched.start();
            return sched;
        } catch (Exception e) {
            log.error("Quartz Scheduler 初始化失败", e);
            return null;
        }
    }

    @Override
    public PageResult<SysJobResp> getJobPage(SysJobQueryReq queryReq, Integer pageNum, Integer pageSize) {
        SysJob query = jobConvert.toEntity(queryReq);
        Page<SysJob> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysJob> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getJobName()), SysJob::getJobName, query.getJobName())
                .like(StringUtils.hasText(query.getJobGroup()), SysJob::getJobGroup, query.getJobGroup())
                .eq(StringUtils.hasText(query.getStatus()), SysJob::getStatus, query.getStatus())
                .orderByAsc(SysJob::getJobId);
        Page<SysJob> result = baseMapper.selectPage(page, wrapper);
        return new PageResult<>(jobConvert.toRespList(result.getRecords()), result.getTotal());
    }

    @Override
    public SysJobResp getJobById(Long jobId) {
        SysJob job = getById(jobId);
        if (job == null) {
            throw new ServiceException(404, "定时任务不存在");
        }
        return jobConvert.toResp(job);
    }

    @Override
    @Transactional
    public int createJob(SysJobCreateReq req) {
        SysJob job = jobConvert.toEntity(req);
        if (job.getJobGroup() == null) {
            job.setJobGroup("DEFAULT");
        }
        if (job.getStatus() == null) {
            job.setStatus("0");
        }
        boolean result = save(job);
        if (result && "0".equals(job.getStatus())) {
            scheduleJob(job);
        }
        return result ? 1 : 0;
    }

    @Override
    @Transactional
    public int updateJob(SysJobUpdateReq req) {
        SysJob existing = getById(req.getJobId());
        if (existing == null) {
            throw new ServiceException(404, "定时任务不存在");
        }
        SysJob job = jobConvert.toEntity(req);
        boolean result = updateById(job);
        if (result) {
            unscheduleJob(existing.getJobId());
            if ("0".equals(job.getStatus())) {
                job.setJobId(existing.getJobId());
                scheduleJob(job);
            }
        }
        return result ? 1 : 0;
    }

    @Override
    @Transactional
    public int deleteJobByIds(Long[] jobIds) {
        for (Long jobId : jobIds) {
            unscheduleJob(jobId);
        }
        return removeByIds(Arrays.asList(jobIds)) ? jobIds.length : 0;
    }

    @Override
    @Transactional
    public int changeStatus(Long jobId, String status) {
        SysJob job = getById(jobId);
        if (job == null) {
            throw new ServiceException(404, "定时任务不存在");
        }
        job.setStatus(status);
        boolean result = updateById(job);
        if (result) {
            if ("1".equals(status)) {
                unscheduleJob(jobId);
            } else {
                scheduleJob(job);
            }
        }
        return result ? 1 : 0;
    }

    @Override
    public int runJob(Long jobId) {
        SysJob job = getById(jobId);
        if (job == null) {
            throw new ServiceException(404, "定时任务不存在");
        }
        jobInvokeUtil.invokeMethod(job.getJobClassName(), job.getJobName(),
                job.getJobGroup(), job.getCronExpression());
        return 1;
    }

    private void scheduleJob(SysJob job) {
        if (scheduler == null) {
            return;
        }
        try {
            String jobKey = "job_" + job.getJobId();
            JobDetail jobDetail = JobBuilder.newJob(QuartzJobExecution.class)
                    .withIdentity(jobKey, job.getJobGroup())
                    .usingJobData("jobClassName", job.getJobClassName())
                    .usingJobData("jobName", job.getJobName())
                    .usingJobData("jobGroup", job.getJobGroup())
                    .usingJobData("cronExpression", job.getCronExpression())
                    .storeDurably()
                    .build();

            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(jobKey + "_trigger", job.getJobGroup())
                    .forJob(jobDetail)
                    .withSchedule(CronScheduleBuilder.cronSchedule(job.getCronExpression())
                            .withMisfireHandlingInstructionFireAndProceed())
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);
        } catch (Exception e) {
            log.error("[定时任务] 调度失败: jobId={}, error={}", job.getJobId(), e.getMessage(), e);
        }
    }

    private void unscheduleJob(Long jobId) {
        if (scheduler == null) {
            return;
        }
        try {
            String jobKey = "job_" + jobId;
            scheduler.deleteJob(new JobKey(jobKey, "DEFAULT"));
        } catch (Exception e) {
            log.error("[定时任务] 取消调度失败: jobId={}", jobId, e);
        }
    }
}
