package com.gradel.parent.ali.rocketmq.biz.consumer.order.api;

import com.aliyun.openservices.ons.api.order.OrderAction;
import com.gradel.parent.ali.rocketmq.biz.model.MqBaseMessageBody;
import com.gradel.parent.ali.rocketmq.biz.model.MqMessageBody;
import com.gradel.parent.ali.rocketmq.biz.model.order.OrderMessageContext;

/**
 * 有序消息消费接口
 * Created with IntelliJ IDEA.
 * @author: sdeven.chen.dongwei@gmail.com
 * @date: 2016/7/14
 */
@FunctionalInterface
public interface RocketMqOrderMessageListener {

    /**
     * 执行业务 返回Action.CommitMessage表示消费成功，或 抛异常也是表示消费成功（只有返回Action.ReconsumeLater才会告知服务器稍后再投递这条消息,消费失败）
     * @param record 消息记录
     * @param messageContext 消息上下文
     * @see MqMessageBody
     * @return Action
     */
    OrderAction call(MqBaseMessageBody record, OrderMessageContext messageContext) throws Exception;
}
