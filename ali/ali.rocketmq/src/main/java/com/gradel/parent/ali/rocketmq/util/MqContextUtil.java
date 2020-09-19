package com.gradel.parent.ali.rocketmq.util;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageAccessor;
import com.gradel.parent.ali.rocketmq.model.MessageContext;
import com.gradel.parent.ali.rocketmq.model.order.OrderMessageContext;
import com.gradel.parent.ali.rocketmq.model.transaction.TransactionMessageContext;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @date 2019/01/30 下午3:50
 */
public class MqContextUtil {

    public static MessageContext getMessageContext(Message message){
        MessageContext messageContext = new MessageContext(message.getTopic(), message.getUserProperties(), MessageAccessor.getSystemProperties(message)
                /*message.getTag(), message.getMsgID(), message.getKey(), message.getReconsumeTimes(),
                message.getBornTimestamp(), message.getBornHost(), message.getShardingKey()*/);
        return messageContext;
    }

    public static OrderMessageContext getOrderMessageContext(Message message){
        OrderMessageContext messageContext = new OrderMessageContext(message.getTopic(), message.getUserProperties(), MessageAccessor.getSystemProperties(message)
                /*message.getTag(), message.getMsgID(), message.getKey(), message.getReconsumeTimes(),
                message.getBornTimestamp(), message.getBornHost(), message.getShardingKey()*/);
        return messageContext;
    }

    public static TransactionMessageContext getTransactionMessageContext(Message message){
        TransactionMessageContext transactionMessageContext = new TransactionMessageContext(message.getTopic(), message.getUserProperties(), MessageAccessor.getSystemProperties(message)
                /*message.getTag(), message.getMsgID(), message.getKey(), message.getReconsumeTimes(), message.getBornTimestamp(), message.getBornHost(), message.getShardingKey()*/);
        return transactionMessageContext;
    }
}
