package com.gradel.parent.ali.rocketmq.consumer.order.api;

import com.aliyun.openservices.ons.api.order.OrderAction;
import com.gradel.parent.ali.rocketmq.model.MessageContext;
import com.gradel.parent.ali.rocketmq.model.MqBaseMessageBody;
import com.gradel.parent.ali.rocketmq.model.MqMessageBody;
import com.gradel.parent.ali.rocketmq.serializer.MqDeserializer;
import com.gradel.parent.common.util.api.topic.MqTopic;

/**
 * 有序消息消费接口
 * Created with IntelliJ IDEA.
 * @author: sdeven.chen.dongwei@gmail.com
 * @date: 2016/7/14
 *
 */
public interface RocketMqOrderMessageListener<T> extends MqDeserializer<T> {

    /**
     * 消费结果，如果应用抛出异常或者返回Null等价于返回Action.ReconsumeLater
     * @param record 消息记录
     * @param messageContext 消息上下文
     * @see MqMessageBody
     * @return Action
     */
    OrderAction call(MqBaseMessageBody<T> record, MessageContext messageContext) throws Exception;

    /**
     * 订阅的主题
     * @return
     */
    default MqTopic subscriTopic(){
        return null;
    }
}
