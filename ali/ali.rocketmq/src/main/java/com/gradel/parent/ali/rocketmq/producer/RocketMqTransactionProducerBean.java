package com.gradel.parent.ali.rocketmq.producer;

import com.aliyun.openservices.ons.api.*;
import com.aliyun.openservices.ons.api.exception.ONSClientException;
import com.aliyun.openservices.ons.api.transaction.LocalTransactionChecker;
import com.aliyun.openservices.ons.api.transaction.LocalTransactionExecuter;
import com.aliyun.openservices.ons.api.transaction.TransactionProducer;
import com.aliyun.openservices.ons.api.transaction.TransactionStatus;
import com.gradel.parent.ali.rocketmq.model.MessageContext;
import com.gradel.parent.ali.rocketmq.model.MqBaseMessageBody;
import com.gradel.parent.ali.rocketmq.model.RockMqSendResult;
import com.gradel.parent.ali.rocketmq.model.transaction.RockMqTransactionMessage;
import com.gradel.parent.ali.rocketmq.model.transaction.TransactionMessageContext;
import com.gradel.parent.ali.rocketmq.producer.api.RocketMqLocalTransactionExecuter;
import com.gradel.parent.ali.rocketmq.producer.api.RocketMqTransactionMessageChecker;
import com.gradel.parent.ali.rocketmq.producer.api.transaction.RocketMqTransactionProducer;
import com.gradel.parent.ali.rocketmq.serializer.MqSerializer;
import com.gradel.parent.ali.rocketmq.util.MqContextUtil;
import com.gradel.parent.ali.rocketmq.util.TransactionMessageContextHolder;
import com.gradel.parent.common.util.api.enums.AppName;
import com.gradel.parent.common.util.constants.CommonConstants;
import com.gradel.parent.common.util.util.ExceptionUtil;
import com.gradel.parent.common.util.threadlocal.SerialNo;
import lombok.extern.slf4j.Slf4j;

import java.util.Properties;

// 消息 ID（有可能消息体一样，但消息 ID 不一样，当前消息 ID 在控制台无法查询）
//                    String msgId = msg.getMsgID();
// 消息体内容进行 crc32，也可以使用其它的如 MD5
//                    long crc32Id = CRC32Util.crc32Code(msg.getBody());
// 消息 ID 和 crc32id 主要是用来防止消息重复
// 如果业务本身是幂等的，可以忽略，否则需要利用 msgId 或 crc32Id 来做幂等
// 如果要求消息绝对不重复，推荐做法是对消息体 body 使用 crc32或 md5来防止重复消息
/**
 * 事务消息发送者
 *
 * @author: sdeven.chen.dongwei@gmail.com
 * @date 2019/01/02 下午3:04
 *
 * see https://help.aliyun.com/document_detail/29548.html?spm=a2c4g.11186623.6.574.62181223nfubI8
 * 记得要调用 start/shutdown
 */
@Slf4j
public class RocketMqTransactionProducerBean extends RocketMqProducerAbstract implements RocketMqTransactionProducer {

    private Properties properties;

    private RocketMqTransactionMessageChecker rocketMqTransactionMessageChecker;

    private TransactionProducer transactionproducer;

    @Override
    public void start() {
        if (null == this.properties) {
            throw new ONSClientException("properties not set");
        }
        if (null == this.rocketMqTransactionMessageChecker) {
            throw new ONSClientException("Transaction MQ localTransactionChecker must not null");
        }

        super.init(properties);

        this.transactionproducer = ONSFactory.createTransactionProducer(this.properties, getLocalTransactionChecker(this.rocketMqTransactionMessageChecker));
        this.transactionproducer.start();
    }

    @Override
    public void updateCredential(Properties credentialProperties) {
        if (this.transactionproducer != null) {
            this.transactionproducer.updateCredential(credentialProperties);
        }
    }

    @Override
    public void shutdown() {
        if (this.transactionproducer != null) {
            this.transactionproducer.shutdown();
        }
    }

    /**
     * 该方法用来发送一条事务型消息. 一条事务型消息发送分为三个步骤:
     * <ol>
     * <li>本服务实现类首先发送一条半消息到到消息服务器;</li>
     * <li>通过<code>executer</code>执行本地事务;</li>
     * <li>根据上一步骤执行结果, 决定发送提交或者回滚第一步发送的半消息;</li>
     * </ol>
     *
     * @param message  要发送的事务型消息
     * @param executer 本地事务执行器
     * @param arg      应用自定义参数，该参数可以传入本地事务执行器
     * @param message
     * @return 发送结果，true 表示发送成功，否则发送失败
     * @see Message
     */
    @Override
    public <T> boolean send(RockMqTransactionMessage<T> message, RocketMqLocalTransactionExecuter executer, Object arg, MqSerializer<T> serializer) {
        return sendBackResult(message, executer, arg, serializer).isSuccess();
    }

