package com.gradel.parent.tencent.cmq.api.serializer;


import com.gradel.parent.tencent.cmq.api.exception.SerializationException;
import com.gradel.parent.common.util.api.model.BaseMessage;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @date: 2017-11-23
 * @Description:
 */
@FunctionalInterface
public interface MqSerializer<T> {
    String serialize(BaseMessage<T> t) throws SerializationException;
}