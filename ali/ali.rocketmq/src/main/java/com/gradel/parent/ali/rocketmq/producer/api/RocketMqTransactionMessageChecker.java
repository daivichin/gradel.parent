package com.gradel.parent.ali.rocketmq.producer.api;

import com.aliyun.openservices.ons.api.transaction.TransactionStatus;
import com.gradel.parent.ali.rocketmq.model.MessageContext;
import com.gradel.parent.ali.rocketmq.model.MqBaseMessageBody;
import com.gradel.parent.ali.rocketmq.model.MqMessageBody;
import com.gradel.parent.ali.rocketmq.serializer.MqDeserializer;

/**
 * 无序消息消费接口
 * Created with IntelliJ IDEA.
 * @author: sdeven.chen.dongwei@gmail.com
 * @date: 2016/7/14
 */
public interface RocketMqTransactionMessageChecker<T> extends MqDeserializer<T> {

    /**
     *  执行业务检查, 事务消息的第一次回查的最快时间，实际回查时间向后浮动0~5秒；如第一次回查后事务仍未提交，后续每隔5秒回查一次。
     *  CommitTransaction 	提交事务，对应的事务消息将投递给消费者
     *  RollbackTransaction 回滚事务，对应的事务消息会被删除，不会投递给消费者
     *  Unknow				未知状态，一般在用户无法确定事务是成功还是失败时使用，对于未知状态的事务，服务端会定期进行事务回查
     *
     * @param record 消息记录
     * @param messageContext 消息上下文
     * @see MqMessageBody
     * @return Action
     */
    TransactionStatus check(MqBaseMessageBody<T> record, MessageContext messageContext) throws Exception;
}
