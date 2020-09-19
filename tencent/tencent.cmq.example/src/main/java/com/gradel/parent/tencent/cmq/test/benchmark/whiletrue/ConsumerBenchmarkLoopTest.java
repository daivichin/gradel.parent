package com.gradel.parent.tencent.cmq.test.benchmark.whiletrue;


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
import com.gradel.parent.tencent.cmq.test.util.PropertiesUtil;

import java.util.LinkedList;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @date: 2017-12-11
 * @Description:
 */
public class ConsumerBenchmarkLoopTest {


    final static String queue = BenchQueueEnum.QUEUE_BenchmarkTest.getCode();



    public static void main(String[] args) throws Exception {
        Properties consumerProp = ConfigManager.getConsumerProp();

        System.out.printf("%s%n", PropertiesUtil.toString(consumerProp));

        final Consumer consumer = CMQFactory.createConsumer(ConfigManager.getConsumerProp());

        final StatsBenchmarkConsumer statsBenchmarkConsumer = new StatsBenchmarkConsumer();

        final Timer timer = new Timer("BenchmarkTimerThread", true);

        final LinkedList<Long[]> snapshotList = new LinkedList<Long[]>();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                snapshotList.addLast(statsBenchmarkConsumer.createSnapshot());
                if (snapshotList.size() > 10) {
                    snapshotList.removeFirst();
                }
            }
        }, 1000, 1000);

        timer.scheduleAtFixedRate(new TimerTask() {
            private void printStats() {
                if (snapshotList.size() >= 10) {
                    Long[] begin = snapshotList.getFirst();
                    Long[] end = snapshotList.getLast();

                    final long consumeTps =
                            (long) (((end[1] - begin[1]) / (double) (end[0] - begin[0])) * 1000L);
                    final double averageB2CRT = (end[2] - begin[2]) / (double) (end[1] - begin[1]);
                    final double averageS2CRT = (end[3] - begin[3]) / (double) (end[1] - begin[1]);

                    System.out.printf("Consume TPS: %d, Average(B2C) RT: %7.3f, Average(S2C) RT: %7.3f, MAX(B2C) RT: %d, MAX(S2C) RT: %d, reMsgTotalCount: %d%n",
                            consumeTps, averageB2CRT, averageS2CRT, end[4], end[5], end[1]
                    );
                }
            }

            @Override
            public void run() {
                try {
                    this.printStats();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 10000, 10000);


        consumer.subscribe(queue, new CMQMessageListener() {
            @Override
            public BaseMessage deserialize(String json) throws SerializationException {
//                System.out.println(json);
                return new CMQMessageBody();
            }

            @Override
            public Action consume(BaseMessage message, MessageContext context) {
                long now = System.currentTimeMillis();

                statsBenchmarkConsumer.getReceiveMessageTotalCount().increment();

                long born2ConsumerRT = now - context.getFirstDequeueTime();
                statsBenchmarkConsumer.getBorn2ConsumerTotalRT().add(born2ConsumerRT);

                long store2ConsumerRT = now - context.getEnqueueTime();
                statsBenchmarkConsumer.getStore2ConsumerTotalRT().add(store2ConsumerRT);

                compareAndSetMax(statsBenchmarkConsumer.getBorn2ConsumerMaxRT(), born2ConsumerRT);

                compareAndSetMax(statsBenchmarkConsumer.getStore2ConsumerMaxRT(), store2ConsumerRT);

                return Action.CommitMessage;
            }
        });

        consumer.start();

        System.out.printf("Consumer Started.%n");
    }

    public static void compareAndSetMax(final AtomicLong target, final long value) {
        long prev = target.get();
        while (value > prev) {
            boolean updated = target.compareAndSet(prev, value);
            if (updated)
                break;

            prev = target.get();
        }
    }
}

class StatsBenchmarkConsumer {
    private final LongAdder receiveMessageTotalCount = new LongAdder();

    private final LongAdder born2ConsumerTotalRT = new LongAdder();

    private final LongAdder store2ConsumerTotalRT = new LongAdder();

    private final AtomicLong born2ConsumerMaxRT = new AtomicLong(0L);

    private final AtomicLong store2ConsumerMaxRT = new AtomicLong(0L);

    public Long[] createSnapshot() {
        Long[] snap = new Long[]{
                System.currentTimeMillis(),
                this.receiveMessageTotalCount.longValue(),
                this.born2ConsumerTotalRT.longValue(),
                this.store2ConsumerTotalRT.longValue(),
                this.born2ConsumerMaxRT.longValue(),
                this.store2ConsumerMaxRT.longValue(),
        };

        return snap;
    }

    public void print() {
        Long[] snap = new Long[]{
                this.receiveMessageTotalCount.longValue(),
                this.born2ConsumerTotalRT.longValue(),
                this.store2ConsumerTotalRT.longValue(),
                this.born2ConsumerMaxRT.longValue(),
                this.store2ConsumerMaxRT.longValue(),
        };
        System.out.printf("receiveMessageTotalCount: %d, born2ConsumerTotalRT: %d, store2ConsumerTotalRT: %d, born2ConsumerMaxRT: %d, store2ConsumerMaxRT: %d%n", snap);
    }

    public LongAdder getReceiveMessageTotalCount() {
        return receiveMessageTotalCount;
    }

    public LongAdder getBorn2ConsumerTotalRT() {
        return born2ConsumerTotalRT;
    }

    public LongAdder getStore2ConsumerTotalRT() {
        return store2ConsumerTotalRT;
    }

    public AtomicLong getBorn2ConsumerMaxRT() {
        return born2ConsumerMaxRT;
    }

    public AtomicLong getStore2ConsumerMaxRT() {
        return store2ConsumerMaxRT;
    }
}

