package com.gradel.parent.ali.rocketmq.autoconfigure;

import com.gradel.parent.ali.rocketmq.producer.api.RocketMqTransactionMessageChecker;
import lombok.Data;
import lombok.ToString;

import java.util.Properties;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @date: 2018-03-23
 * @Description:MQ 环境配置，如生产环境、测试环境
 */
//@ConfigurationProperties(prefix = "mq")
@Data
@ToString
public class TransactionProducerProperties extends Properties{

    /**
     * 事务消息回调检查器clazz
     * @see RocketMqTransactionMessageChecker
     */
//    private String transactionMessageCheckerClazz;

    /**
     * 事务消息回调检查器beanName
     * @see RocketMqTransactionMessageChecker
     */
    private String transactionMessageCheckerBeanName;

}
