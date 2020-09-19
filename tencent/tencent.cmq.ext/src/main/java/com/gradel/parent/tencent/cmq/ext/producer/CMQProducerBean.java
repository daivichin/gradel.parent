package com.gradel.parent.tencent.cmq.ext.producer;

import com.gradel.parent.tencent.cmq.api.Producer;
import com.gradel.parent.tencent.cmq.api.impl.CMQFactory;
import com.gradel.parent.tencent.cmq.api.model.CMQMessage;
import com.gradel.parent.tencent.cmq.api.model.SendResult;
import com.gradel.parent.tencent.cmq.api.serializer.MqSerializer;
import com.gradel.parent.tencent.qcloud.cmq.CMQClientException;
import lombok.extern.slf4j.Slf4j;
import java.util.Properties;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @date: 2016/11/24
 *
 * @see Producer
 */
@Slf4j
public class CMQProducerBean implements Producer {

    private Properties properties;

    private Producer producer;

    @Override
    public void start() {
        producer = CMQFactory.createProducer(properties);
        producer.start();
    }

    @Override
    public void shutdown() {
        if (producer != null)
            producer.shutdown();
    }

    @Override
    public <T> SendResult send(CMQMessage<T> message, MqSerializer<T> serializer) {
        if (null == producer) {
            throw new CMQClientException("Send Message must be called after RocketMqProducerBean started");
        }
        return producer.send(message, serializer);
    }

    @Override
    public boolean isStarted() {
        return (producer != null && producer.isStarted());
    }

    @Override
    public boolean isClosed() {
        return producer != null && producer.isClosed();
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}

