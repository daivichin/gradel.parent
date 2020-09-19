package com.gradel.parent.ali.rocketmq.autoconfigure.bean;

import com.gradel.parent.ali.rocketmq.autoconfigure.MqEnvProperties;
import com.gradel.parent.ali.rocketmq.consumer.order.RocketMqOrderConsumerBean;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.Properties;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @date 2018/12/30 下午3:21
 * start/shutdown
 *
 * 注意
 *  （RocketMqMessageListener） bean的名字 不能与 消费组(mq.consumers.fileExportConsumer)配置的名字一样
 */
@Slf4j
@Data
@EnableConfigurationProperties(MqEnvProperties.class)
public class RocketMqOrderConsumerPostBean extends RocketMqOrderConsumerBean implements InitializingBean {

    private String beanName;

    private final MqEnvProperties mqEnvProperties;

    public RocketMqOrderConsumerPostBean(MqEnvProperties mqEnvProperties) {
        this.mqEnvProperties = mqEnvProperties;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (mqEnvProperties != null) {
            Properties properties = mqEnvProperties.getConsumers().get(getBeanName());
            if (properties == null || properties.isEmpty()) {
                throw new IllegalArgumentException("###########  Can not found Consumer[" + beanName + "] config from properties. see MqEnvProperties.class");
            }else{
                log.info("###########  found Consumer[" + beanName + "] config from properties. start to setProperties");
            }
            this.setProperties(properties);
        }
    }
    /*
    @Override
    public void destroy() throws Exception {
        shutdown();
    }*/
}
