package com.gradel.parent.ali.rocketmq.consumer;

import com.aliyun.openservices.ons.api.*;
import com.aliyun.openservices.ons.api.exception.ONSClientException;
import com.gradel.parent.ali.rocketmq.consumer.api.RocketMqConsumer;
import com.gradel.parent.ali.rocketmq.consumer.api.RocketMqMessageListener;
import com.gradel.parent.ali.rocketmq.model.MessageContext;
import com.gradel.parent.ali.rocketmq.model.MqBaseMessageBody;
import com.gradel.parent.ali.rocketmq.util.MqContextUtil;
import com.gradel.parent.ali.rocketmq.util.MqUtil;
import com.gradel.parent.ali.rocketmq.topic.SubscibeTopic;
import com.gradel.parent.common.util.api.enums.AppName;
import com.gradel.parent.common.util.api.topic.MqTopic;
import com.gradel.parent.common.util.util.ExceptionUtil;
import com.gradel.parent.common.util.threadlocal.SerialNo;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static java.util.stream.Collectors.joining;

//import com.ml.common.util.SerializerUtil;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @date: 2016/11/24
 * @Description: 无序消息消费者，用来订阅消息
 * 记得要调用 start/shutdown
 *
 * @see com.aliyun.openservices.ons.api.bean.ConsumerBean
 */
@Slf4j
public class RocketMqConsumerBean implements RocketMqConsumer, ConsumerCheck {
    private Properties properties;
//    private Map<SubscibeTopic, RocketMqMessageListener> subscriptionTable;

    private Map<String, Map<String, RocketMqMessageListener>> topicTagListenerMap;//key: topic value: tag > listener
    private Consumer consumer;
    private String consumerId;

    @Override
    public void start() {
        if (null == this.properties) {
            throw new ONSClientException("properties field not set");
        }

        if (null == this.topicTagListenerMap) {
            throw new ONSClientException("topicTagListenerMap field not set");
        }

        consumerId = MqUtil.getConsumerId(this.properties);
        log.info("########## Start to create Consumer[consumerId={}]", consumerId);
        check(consumerId);
        this.consumer = ONSFactory.createConsumer(this.properties);

        topicTagListenerMap.forEach((key, value) -> {
            Map<String, RocketMqMessageListener> tagListenerMap = value;
            if (tagListenerMap.size() <= 0) {
                throw new ONSClientException("subscriptionTable[topic:" + key + "] listener not set");
            }
            if (tagListenerMap.size() == 1) {
                //该主题只有一个tag，so
                tagListenerMap.forEach((tagKey, rocketMqMessageListener) -> {
                    if (rocketMqMessageListener == null) {
                        throw new ONSClientException("subscriptionTable[topic:" + key + ", tag:" + tagKey + "] listener not set");
                    }
                    this.subscribe(key, tagKey, rocketMqMessageListener, null);
                });
            } else {
                //多个tag

                String moreTagStrs = tagListenerMap.entrySet().stream().map(entry -> entry.getKey()).collect(joining("||", "", ""));
                /*Iterator<Map.Entry<String, RocketMqMessageListener>> tagListenerIterator = tagListenerMap.entrySet().iterator();
                StringBuilder tagBuilder = new StringBuilder();
                while (tagListenerIterator.hasNext()) {
                    if (tagBuilder.length() > 0) {
                        tagBuilder.append("||");
                    }
                    tagBuilder.append(tagListenerIterator.next().getKey());
                }*/
                this.subscribe(key, moreTagStrs, null, tagListenerMap);
            }
        });

        this.consumer.start();

    }

    @Override
    public void updateCredential(Properties credentialProperties) {
        if (this.consumer != null) {
            this.consumer.updateCredential(credentialProperties);
        }
    }

    @Override
    public void shutdown() {
        if (this.consumer != null) {
            this.consumer.shutdown();
        }
    }

