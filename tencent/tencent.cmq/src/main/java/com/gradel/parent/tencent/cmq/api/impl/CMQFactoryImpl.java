package com.gradel.parent.tencent.cmq.api.impl;

import com.gradel.parent.tencent.cmq.api.CMQFactoryAPI;
import com.gradel.parent.tencent.cmq.api.Consumer;
import com.gradel.parent.tencent.cmq.api.Producer;

import java.util.Properties;


public class CMQFactoryImpl implements CMQFactoryAPI {
    @Override
    public Producer createProducer(final Properties properties) {
        ProducerImpl producer = new ProducerImpl(properties);
        return producer;
    }


    @Override
    public Consumer createConsumer(final Properties properties) {
        ConsumerImpl consumer = new ConsumerImpl(properties);
        return consumer;
    }
}