    /**
     * 该方法用来发送一条事务型消息. 一条事务型消息发送分为三个步骤:
     * <ol>
     * <li>本服务实现类首先发送一条半消息到到消息服务器;</li>
     * <li>通过<code>executer</code>执行本地事务;</li>
     * <li>根据上一步骤执行结果, 决定发送提交或者回滚第一步发送的半消息;</li>
     * </ol>
     *
     * @param message  要发送的事务型消息
     * @param executer 本地事务执行器
     * @param arg      应用自定义参数，该参数可以传入本地事务执行器
     * @param message
     * @return 发送结果，true 表示发送成功，否则发送失败
     * @see Message
     */
    @Override
    public <T> RockMqSendResult sendBackResult(final RockMqTransactionMessage<T> message, RocketMqLocalTransactionExecuter executer, Object arg, final MqSerializer<T> serializer) {
        if (message.getContent() == null) {
            Object[] params = {SerialNo.getSerialNo(), message.getTopic(), message.getTag(), message.getKey()};
            log.error("[{}] Transaction Mq send Failure, Because Message content is null , topic:[{}], tag:[{}], key:[{}]", params);
            return RockMqSendResult.fail();
        }

        // 在消息属性中添加第一次消息回查的最快时间，单位秒
        message.putUserProperties(PropertyKeyConst.CheckImmunityTimeInSeconds, Integer.toString(message.getCheckImmunityTimeInSeconds()));
        // 以上方式只确定事务消息的第一次回查的最快时间，实际回查时间向后浮动0~5秒；如第一次回查后事务仍未提交，后续每隔5秒回查一次。

        SendResult sendResult = null;
        long start = System.currentTimeMillis();
        try {
            byte[] bytes = serializer.serialize(message.getContent());
            message.setBody(bytes);

            if(!checkBeforeSendMsg(message)){
                return RockMqSendResult.fail();
            }
            sendResult = transactionproducer.send(message, new LocalTransactionExecuter() {
                @Override
                public TransactionStatus execute(com.aliyun.openservices.ons.api.Message msg, Object arg) {
                    TransactionMessageContext transactionMessageContext = MqContextUtil.getTransactionMessageContext(msg);
                    // 消息 ID（有可能消息体一样，但消息 ID 不一样，当前消息 ID 在控制台无法查询）
//                    String msgId = msg.getMsgID();
                    // 消息体内容进行 crc32，也可以使用其它的如 MD5
//                    long crc32Id = CRC32Util.crc32Code(msg.getBody());
                    // 消息 ID 和 crc32id 主要是用来防止消息重复
                    // 如果业务本身是幂等的，可以忽略，否则需要利用 msgId 或 crc32Id 来做幂等
                    // 如果要求消息绝对不重复，推荐做法是对消息体 body 使用 crc32或 md5来防止重复消息

//                    return executer.execute((RockMqMessage)msg, arg);
                    TransactionStatus transactionStatus = TransactionStatus.Unknow;
                    try {
                        TransactionMessageContextHolder.setTransactionMessageContext(transactionMessageContext);
                        transactionStatus = executer.execute(message, arg, transactionMessageContext);
                    } finally {
                        TransactionMessageContextHolder.clearTransactionMessageContext();
                    }
                    return transactionStatus;

                }
            }, arg);
        } catch (Throwable e) {
            Object[] params = {SerialNo.getSerialNo(), message.getTopic(), message.getTag(), message.getKey(), message.getContent(), (System.currentTimeMillis() - start), ExceptionUtil.getAsString(e)};
            log.error("[{}] Transaction Mq send Exception, topic:[{}], tag:[{}], key:[{}], message:[{}], costTime:{}ms, Some Exception Occur:[{}]", params);
        }
        return RockMqSendResult.successIfNotNull(sendResult);
    }

    @Override
    public <T> boolean send(RockMqTransactionMessage<T> message, RocketMqLocalTransactionExecuter executer, MqSerializer<T> serializer) {
        return sendBackResult(message, executer, serializer).isSuccess();
    }

