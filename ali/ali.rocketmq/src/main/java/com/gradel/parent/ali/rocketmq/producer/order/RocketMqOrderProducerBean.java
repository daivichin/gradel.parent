package com.gradel.parent.ali.rocketmq.producer.order;

import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.exception.ONSClientException;
import com.aliyun.openservices.ons.api.order.OrderProducer;
import com.gradel.parent.ali.rocketmq.model.MqMessageBody;
import com.gradel.parent.ali.rocketmq.model.RockMqSendResult;
import com.gradel.parent.ali.rocketmq.model.order.RockMqOrderMessage;
import com.gradel.parent.ali.rocketmq.producer.RocketMqProducerAbstract;
import com.gradel.parent.ali.rocketmq.producer.order.api.RocketMqOrderProducer;
import com.gradel.parent.ali.rocketmq.serializer.MqSerializer;
import com.gradel.parent.common.util.util.ExceptionUtil;
import com.gradel.parent.common.util.threadlocal.SerialNo;
import lombok.extern.slf4j.Slf4j;

import java.util.Properties;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @date: 2016/11/24
 * @Description: 有序消息生产者接口
 *
 * @see OrderProducer
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
 记得要调用 start/shutdown
 */
@Slf4j
public class RocketMqOrderProducerBean extends RocketMqProducerAbstract implements RocketMqOrderProducer {

    private Properties properties;

    private OrderProducer orderProducer;

    @Override
    public void start() {
        if (null == this.properties) {
            throw new ONSClientException("properties not set");
        }

        super.init(properties);

        this.orderProducer = ONSFactory.createOrderProducer(this.properties);
        this.orderProducer.start();
    }

    @Override
    public void updateCredential(Properties credentialProperties) {
        if(this.orderProducer != null){
            this.orderProducer.updateCredential(credentialProperties);
        }
    }

    @Override
    public void shutdown() {
        if (this.orderProducer != null) {
            this.orderProducer.shutdown();
        }
    }

    /**
     * 同步发送消息
     *
     * @param message
     * @return 发送结果，true 表示发送成功，否则发送失败
     */
    @Override
    public <T> boolean send(RockMqOrderMessage<T> message, MqSerializer<T> serializer) {
        return sendBackResult(message, serializer).isSuccess();
    }

    /**
     * 同步发送消息
     *
     * @param message
     * @return 发送结果，true 表示发送成功，否则发送失败
     */
    @Override
    public <T> RockMqSendResult sendBackResult(final RockMqOrderMessage<T> message, final MqSerializer<T> serializer) {
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
            sendResult = orderProducer.send(message, message.getShardingKey());
        } catch (Throwable e) {
            Object[] params = {SerialNo.getSerialNo(), message.getTopic(), message.getTag(), message.getKey(), message.getContent(), message.getShardingKey(), (System.currentTimeMillis() - start), ExceptionUtil.getAsString(e)};
            log.error("[{}] Mq send order message Exception, topic:[{}], tag:[{}], key:[{}], message:[{}], shardingKey[{}], costTime:{}ms, Some Exception Occur:[{}]", params);
        }
        return RockMqSendResult.successIfNotNull(sendResult);
    }

    public Properties getProperties() {
        return properties;
    }


    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public boolean isStarted() {
        return this.orderProducer.isStarted();
    }

    @Override
    public boolean isClosed() {
        return this.orderProducer.isClosed();
    }
}

