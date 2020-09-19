package com.gradel.parent.ali.rocketmq.producer.api.transaction;

import com.aliyun.openservices.ons.api.Admin;
import com.gradel.parent.ali.rocketmq.model.RockMqSendResult;
import com.gradel.parent.ali.rocketmq.model.transaction.RockMqTransactionMessage;
import com.gradel.parent.ali.rocketmq.producer.api.RocketMqLocalTransactionExecuter;
import com.gradel.parent.ali.rocketmq.serializer.MqSerializer;

/**
 * 发送发布式事务消息
 * 事务消息的 Producer ID 不能与其他类型消息的 Producer ID 共用。与其他类型的消息不同，事务消息有回查机制，回查时MQ Server会根据Producer ID去查询客户端
 * @author: sdeven.chen.dongwei@gmail.com
 * @date 2019/01/02 下午2:49
 * @see com.aliyun.openservices.ons.api.transaction.TransactionProducer
 */
public interface RocketMqTransactionProducer extends Admin {
    /**
     * 该方法用来发送一条事务型消息. 一条事务型消息发送分为三个步骤:
     * <ol>
     *     <li>本服务实现类首先发送一条半消息到到消息服务器;</li>
     *     <li>通过<code>executer</code>执行本地事务;</li>
     *     <li>根据上一步骤执行结果, 决定发送提交或者回滚第一步发送的半消息;</li>
     * </ol>
     * @param message 要发送的事务型消息
     * @param executer 本地事务执行器
     * @param arg 应用自定义参数，该参数可以传入本地事务执行器
     * @param serializer 序列化
     * @return 发送结果.
     */
    <T> boolean send(final RockMqTransactionMessage<T> message,
                     final RocketMqLocalTransactionExecuter executer,
                     final Object arg, final MqSerializer<T> serializer);

    /**
     * 同上
     * @param message 要发送的事务型消息
     * @param executer 本地事务执行器
     * @param arg 应用自定义参数，该参数可以传入本地事务执行器
     * @param serializer 序列化
     * @return 发送结果.
     */
    <T> RockMqSendResult sendBackResult(final RockMqTransactionMessage<T> message,
                                        final RocketMqLocalTransactionExecuter executer,
                                        final Object arg, final MqSerializer<T> serializer);

    /**
     * 该方法用来发送一条事务型消息. 一条事务型消息发送分为三个步骤:
     * <ol>
     *     <li>本服务实现类首先发送一条半消息到到消息服务器;</li>
     *     <li>通过<code>executer</code>执行本地事务;</li>
     *     <li>根据上一步骤执行结果, 决定发送提交或者回滚第一步发送的半消息;</li>
     * </ol>
     * @param message 要发送的事务型消息
     * @param executer 本地事务执行器
     * @param serializer 序列化
     * @return 发送结果.
     */
    <T> boolean send(final RockMqTransactionMessage<T> message,
                     final RocketMqLocalTransactionExecuter executer, final MqSerializer<T> serializer);

    /**
     * 同上
     * @param message 要发送的事务型消息
     * @param executer 本地事务执行器
     * @param serializer 序列化
     * @return 发送结果.
     */
    <T> RockMqSendResult sendBackResult(final RockMqTransactionMessage<T> message,
                                        final RocketMqLocalTransactionExecuter executer, final MqSerializer<T> serializer);

}
