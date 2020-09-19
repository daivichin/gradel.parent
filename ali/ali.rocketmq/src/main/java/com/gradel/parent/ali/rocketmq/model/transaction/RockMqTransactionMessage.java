package com.gradel.parent.ali.rocketmq.model.transaction;

import com.gradel.parent.ali.rocketmq.model.MqBaseMessageBody;
import com.gradel.parent.ali.rocketmq.model.RockMqMessage;
import com.gradel.parent.common.util.api.topic.MqTopic;
import lombok.ToString;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @Description: 事务消息
 *
 *
注意事项
事务消息的 Producer ID 不能与其他类型消息的 Producer ID 共用。与其他类型的消息不同，事务消息有回查机制，回查时MQ Server会根据Producer ID去查询客户端。

通过 ONSFactory.createTransactionProducer 创建事务消息的 Producer 时必须指定 LocalTransactionChecker 的实现类，处理异常情况下事务消息的回查。

事务消息发送完成本地事务后，可在 execute 方法中返回以下三种状态：

TransactionStatus.CommitTransaction 提交事务，允许订阅方消费该消息。
TransactionStatus.RollbackTransaction 回滚事务，消息将被丢弃不允许消费。
TransactionStatus.Unknow 暂时无法判断状态，期待固定时间以后 MQ Server 向发送方进行消息回查。
可通过以下方式给每条消息设定第一次消息回查的最快时间：

Message message = new Message();
// 在消息属性中添加第一次消息回查的最快时间，单位秒。例如，以下设置实际第一次回查时间为 120 ~ 125 秒之间
message.putUserProperties(PropertyKeyConst.CheckImmunityTimeInSeconds,"120");
// 以上方式只确定事务消息的第一次回查的最快时间，实际回查时


 @seeurl https://help.aliyun.com/document_detail/43348.html?spm=a2c4g.11186623.6.553.703f12d2ND6JZC
 */
@ToString(callSuper = true)
public class RockMqTransactionMessage<T> extends RockMqMessage<T> {

    public static int DEFAULT_CHECK_TIMEINSECONDS = 120;

    //设置实际第一次回查时间(事务回查)，实际回查时间向后浮动0~5秒；如第一次回查后事务仍未提交，后续每隔5秒回查一次。
    private int checkImmunityTimeInSeconds;

    public RockMqTransactionMessage() {
    }

    /**
     *
     * @param topic 主题
     * @param body 消息体
     * @param checkImmunityTimeInSeconds 设置实际第一次回查时间(事务回查)，实际回查时间向后浮动0~5秒；如第一次回查后事务仍未提交，后续每隔5秒回查一次。
     */
    public RockMqTransactionMessage(MqTopic topic, MqBaseMessageBody<T> body, int checkImmunityTimeInSeconds) {
        super(topic, body);
        this.checkImmunityTimeInSeconds = checkImmunityTimeInSeconds;
    }

    /**
     *
     * @param topic 主题
     * @param body 消息体
     */
    public RockMqTransactionMessage(MqTopic topic, MqBaseMessageBody<T>body) {
        this(topic, body, DEFAULT_CHECK_TIMEINSECONDS);
    }

    public int getCheckImmunityTimeInSeconds() {
        return checkImmunityTimeInSeconds;
    }

    public void setCheckImmunityTimeInSeconds(int checkImmunityTimeInSeconds) {
        this.checkImmunityTimeInSeconds = checkImmunityTimeInSeconds;
    }
}
