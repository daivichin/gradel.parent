package com.gradel.parent.tencent.cmq.api.model;

import lombok.Data;
import lombok.ToString;

import java.util.Vector;

/**
 * 每次消费消息的上下文，供将来扩展使用
 */
@ToString
@Data
public class MessageContext {

    /**
     * 消息队列
     */
    private String queue;

    /**
     * 服务器返回的消息ID
     */
    private String msgId;
    /**
     * 每次消费唯一的消息句柄，用于删除等操作
     */
    private String receiptHandle;
    /**
     * 消息发送到队列的时间，从 1970年1月1日 00:00:00 000 开始的毫秒数
     */
    private long enqueueTime;
    /**
     * 消息下次可见的时间，从 1970年1月1日 00:00:00 000 开始的毫秒数
     */
    private long nextVisibleTime;
    /**
     * 消息第一次出队列的时间，从 1970年1月1日 00:00:00 000 开始的毫秒数
     */
    private long firstDequeueTime;
    /**
     * 出队列次数
     */
    private int dequeueCount;
    private Vector<String> msgTag;

}
