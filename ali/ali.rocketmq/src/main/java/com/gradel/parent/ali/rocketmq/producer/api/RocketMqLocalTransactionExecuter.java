package com.gradel.parent.ali.rocketmq.producer.api;

import com.aliyun.openservices.ons.api.transaction.TransactionStatus;
import com.gradel.parent.ali.rocketmq.model.transaction.RockMqTransactionMessage;
import com.gradel.parent.ali.rocketmq.model.transaction.TransactionMessageContext;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @date 2019/01/02 下午4:43
 */
@FunctionalInterface
public interface RocketMqLocalTransactionExecuter<T> {

    /**
     * 执行本地事务，由应用来重写
     *
     * @param message 消息
     * @param arg 应用自定义参数，由send方法传入并回调
     * @param transactionMessageContext
     * @return {@link TransactionStatus} 返回事务执行结果，包括提交事务、回滚事务、未知状态
     */
    TransactionStatus execute(final RockMqTransactionMessage<T> message, final Object arg, TransactionMessageContext transactionMessageContext);
}
