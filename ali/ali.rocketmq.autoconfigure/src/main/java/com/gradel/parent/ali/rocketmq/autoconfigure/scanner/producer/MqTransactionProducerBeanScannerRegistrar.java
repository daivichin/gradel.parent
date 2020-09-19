package com.gradel.parent.ali.rocketmq.autoconfigure.scanner.producer;

import com.gradel.parent.ali.rocketmq.autoconfigure.MqEnvProperties;
import com.gradel.parent.ali.rocketmq.autoconfigure.RegUtil;
import com.gradel.parent.ali.rocketmq.producer.RocketMqTransactionProducerBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 事务生产者s
 * @author: sdeven.chen.dongwei@gmail.com
 * @date 2019/01/02 下午11:14
 */
@Slf4j
public class MqTransactionProducerBeanScannerRegistrar
        implements BeanFactoryAware, ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {

    private BeanFactory beanFactory;

    private ResourceLoader resourceLoader;

    private Environment environment;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        log.debug("Searching for Mq TransactionProducer from Environment");

        String producerPrefix = MqEnvProperties.MQ_TRANSACTION_PRODUCERS_PREFIX;
        Class<?> beanClass = RocketMqTransactionProducerBean.class;
        RegUtil.regRocketMqProducerBeans(registry, producerPrefix, beanClass, environment);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
