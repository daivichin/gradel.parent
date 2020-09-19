package com.gradel.parent.ali.rocketmq.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @Description: 消息统一载体
 * @date 2016/5/9
 */
@ToString(callSuper = true)
@EqualsAndHashCode
@Data
public class MqMessageBody<T> implements MqBaseMessageBody<T>, Serializable {

    private static final long serialVersionUID = -6581653428040313373L;

    /**
     * 业务ID(rockmq 可以根据当前业务ID和主题查询消息) --注意：不设置也不会影响消息正常收发
     */
    private String businessId;

    /**
     * 消息体 不能为空----为了当出现异常时，方便输出重要日志信息，必须都实现toString方法（@ToString(callSuper = true)）
     */
    private T content;

    public MqMessageBody() {
    }

    public MqMessageBody(T content) {
//        notNull(content, "Mq Message content must not null!!!");
        this.content = content;
    }

    /**
     *
     * @param content 消息内容
     * @param businessId 业务ID(rockmq 可以根据当前业务ID和主题查询消息) --注意：不设置也不会影响消息正常收发
     */
    public MqMessageBody(T content, String businessId) {
//        notNull(content, "Mq Message content must not null!!!");
        this.content = content;
        this.businessId = businessId;
    }

    public static <T> MqMessageBody<T> getInstance(T content) {
//        notNull(content, "Mq Message content must not null!!!");
        return new MqMessageBody(content);
    }

    /**
     * 生成消息体
     * @param content 消息内容
     * @param businessId 业务ID(rockmq 可以根据当前业务ID和主题查询消息) --注意：不设置也不会影响消息正常收发
     */
    public static <T> MqMessageBody<T> getInstance(T content, String businessId) {
//        notNull(content, "Mq Message content must not null!!!");
        return new MqMessageBody(content, businessId);
    }
}
