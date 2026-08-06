package com.ruoyi.system.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 示例定时任务
 * <p>输出当前时间到日志</p>
 */
@Component
public class SampleJob extends AbstractJob {

    private static final Logger log = LoggerFactory.getLogger(SampleJob.class);

    @Override
    public void execute() {
        log.info("[SampleJob] 定时任务执行时间: {}", java.time.LocalDateTime.now());
    }
}
