package com.gradel.parent.common.task.thread;

import com.gradel.parent.common.util.util.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @date: 2016/10/12
 * @Description:可关闭线程-》只执行一次
 */
@Slf4j
public abstract class WorkThread extends Thread {
    private final String name;
    private final boolean isInterruptible;
    private final AtomicBoolean isRunning;
    private final CountDownLatch shutdownLatch;

    public WorkThread(String name, boolean isInterruptible) {
        this(name, isInterruptible, false);
    }

    public WorkThread(String name, boolean isInterruptible, boolean isDaemon) {
        super(name);
        this.name = name;
        this.isInterruptible = isInterruptible;
        this.setDaemon(isDaemon);
        this.isRunning = new AtomicBoolean(true);
        this.shutdownLatch = new CountDownLatch(1);
    }

    public String name() {
        return this.name;
    }

    public boolean isInterruptible() {
        return this.isInterruptible;
    }

    public AtomicBoolean isRunning() {
        return this.isRunning;
    }

    protected CountDownLatch shutdownLatch() {
        return this.shutdownLatch;
    }

    public void shutdown() {
        this.initiateShutdown();
        this.awaitShutdown();
    }

    public boolean initiateShutdown() {
        boolean shutdown;
        if (this.isRunning().compareAndSet(true, false)) {
            if (log.isDebugEnabled()) {
                log.debug("Starting Shutting of thread {}", getName());
            }
            this.isRunning().set(false);
            if (this.isInterruptible()) {
                this.interrupt();
            }
            shutdown = true;
        } else {
            shutdown = false;
        }

        return shutdown;
    }

    public void awaitShutdown() {
        try {
            this.shutdownLatch().await();
        } catch (InterruptedException e) {
            log.error("Await Shutdown of thread {}, Exception:{}", getName(), ExceptionUtil.getAsString(e));
        }
        if (log.isDebugEnabled()) {
            log.debug("Shutdown completed of thread {}", getName());
        }
    }

    /**
     * 只执行一次
     */
    public abstract void workOnce();

    /**
     * 线程退出 run()里的work()后调用此方法
     */
    public void doShutDownComplete() {

    }

    @Override
    public void run() {
        if (log.isDebugEnabled()) {
            log.debug("Starting of thread {}", getName());
        }
        try {
            this.workOnce();
        } catch (final Throwable e) {
            log.error("Thread {} exiting with uncaught exception: {}", getName(), ExceptionUtil.getAsString(e));
        } finally {
            try {
                doShutDownComplete();
            } catch (final Throwable e) {
                log.error("ShutDown Complete Thread {} with exception: {}", getName(), ExceptionUtil.getAsString(e));
            }
        }
        this.isRunning().compareAndSet(true, false);
        this.shutdownLatch().countDown();
        if (log.isDebugEnabled()) {
            log.debug("Stopped of thread {}", getName());
        }
    }


}