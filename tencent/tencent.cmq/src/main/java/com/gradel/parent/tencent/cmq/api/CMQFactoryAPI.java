package com.gradel.parent.tencent.cmq.api;

import java.util.Properties;

/**
 * 生产者发布者工厂类
 */
public interface CMQFactoryAPI {
    Producer createProducer(final Properties properties);

    Consumer createConsumer(final Properties properties);
}
