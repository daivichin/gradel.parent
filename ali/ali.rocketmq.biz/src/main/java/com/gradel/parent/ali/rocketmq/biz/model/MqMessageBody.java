package com.gradel.parent.ali.rocketmq.biz.model;

import lombok.ToString;

import java.io.Serializable;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @Description: 消息统一载体
 * @date 2016/5/9
 */
@ToString
public class MqMessageBody<T> implements MqBaseMessageBody<T>, Serializable {

    private static final long serialVersionUID = -6581653428040313373L;

    /**
     * 业务ID(rockmq 可以根据当前业务ID和主题查询消息) --注意：不设置也不会影响消息正常收发
     */
    private String businessId;

    /**
     * 消息体 不能为空
     */
    private T content;

    private MqMessageBody() {
    }

    private MqMessageBody(T content) {
        this.content = content;
    }

    /**
     *
     * @param content 消息内容
     * @param businessId 业务ID(rockmq 可以根据当前业务ID和主题查询消息) --注意：不设置也不会影响消息正常收发
     */
    private MqMessageBody(T content, String businessId) {
        this.content = content;
        this.businessId = businessId;
    }

    public static <T> MqMessageBody<T> getInstance(T content) {
        notNull(content, "Mq Message content must not null!!!");
        return new MqMessageBody(content);
    }

    /**
     * 生成消息体
     * @param content 消息内容
     * @param businessId 业务ID(rockmq 可以根据当前业务ID和主题查询消息) --注意：不设置也不会影响消息正常收发
     */
    public static <T> MqMessageBody<T> getInstance(T content, String businessId) {
        notNull(content, "Mq Message content must not null!!!");
        return new MqMessageBody(content, businessId);
    }

    @Override
    public T getContent() {
        return content;
    }

    private static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    @Override
    public String getBusinessId() {
        return businessId;
    }
}
