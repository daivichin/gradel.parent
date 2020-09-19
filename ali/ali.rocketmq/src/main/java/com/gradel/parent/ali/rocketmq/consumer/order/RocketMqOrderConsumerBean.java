package com.gradel.parent.ali.rocketmq.consumer.order;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.exception.ONSClientException;
import com.aliyun.openservices.ons.api.order.ConsumeOrderContext;
import com.aliyun.openservices.ons.api.order.MessageOrderListener;
import com.aliyun.openservices.ons.api.order.OrderAction;
import com.aliyun.openservices.ons.api.order.OrderConsumer;
import com.gradel.parent.ali.rocketmq.consumer.ConsumerCheck;
import com.gradel.parent.ali.rocketmq.consumer.order.api.RocketMqOrderConsumer;
import com.gradel.parent.ali.rocketmq.consumer.order.api.RocketMqOrderMessageListener;
import com.gradel.parent.ali.rocketmq.model.MqBaseMessageBody;
import com.gradel.parent.ali.rocketmq.model.MqMessageBody;
import com.gradel.parent.ali.rocketmq.model.order.OrderMessageContext;
import com.gradel.parent.ali.rocketmq.util.MqContextUtil;
import com.gradel.parent.ali.rocketmq.util.MqUtil;
import com.gradel.parent.ali.rocketmq.topic.SubscibeTopic;
import com.gradel.parent.common.util.api.enums.AppName;
import com.gradel.parent.common.util.api.topic.MqTopic;
import com.gradel.parent.common.util.util.ExceptionUtil;
import com.gradel.parent.common.util.threadlocal.SerialNo;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

//import com.ml.common.util.SerializerUtil;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @date: 2016/11/24
 * @Description: 有序消息消费者，用来订阅消息(顺序消费)
 *
 * @see OrderConsumer
 *
 *
 *  全局顺序：对于指定的一个 Topic，所有消息按照严格的先入先出（FIFO）的顺序进行发布和消费。
 *  MQ 全局顺序消息适用于以下场景：
        性能要求不高，所有的消息严格按照 FIFO 原则进行消息发布和消费的场景。
    MQ 分区顺序消息适用于如下场景：
        性能要求高，以 sharding key 作为分区字段，在同一个区块中严格的按照 FIFO 原则进行消息发布和消费的场景。
        举例说明：
        【例一】
        用户注册需要发送发验证码，以用户 ID 作为 sharding key， 那么同一个用户发送的消息都会按照先后顺序来发布和订阅。
        【例二】
        电商的订单创建，以订单 ID 作为 sharding key，那么同一个订单相关的创建订单消息、订单支付消息、订单退款消息、订单物流消息都会按照先后顺序来发布和订阅。
        阿里巴巴集团内部电商系统均使用此种分区顺序消息，既保证业务的顺序，同时又能保证业务的高性能。


消息类型对比

Topic类型	支持事务消息	支持定时消息	性能
无序消息	       是	       是	   最高
分区顺序	       否	       否	   高
全局顺序	       否	       否	   一般

发送方式对比

消息类型	支持可靠同步发送	支持可靠异步发送	支持 oneway 发送
无序消息	是	           是	            是
分区顺序	是	           否	            否
全局顺序	是	           否	            否

 @seeurl https://help.aliyun.com/document_detail/49319.html?spm=5176.doc43349.6.561.zH2zYW
 @see MqMessageBody
 */
@Slf4j
public class RocketMqOrderConsumerBean implements RocketMqOrderConsumer, ConsumerCheck {
    private Properties properties;
//    private Map<SubscibeTopic, RocketMqOrderMessageListener> subscriptionTable;

    private Map<String, Map<String, RocketMqOrderMessageListener>> topicTagListenerMap;//key: topic value: tag > listener
    private OrderConsumer orderConsumer;
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
        log.info("########## Start to create Ordered Consumer[consumerId={}]", consumerId);
        check(consumerId);
        this.orderConsumer = ONSFactory.createOrderedConsumer(this.properties);

