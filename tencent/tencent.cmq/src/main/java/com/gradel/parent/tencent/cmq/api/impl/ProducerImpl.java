package com.gradel.parent.tencent.cmq.api.impl;

import com.gradel.parent.tencent.cmq.api.Producer;
import com.gradel.parent.tencent.cmq.api.enums.ErrorCodeEnum;
import com.gradel.parent.tencent.cmq.api.model.CMQMessage;
import com.gradel.parent.tencent.cmq.api.model.SendResult;
import com.gradel.parent.tencent.cmq.api.serializer.MqSerializer;
import com.gradel.parent.tencent.cmq.api.util.ClientLogger;
import com.gradel.parent.tencent.qcloud.cmq.CMQClientException;
import com.gradel.parent.common.util.util.ExceptionUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProducerImpl extends CMQConfigAbstract implements Producer {

    private final static Logger log = ClientLogger.getLog();

    private final AtomicBoolean started = new AtomicBoolean(false);

    private DefaultSendMessage defaultSendMessage;

    private Properties properties;

    public ProducerImpl() {
        defaultSendMessage = new DefaultSendMessage(this);
    }

    public ProducerImpl(final Properties properties) {
        this.properties = properties;
        defaultSendMessage = new DefaultSendMessage(this);
    }


    @Override
    public void start() {
        try {
            if (this.started.compareAndSet(false, true)) {
                super.init(properties);
            }
        } catch (Exception e) {
            throw new CMQClientException(e.getMessage());
        }
    }


    @Override
    public void shutdown() {
        if (this.started.compareAndSet(true, false)) {
            //TODO
        }
    }

    @Override
    public <T> SendResult<String> send(final CMQMessage<T> message, final MqSerializer<T> serializer) {

        if (message == null) {
            log.error("CMQ send Failure, Because Message content is null");
            return SendResult.newInstance(ErrorCodeEnum.FAILURE_MSG_CONTEN_EMPTY.getCode(), "CMQ send Failure, Because Message content is null");
        }

        if (StringUtils.isBlank(message.getQueue())) {
            log.error("CMQ send Failure, Because Message queue is empty");
            return SendResult.newInstance(ErrorCodeEnum.FAILURE_MSG_QUEUE_EMPTY.getCode(), "CMQ send Failure, Because Message queue is empty");
        }

        if (message.getContent() == null) {
            log.error("CMQ send Failure, Because Message content is null, queue:[{}]", message.getQueue());
            return SendResult.newInstance(ErrorCodeEnum.FAILURE_MSG_CONTEN_EMPTY.getCode(), "CMQ send Failure, Because Message content is null");
        }

        if (serializer == null) {
            log.error("CMQ send Failure, Because serializer is null, queue:[{}]", message.getQueue());
            return SendResult.newInstance(ErrorCodeEnum.FAILURE_MSG_SERIALIZER_EMPTY.getCode(), "CMQ send Failure, Because serializer is null");
        }

        String body = null;
        try {
            body = serializer.serialize(message.getContent());
        } catch (Exception e) {
            log.error("SendMessage serializer exception, Exception:{}", ExceptionUtil.getAsString(e));
            return SendResult.newInstance(ErrorCodeEnum.FAILURE_MSG_SERIALIZER_ERR.getCode(), ErrorCodeEnum.FAILURE_MSG_SERIALIZER_ERR.getDesc());
        }
        return defaultSendMessage.sendMessage(message.getQueue(), body, message.getDelayTimeSeconds(), message.getConnectTimeoutMilliseconds(), message.getReadTimeoutMilliseconds());


    }

    /*@Override
    public void sendOneway(CMQMessage message) {
        defaultSendMessage.sendMessage(message);
    }

    @Override
    public void sendAsync(CMQMessage message, SendCallback sendCallback) {
        defaultSendMessage.sendMessage(message);
    }*/

    @Override
    public boolean isStarted() {
        return started.get();
    }


    @Override
    public boolean isClosed() {
        return !isStarted();
    }

}
