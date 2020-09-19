package com.gradel.parent.tencent.cmq.ext.consumer;

import com.gradel.parent.tencent.cmq.api.CMQMessageListener;
import com.gradel.parent.tencent.cmq.api.Consumer;
import com.gradel.parent.tencent.cmq.api.QueueName;
import com.gradel.parent.tencent.cmq.api.impl.CMQFactory;
import com.gradel.parent.tencent.qcloud.cmq.CMQClientException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class CMQConsumerBean implements Consumer {

    private final Map<String/* queue */, CMQMessageListener> subscribeQueue = new ConcurrentHashMap<>();

    private Properties properties;

    private Consumer consumer;

    @Override
    public void start() {
        consumer = CMQFactory.createConsumer(properties);
        subscribeQueue.forEach((k, v) -> consumer.subscribe(k, v));
        consumer.start();
    }

    @Override
    public void shutdown() {
        if (this.consumer != null) {
            this.consumer.shutdown();
        }
    }

    @Override
    public void subscribe(String queue, CMQMessageListener listener) {
        if (null == consumer) {
            throw new CMQClientException("Subscribe Queue must be called after RocketMqConsumerBean started");
        }
        consumer.subscribe(queue, listener);
    }

    @Override
    public void unsubscribe(String queue) {
        if (null == consumer) {
            throw new CMQClientException("Unsubscribe Queue must be called after RocketMqConsumerBean started");
        }
        consumer.unsubscribe(queue);
    }

    @Override
    public boolean isStarted() {
        return (consumer != null && consumer.isStarted());
    }

    @Override
    public boolean isClosed() {
        return consumer != null && consumer.isClosed();
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    /**
     * set for Map
     *
     * @param subscribeQueue
     */
    public void setSubscribeQueue(Map<String, CMQMessageListener> subscribeQueue) {
        if (subscribeQueue != null) {
            subscribeQueue.forEach((k, v) -> {
                addQueueListener(k, v);
            });
        }
    }

    /**
     * set for Map
     *
     * @param subscribeQueue
     */
    public void setSubscribeQueueName(Map<QueueName, CMQMessageListener> subscribeQueue) {
        if (subscribeQueue != null) {
            subscribeQueue.forEach((k, v) -> {
                addQueueListener(k.getCode(), v);
            });
        }
    }

    /**
     * set for List
     *
     * @param subscriptionList
     */
    public void setSubscriptionList(List<CMQMessageListener> subscriptionList) {
        if (subscriptionList != null) {
            initTopicTagListenerList(subscriptionList);
        }
    }

    private void initTopicTagListenerList(List<CMQMessageListener> subscriptionList) {
        if (subscriptionList != null) {
            subscriptionList.forEach(listener -> {
                if (listener.subscriQueue() != null) {
                    addQueueListener(listener.subscriQueue().getCode(), listener);
                }
            });
        }
    }

    private void addQueueListener(String queue, CMQMessageListener listener) {
        if (StringUtils.isBlank(queue)) {
            throw new CMQClientException("subscribe queue is null");
        }

        if (null == listener) {
            throw new CMQClientException("subscribe listener is null");
        }

        if (this.subscribeQueue.put(queue, listener) != null) {
            log.error("Repeat the subscription queue[{}], cconsumer:[{}]", queue, listener);
            throw new IllegalArgumentException("CMQConsumer[" + listener.getClass() + "] subscribe queue[" + queue + "] is exist!");
        }
    }

}
