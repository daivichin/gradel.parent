package com.gradel.parent.ali.rocketmq.serializer;

import com.gradel.parent.ali.rocketmq.exception.SerializationException;
import com.gradel.parent.ali.rocketmq.model.MqBaseMessageBody;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @date: 2017-11-23
 * @Description:
 */
@FunctionalInterface
public interface MqSerializer<T> {
    byte[] serialize(MqBaseMessageBody<T> t) throws SerializationException;
}