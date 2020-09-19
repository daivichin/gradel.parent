package com.gradel.parent.ali.rocketmq.autoconfigure;

import com.gradel.parent.ali.rocketmq.autoconfigure.scanner.producer.MqOrderProducerBeanScannerRegistrar;
import com.gradel.parent.ali.rocketmq.autoconfigure.scanner.producer.MqProducerBeanScannerRegistrar;
import com.gradel.parent.ali.rocketmq.autoconfigure.scanner.producer.MqTransactionProducerBeanScannerRegistrar;
import com.gradel.parent.ali.rocketmq.config.RocketMqLoggerConfigBean;
import com.gradel.parent.ali.rocketmq.consumer.RocketMqConsumerBean;
import com.gradel.parent.ali.rocketmq.producer.RocketMqProducerBean;
import com.gradel.parent.ali.rocketmq.topic.env.TopicEnvBean;
import com.gradel.parent.common.util.api.env.ITopicEnv;
import com.gradel.parent.common.util.api.env.TopicEnvManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.FieldRetrievingFactoryBean;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.Set;

import static java.util.Collections.emptySet;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @date: 2018-03-21
 * @Description:
 */
@Slf4j
@Configuration
@ConditionalOnClass({RocketMqConsumerBean.class, RocketMqProducerBean.class})
@EnableConfigurationProperties(MqEnvProperties.class)
public class AliMqAutoConfiguration {

    private final MqEnvProperties properties;

    public AliMqAutoConfiguration(MqEnvProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void checkConfigFileExists() throws Exception {
        /*boolean found = !(this.properties == null || StringUtils.isEmpty(properties.getTopicEnv()));
        Assert.state(found, "Cannot find config [mq.topicEnv], please add config file or check your MqEnvProperties configuration");*/
        /**
         * 初始化日志
         */
        RocketMqLoggerConfigBean mqLoggerConfigBean = new RocketMqLoggerConfigBean();
        mqLoggerConfigBean.setLogLevel(properties.getLogLevel());
        mqLoggerConfigBean.setLogRoot(properties.getLogRoot());
        mqLoggerConfigBean.setLogMaxIndex(properties.getLogMaxIndex());
        mqLoggerConfigBean.afterPropertiesSet();

    }

    /**
     * 环境 生产或测试
     *
     * @return
     * @throws Exception
     */
    @Bean
    @ConditionalOnProperty(prefix = "mq", value = "topic-env")
    @ConditionalOnMissingBean(name = "topicEnvBean")
    public TopicEnvBean topicEnvBean() throws Exception {
        if (StringUtils.isEmpty(properties.getTopicEnv())) {
            throw new IllegalArgumentException("MqConsumer properties[mq.topicEnv] must not empty!");
        }
        FieldRetrievingFactoryBean topicEnvFactoryBean = new FieldRetrievingFactoryBean();
        topicEnvFactoryBean.setStaticField(properties.getTopicEnv());
        topicEnvFactoryBean.afterPropertiesSet();
        TopicEnvBean topicEnvBean = new TopicEnvBean();
        ITopicEnv topicEnv = (ITopicEnv) topicEnvFactoryBean.getObject();
        topicEnvBean.setTopicEnv(topicEnv);

        TopicEnvManager.topicEnv(topicEnv);
//        topicEnvBean.afterPropertiesSet();
        return topicEnvBean;
    }

    /**
     * 日志
     * @return
     * @throws Exception
     */
    /*@Bean
    @ConditionalOnMissingBean(RocketMqLoggerConfigBean.class)
    @ConditionalOnProperty(prefix = "mq", name = "log-root", matchIfMissing = false)
    public RocketMqLoggerConfigBean rocketMqLoggerConfigBean() throws Exception {
        RocketMqLoggerConfigBean mqLoggerConfigBean = new RocketMqLoggerConfigBean();
        mqLoggerConfigBean.setLogLevel(properties.getLogLevel());
        mqLoggerConfigBean.setLogMaxIndex(properties.getLogMaxIndex());
        mqLoggerConfigBean.setLogRoot(properties.getLogRoot());
        return mqLoggerConfigBean;
    }*/

    /**
     * 生产者
     *
     * @return
     * @throws Exception
     */
    /*@Bean(initMethod = "start", destroyMethod = "shutdown")
    @ConditionalOnMissingBean(RocketMqProducerBean.class)
    @ConditionalOnResource(resources = {"classpath:default-mq-producer.properties"})
    public RocketMqProducerBean defaultMqProducer() throws Exception {
        RocketMqProducerBean mqProducerBean = new RocketMqProducerBean();
        Properties properties = PropertiesLoaderUtils.loadProperties(new EncodedResource(new ClassPathResource("default-mq-producer.properties")));
        mqProducerBean.setProperties(properties);
        return mqProducerBean;
    }*/

    /**
     * 普通生产者s, 顺序生产者s, 事务生产者s
     */
    @Configuration
    @AutoConfigureAfter(name = "topicEnvBean")
    @Import({
            MqProducerBeanScannerRegistrar.class, //普通生产者
            MqOrderProducerBeanScannerRegistrar.class, //顺序生产者
            MqTransactionProducerBeanScannerRegistrar.class//事务生产者
    })
    public static class MqProducerBeanAutoScannerRegConfiguration {
    }

    @Configuration
    @ConditionalOnProperty(name = MqEnvProperties.BASE_PACKAGES_PROPERTY_NAME)
    public static class MqProducerAndConsumerBeanAutoScannerRegConfiguration {

        //无序／事务消费者
        @Bean
        public RocketMqConsumerBeanPostProcessor rocketMqConsumerBeanPostProcessor(Environment environment) {
            Set<String> packagesToScan = environment.getProperty(MqEnvProperties.BASE_PACKAGES_PROPERTY_NAME, Set.class, emptySet());
            return new RocketMqConsumerBeanPostProcessor(MqConsumer.class, packagesToScan);
        }
        //有序消费者
        @Bean
        public RocketMqConsumerBeanPostProcessor rocketMqOrderConsumerBeanPostProcessor(Environment environment) {
            Set<String> packagesToScan = environment.getProperty(MqEnvProperties.BASE_PACKAGES_PROPERTY_NAME, Set.class, emptySet());
            return new RocketMqConsumerBeanPostProcessor(MqOrderConsumer.class, packagesToScan);
        }
    }
}
