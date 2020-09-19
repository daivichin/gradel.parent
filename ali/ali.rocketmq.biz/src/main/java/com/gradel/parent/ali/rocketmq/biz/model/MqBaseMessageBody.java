package com.gradel.parent.ali.rocketmq.biz.model;

import com.gradel.parent.common.util.api.model.BaseMessage;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @Description: 消息统一载体
 *
 * @see MqMessageBody
 */
public interface MqBaseMessageBody<T> extends BaseMessage<T> {
    /**
     * 业务ID(rockmq 可以根据当前业务ID和主题查询消息) --注意：不设置也不会影响消息正常收发
     * @return
     */
    String getBusinessId();
}
