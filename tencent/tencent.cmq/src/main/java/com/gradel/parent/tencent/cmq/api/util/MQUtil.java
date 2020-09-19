package com.gradel.parent.tencent.cmq.api.util;

import com.gradel.parent.tencent.cmq.api.model.MessageContext;
import com.gradel.parent.tencent.qcloud.cmq.CMQClientException;
import com.gradel.parent.tencent.qcloud.cmq.Message;

public class MQUtil {

    /*public static QueueMessage msgConvert(Message message) {
        QueueMessage msgRMQ = new QueueMessage();
        if (message == null) {
            throw new CMQClientException("\'message\' is null");
        }

        if (message.msgId != null) {
            msgRMQ.setMsgId(message.msgId);
        }
        if (message.receiptHandle != null) {
            msgRMQ.setReceiptHandle(message.receiptHandle);
        }
        *//*if (message.msgBody != null) {
            msgRMQ.setMsgBody(message.msgBody);
        }*//*
        QueueMessageSetters.setMsgBody(msgRMQ, message.msgBody);

        msgRMQ.setEnqueueTime(message.enqueueTime);
        msgRMQ.setNextVisibleTime(message.nextVisibleTime);
        msgRMQ.setFirstDequeueTime(message.firstDequeueTime);
        msgRMQ.setDequeueCount(message.dequeueCount);

        return msgRMQ;
    }
*/
    public static MessageContext msgContentConvert(Message message) {
        MessageContext msgRMQ = new MessageContext();
        if (message == null) {
            throw new CMQClientException("\'message\' is null");
        }

        if (message.msgId != null) {
            msgRMQ.setMsgId(message.msgId);
        }
        if (message.receiptHandle != null) {
            msgRMQ.setReceiptHandle(message.receiptHandle);
        }
        msgRMQ.setEnqueueTime(message.enqueueTime);
        msgRMQ.setNextVisibleTime(message.nextVisibleTime);
        msgRMQ.setFirstDequeueTime(message.firstDequeueTime);
        msgRMQ.setDequeueCount(message.dequeueCount);

        return msgRMQ;
    }
}
