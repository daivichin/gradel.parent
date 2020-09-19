package com.gradel.parent.tencent.cmq.test.quickstart;


import com.gradel.parent.common.util.util.JsonUtil;
import com.gradel.parent.tencent.cmq.api.Producer;
import com.gradel.parent.tencent.cmq.api.impl.CMQFactory;
import com.gradel.parent.tencent.cmq.api.model.CMQMessage;
import com.gradel.parent.tencent.cmq.api.model.CMQMessageBody;
import com.gradel.parent.tencent.cmq.api.model.SendResult;
import com.gradel.parent.tencent.cmq.test.benchmark.BenchQueueEnum;
import com.gradel.parent.tencent.cmq.test.config.ConfigManager;

import java.util.concurrent.ThreadLocalRandom;

/**
 */
public class ProducerTest {

    final static int SIZE = 10;

    static String[] queues = new String[]{"myqueue-test", "myqueue-test1"};
    static String[] messages = new String[]{"message", "notify"};


    public static void main(String[] args) throws Exception {
        /*
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring/spring-mq-producer.xml");
        ctx.start();
        Producer producer = ctx.getBean(Producer.class);
        */

        final Producer producer = CMQFactory.createProducer(ConfigManager.getProducerProp());
        producer.start();

        for (int i = 0; i < SIZE; i++) {
            MsgBody body = new MsgBody();
            body.setContent(messages[ThreadLocalRandom.current().nextInt(1000) % 2]);
            body.setIndex(i);

            CMQMessage<MsgBody> cmqMessage = new CMQMessage<>(BenchQueueEnum.QUEUE_BenchmarkTest, CMQMessageBody.getInstance(body));

//            cmqMessage.setDelayTimeSeconds(10);

            SendResult result = producer.send(cmqMessage, (t) -> JsonUtil.toJson(t));
            if(result.isSuccess()){

            }

            System.out.println(result);
        }
        System.out.printf("Producer Started.%n");
    }
}