    /**
     * @param topic
     * @param subExpression
     * @param messageListener   为null，则表示当前消费者为top->tab(多)，一个主题有多个tab，否则一个主题一个tab 对应一个消费者
     * @param tagListenerMap    不为null，则表示当前消费者为top->tab(多)，一个主题有多个tab，否则一个主题一个tab 对应一个消费者
     */
    private void subscribe(final String topic, final String subExpression, final RocketMqMessageListener messageListener, final Map<String, RocketMqMessageListener> tagListenerMap) {
        if (null == this.consumer) {
            throw new ONSClientException("Subscribe must be called after consumerBean started");
        }
        log.info("########## consumerId[{}] Start to substribe topic[{}] tags[{}]", consumerId, topic, subExpression);
        this.consumer.subscribe(topic, subExpression, new MessageListener() {
            @Override
            public Action consume(Message message, ConsumeContext context) {
                RocketMqMessageListener listener = messageListener;
                if (tagListenerMap != null/* && listener == null*/) {
                    listener = tagListenerMap.get(message.getTag());
                }

                if (listener == null) {
                    Object[] params = new Object[]{message.getMsgID(), message.getTopic(), message.getTag(), message.getKey(), consumerId};
                    log.error("Mq consume messageListener has not found, msgId:[{}], topic[{}], tag[{}], key[{}], consumerId:[{}]", params);
                    return Action.ReconsumeLater;
                }


                MqBaseMessageBody record = null;
                MessageContext messageContext = MqContextUtil.getMessageContext(message);
                try {
                    //初始化线程上下文日志ID
                    SerialNo.init(AppName.ROCKETMQ_SC);
                    //反序列化对象
                    record = listener.deserialize(message.getBody(), messageContext);
//                    record = SerializerUtil.unserialize(message.getBody());
                    if (record == null) {
                        Object[] params = new Object[]{SerialNo.getSerialNo(), message.getMsgID(), messageContext.getUniqMsgId(), message.getTopic(), message.getTag(), message.getKey(), consumerId, message.getReconsumeTimes()};
                        log.error("[{}]Mq consume message Exception: record is null. msgId:[{}], uniqMsgId:[{}], topic[{}], tag[{}], key[{}], consumerId:[{}], retryTimes:[{}]", params);
//                        return Action.CommitMessage;
                    } else {
                        if (log.isDebugEnabled()) {
                            Object[] params = new Object[]{SerialNo.getSerialNo(), message.getMsgID(), messageContext.getUniqMsgId(), message.getTopic(), message.getTag(), message.getKey(), consumerId, message.getReconsumeTimes(), record};
                            log.debug("[{}] Mq Received message: msgId:[{}], uniqMsgId:[{}], topic[{}], tag[{}], key[{}], consumerId:[{}], retryTimes:[{}], record:[{}]", params);
                        } else {
                            Object[] params = new Object[]{SerialNo.getSerialNo(), message.getMsgID(), messageContext.getUniqMsgId(), message.getTopic(), message.getTag(), message.getKey(), message.getReconsumeTimes()};
                            log.info("[{}] Mq Received message: msgId:[{}], uniqMsgId:[{}], topic[{}], tag[{}], key[{}], retryTimes:[{}]", params);
                        }
                    }

                    //回调业务处理接口（默认返回消费成功）
                    return listener.call(record, messageContext);
                } catch (Throwable e) {
                    //记录日志
                    if (record != null) {
                        Object[] params = new Object[]{SerialNo.getSerialNo(), message.getMsgID(), messageContext.getUniqMsgId(), message.getTopic(), message.getTag(), message.getKey(), consumerId, message.getReconsumeTimes(), record, ExceptionUtil.getAsString(e)};
                        log.error("[{}]Mq consume message Exception: msgId:[{}], uniqMsgId:[{}], topic[{}], tag[{}], key[{}], consumerId:[{}], retryTimes:[{}], record:[{}], Exception:{}", params);
                    } else {
                        Object[] params = new Object[]{SerialNo.getSerialNo(), message.getMsgID(), messageContext.getUniqMsgId(), message.getTopic(), message.getTag(), message.getKey(), consumerId, message.getReconsumeTimes(), record, ExceptionUtil.getAsString(e)};
                        log.error("[{}]Mq consume message Exception: msgId:[{}], uniqMsgId:[{}], topic[{}], tag[{}], key[{}], consumerId:[{}], retryTimes:[{}], record is null, message:[{}], Exception:{}", params);
                    }
                    /*
                    只要抛出异常就会进入重试
                    重试时间与次数：https://help.aliyun.com/knowledge_detail/39127.html
                    消费业务逻辑代码如果返回Action.ReconsumerLater，或者NULL，或者抛出异常，消息都会走重试流程，至多重试16次，如果重试16次后，仍然失败，则消息丢弃。
                    */
                    return Action.ReconsumeLater;
                } finally {
                    //清空线程上下文日志ID
                    SerialNo.clear();
                }
                /*//不管有没有异常都返回消费成功
                return Action.CommitMessage;*/
            }
        });
    }

