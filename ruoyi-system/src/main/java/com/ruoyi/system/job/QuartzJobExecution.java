package com.ruoyi.system.job;

import com.ruoyi.system.domain.entity.SysJob;
import com.ruoyi.system.service.SysJobLogService;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

/**
 * Quartz 任务执行代理
 * <p>Quartz 触发时调用此类，通过反射执行实际的任务类</p>
 */
@Component
public class QuartzJobExecution implements Job {

    private JobInvokeUtil jobInvokeUtil;

    public void setJobInvokeUtil(JobInvokeUtil jobInvokeUtil) {
        this.jobInvokeUtil = jobInvokeUtil;
    }

    @Override
    public void execute(JobExecutionContext context) {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        String jobClassName = dataMap.getString("jobClassName");
        String jobName = dataMap.getString("jobName");
        String jobGroup = dataMap.getString("jobGroup");
        String cronExpression = dataMap.getString("cronExpression");

        if (jobInvokeUtil != null) {
            jobInvokeUtil.invokeMethod(jobClassName, jobName, jobGroup, cronExpression);
        }
    }
}
