package com.gradel.parent.common.task.thread;

import lombok.extern.slf4j.Slf4j;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @date: 2016/10/12
 * @Description: 可关闭线程----while(isRunning){} 执行
 * 与 WorkThread 唯一区别是 while(isRunning){}
 * 只有调关闭才会退出
 * @see WorkThread
 */
@Slf4j
public abstract class ShutdownableThread extends WorkThread {

    public ShutdownableThread(String name, boolean isInterruptible) {
        this(name, isInterruptible, false);
    }

    public ShutdownableThread(String name, boolean isInterruptible, boolean isDaemon) {
        super(name, isInterruptible, isDaemon);
    }

    /**
     * 执行N次
     */
    public abstract void doWork();

    @Override
    public void workOnce() {
        while (this.isRunning().get()) {
            this.doWork();
        }
    }
}