package com.gradel.parent.tencent.cmq.api;

import com.gradel.parent.tencent.cmq.api.model.SendResult;
import com.gradel.parent.tencent.cmq.api.serializer.MqSerializer;
import com.gradel.parent.tencent.cmq.api.model.CMQMessage;

/**
 * 消息生产者接口
 */
public interface Producer extends Admin {
    /**
     * 启动服务
     */
    void start();


    /**
     * 关闭服务
     */
    void shutdown();


    /**
     * 同步发送消息， 一定要判断状态码，success:true 表示发送成功，否则发送失败，此方法不会抛异常
     *
     * @param message
     * @return 发送结果，含消息Id
     */
    <T> SendResult send(final CMQMessage<T> message, final MqSerializer<T> serializer);

    /**
     * 发送消息，Oneway形式，服务器不应答，无法保证消息是否成功到达服务器
     *
     * @param message
     *//*
    void sendOneway(final CMQMessage message);

    *//**
     * 发送消息，异步Callback形式
     *
     * @param message
     *//*
    void sendAsync(final CMQMessage message, final SendCallback sendCallback);*/
}
