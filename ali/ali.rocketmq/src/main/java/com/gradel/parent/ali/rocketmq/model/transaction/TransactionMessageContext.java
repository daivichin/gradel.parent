package com.gradel.parent.ali.rocketmq.model.transaction;

import com.gradel.parent.ali.rocketmq.model.MessageContext;
import lombok.Getter;

import java.util.Properties;

public class TransactionMessageContext extends MessageContext {

    /**
     * 当前系统时间
     */
    @Getter
    private long currentSysTimeMil = System.currentTimeMillis();

    public TransactionMessageContext() {
    }

    public TransactionMessageContext(String topic, Properties userProperties, Properties systemProperties/*, String tag, String msgID, String key, int retryTimes, long bornTimestamp, String bornHost, String shardingKey*/) {
        super(topic, userProperties, systemProperties/*, tag, msgID, key, retryTimes, bornTimestamp, bornHost, shardingKey*/);
    }
}
