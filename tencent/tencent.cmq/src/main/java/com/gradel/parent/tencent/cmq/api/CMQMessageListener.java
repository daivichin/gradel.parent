package com.gradel.parent.tencent.cmq.api;

import com.gradel.parent.tencent.cmq.api.model.CMQMessageBody;
import com.gradel.parent.tencent.cmq.api.model.MessageContext;
import com.gradel.parent.tencent.cmq.api.serializer.MqDeserializer;
import com.gradel.parent.common.util.api.model.BaseMessage;

/**
 * 消息监听器，Consumer注册消息监听器来订阅消息
 */
public interface CMQMessageListener<T> extends MqDeserializer<T> {
    /**
     * 消费消息接口，由应用来实现<br>
     *
     * @param message see CMQMessageBody
     *         消息
     * @param context
     *         消费上下文
     *
     * @return 消费结果，如果应用抛出异常或者返回Null等价于返回Action.ReconsumeLater
     *
     * @see CMQMessageBody
     */
    Action consume(final BaseMessage<T> message, final MessageContext context);

    /**
     * 订阅的队列
     * @return
     */
    default QueueName subscriQueue(){
        return null;
    }
}
