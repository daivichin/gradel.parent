package com.gradel.parent.tencent.cmq.test.quickstart;


import com.gradel.parent.common.util.util.JsonUtil;
import com.gradel.parent.tencent.cmq.api.Producer;
import com.gradel.parent.tencent.cmq.api.impl.CMQFactory;
import com.gradel.parent.tencent.cmq.api.model.CMQMessage;
import com.gradel.parent.tencent.cmq.api.model.CMQMessageBody;
import com.gradel.parent.tencent.cmq.api.model.SendResult;
import com.gradel.parent.tencent.cmq.test.benchmark.BenchQueueEnum;
import com.gradel.parent.tencent.cmq.test.config.ConfigManager;
import org.joda.time.DateTime;

/**
 */
public class ProducerDelayTest {

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

        String formatStr = "yyyy-MM-dd HH:mm:ss";

        int dylayTime = 60 * 60;
//        int dylayTime = 60;

        MsgBody body = new MsgBody();
        DateTime time = DateTime.now();
        String content = String.format(String.format("延时%s秒成功消费, 当前发送时间:%s, 消费时间:%s", dylayTime, time.toString(formatStr), time.plusSeconds(dylayTime).toString(formatStr)));
        body.setContent(content);
        body.setIndex(dylayTime);
        body.setDylayTime(dylayTime);

        CMQMessage<MsgBody> cmqMessage = new CMQMessage<>(BenchQueueEnum.QUEUE_BenchmarkTest, CMQMessageBody.getInstance(body));

        cmqMessage.setDelayTimeSeconds(dylayTime);

        System.out.println(content);

        SendResult result = producer.send(cmqMessage, (t) -> JsonUtil.toJson(t));

        System.out.println(result);
        System.out.printf("Producer Started.%n");
    }
}
