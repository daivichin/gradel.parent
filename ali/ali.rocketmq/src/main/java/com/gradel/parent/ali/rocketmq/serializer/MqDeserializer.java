package com.gradel.parent.ali.rocketmq.serializer;

import com.gradel.parent.ali.rocketmq.exception.SerializationException;
import com.gradel.parent.ali.rocketmq.model.MessageContext;
import com.gradel.parent.ali.rocketmq.model.MqBaseMessageBody;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @date: 2017-11-23
 * @Description:
 */
@FunctionalInterface
public interface MqDeserializer<T> {
    MqBaseMessageBody<T> deserialize(byte[] bytes, MessageContext messageContext) throws SerializationException;
}