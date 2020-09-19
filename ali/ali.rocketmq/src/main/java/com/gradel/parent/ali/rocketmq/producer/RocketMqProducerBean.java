package com.gradel.parent.ali.rocketmq.producer;

import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.SendCallback;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.exception.ONSClientException;
import com.gradel.parent.ali.rocketmq.model.RockMqMessage;
import com.gradel.parent.ali.rocketmq.model.RockMqSendResult;
import com.gradel.parent.ali.rocketmq.producer.api.RocketMqProducer;
import com.gradel.parent.ali.rocketmq.serializer.MqSerializer;
import com.gradel.parent.common.util.api.model.Message;
import com.gradel.parent.common.util.util.ExceptionUtil;
import com.gradel.parent.common.util.threadlocal.SerialNo;
import lombok.extern.slf4j.Slf4j;

import java.util.Properties;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @date: 2016/11/24
 * @Description: 无序消息生产者接口
 * @see Producer
 * 记得要调用 start/shutdown
 */
@Slf4j
public class RocketMqProducerBean extends RocketMqProducerAbstract implements RocketMqProducer {

    private Properties properties;

    private Producer producer;

    @Override
    public void start() {
        if (null == this.properties) {
            throw new ONSClientException("properties not set");
        }

        super.init(properties);

        this.producer = ONSFactory.createProducer(this.properties);
        this.producer.start();
    }

    @Override
    public void updateCredential(Properties credentialProperties) {
        if(this.producer != null){
            this.producer.updateCredential(credentialProperties);
        }
    }

    @Override
    public void shutdown() {
        if (this.producer != null) {
            this.producer.shutdown();
        }
    }

    @Override
    public <T> boolean send(RockMqMessage<T> message, MqSerializer<T> serializer) {
        return sendBackResult(message, serializer).isSuccess();
    }

    /**
     * 同步发送消息
     *
     * @param message
     * @return 发送结果，true 表示发送成功，否则发送失败
     *
     * @see Message
     */
    @Override
    public <T> RockMqSendResult sendBackResult(final RockMqMessage<T> message, final MqSerializer<T> serializer){
        if(message.getContent() == null){
            Object[] params = {SerialNo.getSerialNo(), message.getTopic(), message.getTag(), message.getKey()};
            log.error("[{}] Mq send Failure, Because Message content is null , topic:[{}], tag:[{}], key:[{}]", params);
            return RockMqSendResult.fail();
        }

        SendResult sendResult = null;
        long start = System.currentTimeMillis();
        try {
            byte[] bytes = serializer.serialize(message.getContent());
            message.setBody(bytes);
            if(!checkBeforeSendMsg(message)){
                return RockMqSendResult.fail();
            }
            sendResult = producer.send(message);
        }catch (Throwable e){
            Object[] params = {SerialNo.getSerialNo(), message.getTopic(), message.getTag(), message.getKey(), message.getContent(), (System.currentTimeMillis() - start), ExceptionUtil.getAsString(e)};
            log.error("[{}] Mq send Exception, topic:[{}], tag:[{}], key:[{}], message:[{}], costTime:{}ms, Some Exception Occur:[{}]", params);
        }
        return RockMqSendResult.successIfNotNull(sendResult);
    }

    /**
     * 发送消息，Oneway形式，服务器不应答，无法保证消息是否成功到达服务器
     *
     * @param message
     * @return 发送结果，true 表示发送成功，否则发送失败
     *
     * @see Message
     */
    @Override
    public <T> boolean sendOneway(final RockMqMessage<T> message, final MqSerializer<T> serializer){
        if(message.getContent() == null){
            Object[] params = {SerialNo.getSerialNo(), message.getTopic(), message.getTag(), message.getKey()};
            log.error("[{}] Mq sendOneway Failure, Because Message content is null , topic:[{}], tag:[{}], key:[{}]", params);
            return false;
        }

        boolean sendSuccess = true;
        long start = System.currentTimeMillis();
        try {
            byte[] bytes = serializer.serialize(message.getContent());
            message.setBody(bytes);

            if(!checkBeforeSendMsg(message)){
                return false;
            }
            producer.sendOneway(message);
        }catch (Throwable e){
            sendSuccess = false;
            Object[] params = {SerialNo.getSerialNo(), message.getTopic(), message.getTag(), message.getKey(), message.getContent(), (System.currentTimeMillis() - start), ExceptionUtil.getAsString(e)};
            log.error("[{}] Mq send Exception(Oneway), topic:[{}], tag:[{}], key:[{}], message:[{}], costTime:{}ms, Some Exception Occur:[{}]", params);
        }
        return sendSuccess;
    }

    /**
     * 发送消息，异步Callback形式
     *
     * @param message
     * @return 发送结果，true 表示发送成功，否则发送失败
     *
     * @see Message
     */
    @Override
    public <T> boolean sendAsync(final RockMqMessage<T> message, final MqSerializer<T> serializer, final SendCallback sendCallback){
        if(message.getContent() == null){
            Object[] params = {SerialNo.getSerialNo(), message.getTopic(), message.getTag(), message.getKey()};
            log.error("[{}] Mq sendAsync Failure, Because Message content is null , topic:[{}], tag:[{}], key:[{}]", params);
            return false;
        }

        boolean sendSuccess = true;
        long start = System.currentTimeMillis();
        try {
            byte[] bytes = serializer.serialize(message.getContent());
            message.setBody(bytes);

            if(!checkBeforeSendMsg(message)){
                return false;
            }
            producer.sendAsync(message, sendCallback);
        }catch (Throwable e){
            sendSuccess = false;
            Object[] params = {SerialNo.getSerialNo(), message.getTopic(), message.getTag(), message.getKey(), message.getContent(), (System.currentTimeMillis() - start), ExceptionUtil.getAsString(e)};
            log.error("[{}] Mq send Exception(Async), topic:[{}], tag:[{}], key:[{}], message:[{}], costTime:{}ms, Some Exception Occur:[{}]", params);
        }
        return sendSuccess;
    }

    public Properties getProperties() {
        return properties;
    }


    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public boolean isStarted() {
        return this.producer.isStarted();
    }

    @Override
    public boolean isClosed() {
        return this.producer.isClosed();
    }
}

