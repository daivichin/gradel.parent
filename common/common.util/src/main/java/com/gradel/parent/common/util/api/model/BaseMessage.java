package com.gradel.parent.common.util.api.model;

import java.io.Serializable;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @Description: 消息统一载体
 */
public interface BaseMessage<T> extends Serializable{
    /**
     * 消息体
     * @return
     */
    T getContent();
}
