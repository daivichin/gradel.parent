package com.gradel.parent.common.task.boot.autoconfigure;

import com.gradel.parent.common.task.ThreadPoolTaskExecutor;
import com.gradel.parent.common.util.exception.SystemException;
import com.gradel.parent.common.util.util.ReflectiveOperationUtil;
import com.gradel.parent.common.util.util.SpringBootPropertyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Map;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @date 2019/01/31 上午11:52
 */
@Slf4j
@Configuration
@ConditionalOnProperty(value = ThreadPoolTaskExecutorConfiguration.THREAD_POOL_BOOTSTRAP_ENABLED, havingValue = "true", matchIfMissing = false)
public class ThreadPoolTaskExecutorConfiguration implements EnvironmentAware {
    static final String THREAD_POOL_BOOTSTRAP_ENABLED = "thread.pool.bootstrap.enabled";
    private static final String THREAD_POOL_CONFIG = "thread.pool.config";

    private Map<String, Object> threadPoolConfigs;

    @Bean(destroyMethod = "shutdown")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        try {
            return (ThreadPoolTaskExecutor) ReflectiveOperationUtil.getInstance(ThreadPoolTaskExecutor.class.getName(), threadPoolConfigs);
        } catch (ReflectiveOperationException e) {
            throw new SystemException("Can't instance " + ThreadPoolTaskExecutor.class.getName() + "!", e);
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.threadPoolConfigs = this.getThreadPoolConfigs(environment);
    }

    private Map<String, Object> getThreadPoolConfigs(Environment environment) {
        return SpringBootPropertyUtil.handle(environment, THREAD_POOL_CONFIG, Map.class);
    }
}
