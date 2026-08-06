package com.ruoyi.system.job;

/**
 * 定时任务抽象基类
 * <p>所有定时任务需继承此类并实现 execute 方法</p>
 */
public abstract class AbstractJob {

    public abstract void execute();
}