    @Override
    public <T> void subscribe(String topic, String subExpression, RocketMqMessageListener<T> listener) {
        if (listener == null) {
            throw new ONSClientException("subscriptionTable[topic:" + topic + ", tag:" + subExpression + "] listener must not null");
        }
        this.subscribe(topic, subExpression, listener, null);
    }

    @Override
    public void unsubscribe(String topic) {
        if (null == this.consumer) {
            throw new ONSClientException("unsubscribe must be called after consumerBean started");
        }
        this.consumer.unsubscribe(topic);
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
     * @param subscriptionTable
     */
    public void setSubscriptionTable(Map<SubscibeTopic, RocketMqMessageListener> subscriptionTable) {
        if (subscriptionTable != null) {
            topicTagListenerMap = initTopicTagListenerMap(subscriptionTable);
        }
    }

    /**
     * set for List
     *
     * @param subscriptionList
     */
    public void setSubscriptionList(List<RocketMqMessageListener> subscriptionList) {
        if (subscriptionList != null) {
            topicTagListenerMap = initTopicTagListenerList(subscriptionList);
        }
    }

    @Override
    public boolean isStarted() {
        return this.consumer.isStarted();
    }

    @Override
    public boolean isClosed() {
        return this.consumer.isClosed();
    }

    private static Map<String, Map<String, RocketMqMessageListener>> initTopicTagListenerMap(Map<SubscibeTopic, RocketMqMessageListener> subscriptionTable) {
        /*Map<String, Map<String, RocketMqMessageListener>> topicTagListenerMap = subscriptionTable.entrySet().stream()
                .collect(groupingBy(entryKey -> entryKey.getKey().getTopic(), toMap(entryKey -> entryKey.getKey().getExpression(), entryValue -> entryValue.getValue())));
        return topicTagListenerMap;*/
        Map<String, Map<String, RocketMqMessageListener>> topicTagListenerMap = new HashMap<>(subscriptionTable.size());
        Iterator<Map.Entry<SubscibeTopic, RocketMqMessageListener>> it = subscriptionTable.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<SubscibeTopic, RocketMqMessageListener> next = it.next();
            Map<String, RocketMqMessageListener> tagListenerMap = topicTagListenerMap.get(next.getKey().getTopic());
            if (tagListenerMap == null) {
                tagListenerMap = new HashMap<>(5);
                topicTagListenerMap.put(next.getKey().getTopic(), tagListenerMap);
            }
            tagListenerMap.put(next.getKey().getExpression(), next.getValue());
        }
        return topicTagListenerMap;
    }

    private Map<String, Map<String, RocketMqMessageListener>> initTopicTagListenerList(List<RocketMqMessageListener> subscriptionList) {
        Map<SubscibeTopic, RocketMqMessageListener> subscriptionTable = new HashMap<>();
        for (RocketMqMessageListener listener : subscriptionList) {
            MqTopic rockMqTopic = listener.subscriTopic();
            if (rockMqTopic == null) {
                throw new IllegalArgumentException("MqConsumer[" + listener.getClass() + "] subscribe topic must not null, must implement method[subscriTopic]");
            }
            RocketMqMessageListener ifAbsent = subscriptionTable.putIfAbsent(new SubscibeTopic(rockMqTopic), listener);
            if (ifAbsent != null) {
                throw new IllegalArgumentException("MqConsumer[" + listener.getClass() + "] subscribe topic[" + rockMqTopic.getCode() + "] tag[" + rockMqTopic.getTag() + "] is exist!");
            }
        }

        return initTopicTagListenerMap(subscriptionTable);
    }


}
