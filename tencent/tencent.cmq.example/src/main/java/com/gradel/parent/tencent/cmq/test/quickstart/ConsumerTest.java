package com.gradel.parent.tencent.cmq.test.quickstart;

import com.gradel.parent.common.util.api.model.BaseMessage;
import com.gradel.parent.tencent.cmq.api.Action;
import com.gradel.parent.tencent.cmq.api.CMQMessageListener;
import com.gradel.parent.tencent.cmq.api.Consumer;
import com.gradel.parent.tencent.cmq.api.exception.SerializationException;
import com.gradel.parent.tencent.cmq.api.impl.CMQFactory;
import com.gradel.parent.tencent.cmq.api.model.CMQMessageBody;
import com.gradel.parent.tencent.cmq.api.model.MessageContext;
import com.gradel.parent.tencent.cmq.test.benchmark.BenchQueueEnum;
import com.gradel.parent.tencent.cmq.test.config.ConfigManager;


public class ConsumerTest {

    final static String queue = BenchQueueEnum.QUEUE_BenchmarkTest.getCode();

    public static void main(String[] args) {

        final Consumer consumer = CMQFactory.createConsumer(ConfigManager.getConsumerProp());

        consumer.subscribe(queue, new CMQMessageListener() {
            @Override
            public BaseMessage deserialize(String json) throws SerializationException {
                return new CMQMessageBody(json);
            }

            @Override
            public Action consume(BaseMessage message, MessageContext context) {

                System.out.println(message.getContent() + " " + context);
                return Action.CommitMessage;
            }
        });
        /*
         *  Launch the consumer instance.
         */
        consumer.start();

        System.out.printf("Consumer Started.%n");
    }
}
