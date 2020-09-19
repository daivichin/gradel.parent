package com.gradel.parent.ali.rocketmq.autoconfigure;


import com.gradel.parent.ali.rocketmq.consumer.api.RocketMqMessageListener;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @date: 2018-03-21
 * @Description: 无序
 * @see RocketMqMessageListener
 */
@Target({METHOD, TYPE})
@Retention(RUNTIME)
@Inherited
public @interface MqConsumer {

    /**
     * 分组，不同组，则会根据组的名字加载对应的配置
     * @return
     */
    String group() default "defaultMqConsumer";//则当前的配置项为 mq.consumers.defaultMqConsumer.XXX = XXX
}