        Iterator<Map.Entry<String, Map<String, RocketMqOrderMessageListener>>> it = this.topicTagListenerMap.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<String, Map<String, RocketMqOrderMessageListener>> entry = it.next();
            Map<String, RocketMqOrderMessageListener> tagListenerMap = entry.getValue();
            if (tagListenerMap.size() <= 0) {
                throw new ONSClientException("subscriptionTable[topic:" + entry.getKey() + "] listener not set");
            }
            if (tagListenerMap.size() == 1) {
                //该主题只有一个tag，so
                tagListenerMap.keySet().forEach(tag -> {
                    RocketMqOrderMessageListener rocketMqMessageListener = tagListenerMap.get(tag);
                    if(rocketMqMessageListener == null){
                        throw new ONSClientException("subscriptionTable[topic:" + entry.getKey() + ", tag:" + tag + "] listener not set");
                    }
                    this.subscribe(entry.getKey(), tag, rocketMqMessageListener, null);
                    return;
                });
            } else {
                //多个tag
                Iterator<Map.Entry<String, RocketMqOrderMessageListener>> tagListenerIterator = tagListenerMap.entrySet().iterator();
                StringBuilder tagBuilder = new StringBuilder();
                while (tagListenerIterator.hasNext()) {
                    if (tagBuilder.length() > 0) {
                        tagBuilder.append("||");
                    }
                    tagBuilder.append(tagListenerIterator.next().getKey());
                }
                /*this.subscribe(entry.getKey(), tagBuilder.toString(), (record, messageContext) -> {
                    RocketMqOrderMessageListener mqMessageListener = tagListenerMap.get(messageContext.getTag());
                    if (mqMessageListener == null) {
                        Object[] params = new Object[]{SerialNo.getSerialNo(), messageContext.getMsgID(), messageContext.getTopic(), messageContext.getTag(), messageContext.getKey(), properties.get(PropertyKeyConst.ConsumerId), record};
                        log.error("[{}]Mq consume messageListener has not found, msgId:[{}], topic[{}], tag[{}], key[{}], consumerId:[{}], record is null, message:[{}], Exception:{}", params);
                        return OrderAction.Suspend;
                    }
                    return mqMessageListener.call(record, messageContext);
                });*/

                this.subscribe(entry.getKey(), tagBuilder.toString(), null, tagListenerMap);
            }
        }

        this.orderConsumer.start();
    }

    @Override
    public void updateCredential(Properties credentialProperties) {
        if( this.orderConsumer != null){
            this.orderConsumer.updateCredential(credentialProperties);
        }
    }

    @Override
    public void shutdown() {
        if (this.orderConsumer != null) {
            this.orderConsumer.shutdown();
        }
    }

    /**
     *
     * @param topic
     * @param subExpression
     * @param messageListener   为null，则表示当前消费者为top->tab(多)，一个主题有多个tab，否则一个主题一个tab 对应一个消费者
     * @param tagListenerMap    不为null，则表示当前消费者为top->tab(多)，一个主题有多个tab，否则一个主题一个tab 对应一个消费者
     */
    private void subscribe(final String topic, final String subExpression, final RocketMqOrderMessageListener messageListener, final Map<String, RocketMqOrderMessageListener> tagListenerMap) {
        if (null == this.orderConsumer) {
            throw new ONSClientException("Subscribe must be called after orderConsumerBean started");
        }
        log.info("########## consumerId[{}] Start to substribe topic[{}] tags[{}]", consumerId, topic, subExpression);
        this.orderConsumer.subscribe(topic, subExpression, new MessageOrderListener() {
            @Override
            public OrderAction consume(Message message, ConsumeOrderContext context) {

                RocketMqOrderMessageListener listener = messageListener;
                if(tagListenerMap != null/* && listener == null*/){
                    listener = tagListenerMap.get(message.getTag());
                }

                if (listener == null) {
                    Object[] params = new Object[]{message.getMsgID(), message.getTopic(), message.getTag(), message.getKey(), consumerId};
                    log.error("Mq order consume[has been suspended] messageListener has not found, msgId:[{}], topic[{}], tag[{}], key[{}], consumerId:[{}]", params);
                    return OrderAction.Suspend;
                }

                MqBaseMessageBody record = null;
                OrderMessageContext orderMessageContext = MqContextUtil.getOrderMessageContext(message);
                try {
                    //初始化线程上下文日志ID
                    SerialNo.init(AppName.ROCKETMQ_SC);

                    //反序列化对象
                    record = listener.deserialize(message.getBody(), orderMessageContext);
//                    record = SerializerUtil.unserialize(message.getBody());
                    if (record == null) {
                        Object[] params = new Object[]{SerialNo.getSerialNo(), message.getMsgID(), orderMessageContext.getUniqMsgId(), message.getTopic(), message.getTag(), message.getKey(), consumerId, message.getReconsumeTimes()};
                        log.error("[{}]Mq order consume message Exception: record is null. msgId:[{}], uniqMsgId:[{}], topic[{}], tag[{}], key[{}], consumerId:[{}], retryTimes:[{}]", params);
//                        return OrderAction.Success;
                    } else {
                        if (log.isDebugEnabled()) {
                            Object[] params = new Object[]{SerialNo.getSerialNo(), message.getMsgID(), orderMessageContext.getUniqMsgId(), message.getTopic(), message.getTag(), message.getKey(), consumerId, message.getReconsumeTimes(), record};
                            log.debug("[{}]Mq Received message: msgId:[{}], uniqMsgId:[{}], topic[{}], tag[{}], key[{}], consumerId:[{}], retryTimes:[{}], record:[{}]", params);
                        } else {
                            Object[] params = new Object[]{SerialNo.getSerialNo(), message.getMsgID(), orderMessageContext.getUniqMsgId(), message.getTopic(), message.getTag(), message.getKey(), message.getReconsumeTimes()};
                            log.info("[{}]Mq Received message: msgId:[{}], uniqMsgId:[{}], topic[{}], tag[{}], key[{}], retryTimes:[{}]", params);
                        }
                    }

                    //回调业务处理接口（默认返回消费成功）
                    return listener.call(record, orderMessageContext);
                } catch (Throwable e) {
                    //记录日志
                    if (record != null) {
                        Object[] params = new Object[]{SerialNo.getSerialNo(), message.getMsgID(), orderMessageContext.getUniqMsgId(), message.getTopic(), message.getTag(), message.getKey(), consumerId, message.getReconsumeTimes(), record, ExceptionUtil.getAsString(e)};
                        log.error("[{}]Mq order consume[has been suspended] message Exception: msgId:[{}], uniqMsgId:[{}], topic[{}], tag[{}], key[{}], consumerId:[{}], retryTimes:[{}], record:[{}], Exception:{}", params);
                    } else {
                        Object[] params = new Object[]{SerialNo.getSerialNo(), message.getMsgID(), orderMessageContext.getUniqMsgId(), message.getTopic(), message.getTag(), message.getKey(), consumerId, message.getReconsumeTimes(), record, ExceptionUtil.getAsString(e)};
                        log.error("[{}]Mq order consume[has been suspended] message Exception: msgId:[{}], uniqMsgId:[{}], topic[{}], tag[{}], key[{}], consumerId:[{}], retryTimes:[{}], record is null, message:[{}], Exception:{}", params);
                    }
                    /*
                    消费失败，挂起当前队列
                    */
                    return OrderAction.Suspend;
                } finally {
                    //清空线程上下文日志ID
                    SerialNo.clear();
                }
            }
        });
    }

    @Override
    public void subscribe(String topic, String subExpression, RocketMqOrderMessageListener listener) {
        if (listener == null) {
            throw new ONSClientException("subscriptionTable[topic:" + topic + ", tag:" + subExpression + "] listener must not null");
        }
        this.subscribe(topic, subExpression, listener, null);
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    /**
     * set for Map
     * @param subscriptionTable
     */
    public void setSubscriptionTable(Map<SubscibeTopic, RocketMqOrderMessageListener> subscriptionTable) {
        if (subscriptionTable != null) {
            topicTagListenerMap = initTopicTagListenerMap(subscriptionTable);
        }
    }

    /**
     * set for List
     * @param subscriptionList
     */
    public void setSubscriptionList(List<RocketMqOrderMessageListener> subscriptionList) {
        if (subscriptionList != null) {
            topicTagListenerMap = initTopicTagListenerList(subscriptionList);
        }
    }

    @Override
    public boolean isStarted() {
        return this.orderConsumer.isStarted();
    }

    @Override
    public boolean isClosed() {
        return this.orderConsumer.isClosed();
    }

    private Map<String, Map<String, RocketMqOrderMessageListener>> initTopicTagListenerMap(Map<SubscibeTopic, RocketMqOrderMessageListener> subscriptionTable) {
        Map<String, Map<String, RocketMqOrderMessageListener>> topicTagListenerMap = new HashMap<>(subscriptionTable.size());
        Iterator<Map.Entry<SubscibeTopic, RocketMqOrderMessageListener>> it = subscriptionTable.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<SubscibeTopic, RocketMqOrderMessageListener> next = it.next();
            Map<String, RocketMqOrderMessageListener> tagListenerMap = topicTagListenerMap.get(next.getKey().getTopic());
            if (tagListenerMap == null) {
                tagListenerMap = new HashMap<>(5);
                topicTagListenerMap.put(next.getKey().getTopic(), tagListenerMap);
            }
            tagListenerMap.put(next.getKey().getExpression(), next.getValue());
        }
        return topicTagListenerMap;
    }

    private Map<String, Map<String, RocketMqOrderMessageListener>> initTopicTagListenerList(List<RocketMqOrderMessageListener> subscriptionList) {
        Map<SubscibeTopic, RocketMqOrderMessageListener> subscriptionTable = new HashMap<>();
        for (RocketMqOrderMessageListener listener : subscriptionList) {
            MqTopic rockMqTopic = listener.subscriTopic();
            if (rockMqTopic == null) {
                throw new IllegalArgumentException("MqOrderConsumer[" + listener.getClass() + "] subscribe topic must not null, must implement method[subscriTopic]");
            }
            RocketMqOrderMessageListener ifAbsent = subscriptionTable.putIfAbsent(new SubscibeTopic(rockMqTopic), listener);
            if (ifAbsent != null) {
                throw new IllegalArgumentException("MqOrderConsumer[" + listener.getClass() + "] subscribe topic[" + rockMqTopic.getCode() + "] tag[" + rockMqTopic.getTag() + "] is exist!");
            }
        }

        return initTopicTagListenerMap(subscriptionTable);
    }
}