    @Override
    public <T> RockMqSendResult sendBackResult(RockMqTransactionMessage<T> message, RocketMqLocalTransactionExecuter executer, MqSerializer<T> serializer) {
        return sendBackResult(message, executer, CommonConstants.EMPTY_OBJECT, serializer);
    }

    public Properties getProperties() {
        return properties;
    }

    public RocketMqTransactionMessageChecker getRocketMqTransactionMessageChecker() {
        return rocketMqTransactionMessageChecker;
    }

    public void setRocketMqTransactionMessageChecker(RocketMqTransactionMessageChecker rocketMqTransactionMessageChecker) {
        this.rocketMqTransactionMessageChecker = rocketMqTransactionMessageChecker;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public boolean isStarted() {
        return this.transactionproducer.isStarted();
    }

    @Override
    public boolean isClosed() {
        return this.transactionproducer.isClosed();
    }

    private LocalTransactionChecker getLocalTransactionChecker(final RocketMqTransactionMessageChecker checker) {
        return new LocalTransactionChecker() {
            @Override
            public TransactionStatus check(com.aliyun.openservices.ons.api.Message message) {

                MqBaseMessageBody record = null;
                try {
                    //初始化线程上下文日志ID
                    SerialNo.init(AppName.ROCKETMQ_SC);

                    MessageContext messageContext = MqContextUtil.getMessageContext(message);
                    //反序列化对象
                    record = checker.deserialize(message.getBody(), messageContext);
//                    record = SerializerUtil.unserialize(message.getBody());
                    if (record == null) {
                        Object[] params = new Object[]{SerialNo.getSerialNo(), message.getMsgID(), message.getTopic(), message.getTag(), message.getKey(), properties.get(PropertyKeyConst.GROUP_ID), message.getReconsumeTimes()};
                        log.error("[{}]Transaction Mq consume message Exception: record is null. msgId:[{}], topic[{}], tag[{}], key[{}], consumerId:[{}], retryTimes:[{}]", params);
//                        return Action.CommitMessage;
                    } else {
                        if (log.isDebugEnabled()) {
                            Object[] params = new Object[]{SerialNo.getSerialNo(), message.getMsgID(), message.getTopic(), message.getTag(), message.getKey(), properties.get(PropertyKeyConst.GROUP_ID), message.getReconsumeTimes(), record};
                            log.debug("[{}]Transaction Mq Received message: msgId:[{}], topic[{}], tag[{}], key[{}], consumerId:[{}], retryTimes:[{}], record:[{}]", params);
                        } else {
                            Object[] params = new Object[]{SerialNo.getSerialNo(), message.getMsgID(), message.getTopic(), message.getTag(), message.getKey(), message.getReconsumeTimes()};
                            log.info("[{}]Transaction Mq Received message: msgId:[{}], topic[{}], tag[{}], key[{}], retryTimes:[{}]", params);
                        }
                    }

                    //回调业务处理接口（默认返回消费成功）
                    return checker.check(record, messageContext);
                } catch (Throwable e) {
                    //记录日志
                    if (record != null) {
                        Object[] params = new Object[]{SerialNo.getSerialNo(), message.getMsgID(), message.getTopic(), message.getTag(), message.getKey(), properties.get(PropertyKeyConst.GROUP_ID), message.getReconsumeTimes(), record, ExceptionUtil.getAsString(e)};
                        log.error("[{}]Transaction Mq consume message Exception: msgId:[{}], topic[{}], tag[{}], key[{}], consumerId:[{}], retryTimes:[{}], record:[{}], Exception:{}", params);
                    } else {
                        Object[] params = new Object[]{SerialNo.getSerialNo(), message.getMsgID(), message.getTopic(), message.getTag(), message.getKey(), properties.get(PropertyKeyConst.GROUP_ID), message.getReconsumeTimes(), record, ExceptionUtil.getAsString(e)};
                        log.error("[{}]Transaction Mq consume message Exception: msgId:[{}], topic[{}], tag[{}], key[{}], consumerId:[{}], retryTimes:[{}], record is null, message:[{}], Exception:{}", params);
                    }

                    //TransactionStatus.Unknow 暂时无法判断状态，期待固定时间以后 MQ Server 向发送方进行消息回查。
                    //实际回查时间向后浮动0~5秒；如第一次回查后事务仍未提交，后续每隔5秒回查一次。
                    return TransactionStatus.Unknow;
                } finally {
                    //清空线程上下文日志ID
                    SerialNo.clear();
                }
            }
        };
    }
}
