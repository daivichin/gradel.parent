package com.gradel.parent.tencent.cmq.api.model;

import com.gradel.parent.common.util.api.model.Message;
import com.gradel.parent.common.util.api.model.BaseMessage;
import com.gradel.parent.common.util.api.topic.Topic;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@ToString(callSuper = true)
public class CMQMessage<T> extends CMQBodyMessage implements Serializable {

    @Getter @Setter
    private BaseMessage<T> content;

    @Getter @Setter
    private int connectTimeoutMilliseconds = -1;// -1 default 10s
    @Getter @Setter
    private int readTimeoutMilliseconds = -1;// -1 default 10s

    public CMQMessage() {
    }

    /**
     * @param topic 消息主题
     * @param content Message Body 可以是任何二进制形式的数据， MQ不做任何干预， 需要Producer与Consumer协商好一致的序列化和反序列化方式
     *
     * @see Message
     * @see BaseMessage
     * @see CMQMessageBody
     */
    public CMQMessage(Topic topic, BaseMessage<T> content) {

        //businessId:业务ID(rockmq 可以根据当前业务ID和主题查询消息)  --注意：不设置也不会影响消息正常收发
        super(topic.getCode());
        this.content = content;
    }
}

