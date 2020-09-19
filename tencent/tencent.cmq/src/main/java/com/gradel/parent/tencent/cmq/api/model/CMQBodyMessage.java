package com.gradel.parent.tencent.cmq.api.model;

import lombok.ToString;

import java.io.Serializable;


/**
 * 消息类
 */
@ToString(callSuper = true)
public class CMQBodyMessage implements Serializable {
    /** 消息体 */
    private String msgBody;
    /**
     * 消息队列
     */
    private String queue;

    /** 延时消费 */
    private int delayTimeSeconds;

    protected CMQBodyMessage() {
    }

    protected CMQBodyMessage(String queue) {
        this.queue = queue;
    }

    protected CMQBodyMessage(String queue, String msgBody) {
        this.queue = queue;
        this.msgBody = msgBody;
    }

    public String getMsgBody() {
        return msgBody;
    }

    protected void setMsgBody(String msgBody) {
        this.msgBody = msgBody;
    }

    public String getQueue() {
        return queue;
    }

    protected void setQueue(String queue) {
        this.queue = queue;
    }

    public int getDelayTimeSeconds() {
        return delayTimeSeconds;
    }

    public void setDelayTimeSeconds(int delayTimeSeconds) {
        this.delayTimeSeconds = delayTimeSeconds;
    }
}
