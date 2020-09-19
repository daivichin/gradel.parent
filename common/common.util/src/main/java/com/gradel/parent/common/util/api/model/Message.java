package com.gradel.parent.common.util.api.model;

import lombok.ToString;

import java.io.Serializable;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @Description: 消息统一载体
 * @date 2016/5/9
 */
@ToString
public class Message<T> implements BaseMessage<T>, Serializable {

    private static final long serialVersionUID = -6581653428040313373L;

    /**
     * 消息体
     */
    private T content;

    private Message() {
    }

    private Message(T content) {
        this.content = content;
    }

    public static <T> Message<T> getInstance(T content) {
        notNull(content, "Mq Message content must not null!!!");
        return new Message(content);
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


}
