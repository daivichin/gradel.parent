package com.gradel.parent.tencent.cmq.api.impl;

import com.gradel.parent.tencent.cmq.api.Action;
import com.gradel.parent.tencent.cmq.api.CMQMessageListener;
import com.gradel.parent.tencent.cmq.api.model.MessageContext;
import com.gradel.parent.tencent.cmq.api.util.ClientLogger;
import com.gradel.parent.tencent.cmq.api.util.MQUtil;
import com.gradel.parent.tencent.qcloud.cmq.Message;
import com.gradel.parent.common.util.api.model.BaseMessage;
import com.gradel.parent.common.util.util.ExceptionUtil;
import lombok.Getter;
import org.slf4j.Logger;

public class DefaultConsumber {

    private final static Logger log = ClientLogger.getLog();

    @Getter
    private String queue;
    @Getter
    private CMQMessageListener cmqMessageListener;
    @Getter
    private CMQClientRemoteAPI cmqClientRemoteAPI;

    public DefaultConsumber(String queue, CMQMessageListener cmqMessageListener, CMQClientRemoteAPI cmqClientRemoteAPI) {
        this.queue = queue;
        this.cmqMessageListener = cmqMessageListener;
        this.cmqClientRemoteAPI = cmqClientRemoteAPI;
    }

    public Action consumeMessage(Message message) {
        MessageContext messageContext = null;
        BaseMessage msgBody = null;
        Object[] params = null;
        Action action = null;

        try {
            messageContext = MQUtil.msgContentConvert(message);
            messageContext.setQueue(queue);
            msgBody = cmqMessageListener.deserialize(message.msgBody);
        } catch (Throwable e) {
            params = new Object[]{messageContext.getMsgId(), queue, messageContext.getMsgId(), message.msgBody, messageContext.getDequeueCount(), ExceptionUtil.getAsString(e)};
            log.error("[{}]ReceivedMessage Deserialize exception, queue:{}, msgId:{}, msgBody:[{}], dequeueCount:{}, Exception:{}", params);
            return action;
        }

        try {
            //初始化线程上下文日志ID
            params = new Object[]{messageContext.getMsgId(), queue, messageContext.getMsgId(), messageContext.getDequeueCount(), message.msgBody};
            log.info("[{}]Received message: queue[{}], msgId:[{}], dequeueCount:[{}], msgBody:{}", params);

            action = cmqMessageListener.consume(msgBody, messageContext);
            /*if (action != null && action == Action.CommitMessage) {
                try {
                    JSONObject jsonObj = cmqClientRemoteAPI.deleteMessage(queue, messageContext.getReceiptHandle());
                    int code = jsonObj.getInt("code");
                    if (code != 0) {
                        params = new Object[]{messageContext.getMsgId(), queue, code, jsonObj.optString("message"), jsonObj.optString("requestId"), messageContext.getMsgId(), message.msgBody, messageContext.getDequeueCount()};
                        log.error("[{}]DeleteMessage failure, queue:{}, code:{}, errMsg:{}, requestId:{}, msgId:{}, msgBody:[{}], dequeueCount:{}", params);
                    }
                } catch (Exception e) {
                    params = new Object[]{messageContext.getMsgId(), queue, messageContext.getMsgId(), message.msgBody, messageContext.getDequeueCount(), ExceptionUtil.getAsString(e)};
                    log.error("[{}]DeleteMessage exception, queue:{}, msgId:{}, msgBody:[{}], dequeueCount:{}, Exception:{}", params);
                }
            }*/
        } catch (Throwable e) {
            params = new Object[]{messageContext.getMsgId(), cmqMessageListener.getClass().getSimpleName(), queue, messageContext.getMsgId(), message.msgBody, messageContext.getDequeueCount(), ExceptionUtil.getAsString(e)};
            log.error("[{}]Consume[{}] exception, queue:{}, msgId:{}, msgBody:[{}], dequeueCount:{}, Exception:{}", params);
        } finally {
        }
        return action;
    }
}
