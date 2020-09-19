package com.gradel.parent.tencent.cmq.api.model;

import com.gradel.parent.common.util.api.model.BaseMessage;
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
public class CMQMessageBody<T> implements Serializable, BaseMessage<T> {

    private static final long serialVersionUID = -6581653428040313373L;

    /**
     * 消息体 不能为空----为了当出现异常时，方便输出重要日志信息，必须都实现toString方法（@ToString(callSuper = true)）
     */
    private T content;

    public CMQMessageBody() {
    }

    public CMQMessageBody(T content) {
        notNull(content, "CMQ Message content must not null!!!");
        this.content = content;
    }

    public static <T> CMQMessageBody<T> getInstance(T content) {
        notNull(content, "CMQ Message content must not null!!!");
        return new CMQMessageBody(content);
    }

    private static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }
}
