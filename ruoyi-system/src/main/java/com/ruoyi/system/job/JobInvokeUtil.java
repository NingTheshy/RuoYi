package com.ruoyi.system.job;

import com.ruoyi.system.domain.entity.SysJobLog;
import com.ruoyi.system.service.SysJobLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 定时任务执行器
 * <p>通过反射调用任务执行类的方法</p>
 */
@Component
public class JobInvokeUtil {

    private static final Logger log = LoggerFactory.getLogger(JobInvokeUtil.class);

    private final SysJobLogService jobLogService;

    public JobInvokeUtil(SysJobLogService jobLogService) {
        this.jobLogService = jobLogService;
    }

    /**
     * 执行任务方法
     */
    public void invokeMethod(String jobClassName, String jobName, String jobGroup,
                             String cronExpression) {
        SysJobLog jobLog = new SysJobLog();
        jobLog.setJobName(jobName);
        jobLog.setJobGroup(jobGroup);
        jobLog.setJobClassName(jobClassName);
        jobLog.setCronExpression(cronExpression);
        jobLog.setJobTime(LocalDateTime.now());
        jobLog.setCreateTime(LocalDateTime.now());

        try {
            Class<?> clazz = Class.forName(jobClassName);
            Object instance = clazz.getDeclaredConstructor().newInstance();
            Method method = clazz.getMethod("execute");
            method.invoke(instance);
            jobLog.setStatus("0");
            jobLog.setErrorMsg("");
        } catch (Exception e) {
            log.error("[定时任务] 执行失败: jobName={}, error={}", jobName, e.getMessage(), e);
            jobLog.setStatus("1");
            jobLog.setErrorMsg(truncate(e.getMessage(), 2000));
        }

        jobLogService.saveJobLog(jobLog);
    }

    private String truncate(String str, int max) {
        return str != null && str.length() > max ? str.substring(0, max) : str;
    }
}
