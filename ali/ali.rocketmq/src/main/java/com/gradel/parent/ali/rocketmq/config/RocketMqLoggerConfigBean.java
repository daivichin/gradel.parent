package com.gradel.parent.ali.rocketmq.config;

import com.gradel.parent.common.util.util.StringUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

import java.util.Arrays;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @date: 2016/11/24
 * @Description: 初始化mq log日志
 *
 * @see com.aliyun.openservices.ons.api.impl.util.ClientLoggerUtil
 */
@Slf4j
public class RocketMqLoggerConfigBean implements InitializingBean {

    /**
     * 系统变量ClientLoggerUtil
     */
    private static final String CLIENT_LOG_ROOT = "ons.client.logRoot";
    private static final String CLIENT_LOG_FILEMAXINDEX = "ons.client.logFileMaxIndex";
    private static final String CLIENT_LOG_LEVEL = "ons.client.logLevel";
    private static final String[] levelArray = {"ERROR", "WARN", "INFO", "DEBUG"};

    /**
     * 说明：
     * 单个日志文件大小：64MB
     * 保存历史日志文件的最大个数：100个（默认为10）
     * 日志级别：默认为 INFO
     */

    /**
     * mq log 目录
     */
    @Setter
    private String logRoot;

    /**
     * 级别
     */
    @Setter
    private String logLevel;

    /**
     * Log max index
     */
    @Setter
    private int logMaxIndex;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (StringUtil.isNotBlank(logRoot)) {
            System.setProperty(CLIENT_LOG_ROOT, logRoot);
        }
        if (StringUtil.isNotBlank(logLevel)) {
            if (!Arrays.asList(levelArray).contains(logLevel)) {
                throw new IllegalArgumentException("Mq Log level must be [ERROR, WARN, INFO, DEBUG]");
            } else {
                System.setProperty(CLIENT_LOG_LEVEL, logLevel);
            }
        }

        if (logMaxIndex <= 0 || logMaxIndex > 100)
            throw new IllegalArgumentException("Mq Log max index must be Between 1 and 100");
        System.setProperty(CLIENT_LOG_FILEMAXINDEX, logMaxIndex + "");
    }
}
