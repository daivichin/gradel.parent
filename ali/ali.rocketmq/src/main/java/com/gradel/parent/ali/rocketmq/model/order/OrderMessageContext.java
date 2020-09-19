package com.gradel.parent.ali.rocketmq.model.order;

import com.gradel.parent.ali.rocketmq.model.MessageContext;
import lombok.Data;
import lombok.ToString;

import java.util.Properties;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @date: 2016/11/24
 * @Description:
 */
@Data
@ToString
public class OrderMessageContext extends MessageContext {

    public OrderMessageContext() {
    }

    public OrderMessageContext(String topic, Properties userProperties, Properties systemProperties/*, String tag, String msgID, String key, int retryTimes, long bornTimestamp, String bornHost, String shardingKey*/) {
        super(topic, userProperties, systemProperties/*, tag, msgID, key, retryTimes, bornTimestamp, bornHost, shardingKey*/);
    }
}
