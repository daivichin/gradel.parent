package com.gradel.parent.common.task;

import com.gradel.parent.common.task.util.ThreadPoolTaskUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @date: 2016/9/18
 * @Description:线程池(默认启用 ThreadPoolTaskUtil 工具类)
 */
@Slf4j
public class ThreadPoolTaskExecutor extends org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor {

    public void setEnableThreadPoolTaskUtil(boolean enableThreadPoolTaskUtil) {
        if (enableThreadPoolTaskUtil) {
            ThreadPoolTaskUtil.pool(this);
        } else {
            ThreadPoolTaskUtil.pool(null);
        }
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();

        /**
         * 默认为true
         */
        setEnableThreadPoolTaskUtil(true);
    }
}
