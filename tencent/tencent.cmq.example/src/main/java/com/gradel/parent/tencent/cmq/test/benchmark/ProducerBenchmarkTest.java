package com.gradel.parent.tencent.cmq.test.benchmark;

import com.gradel.parent.common.util.api.model.Message;
import com.gradel.parent.common.util.util.JsonUtil;
import com.gradel.parent.tencent.cmq.api.Producer;
import com.gradel.parent.tencent.cmq.api.impl.CMQFactory;
import com.gradel.parent.tencent.cmq.api.model.CMQMessage;
import com.gradel.parent.tencent.cmq.api.model.SendResult;
import com.gradel.parent.tencent.cmq.test.config.ConfigManager;
import com.gradel.parent.tencent.cmq.test.quickstart.MsgBody;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @date: 2017-12-11
 * @Description:
 */
public class ProducerBenchmarkTest {

    public final static int threadCount = 50;
    public final static int threadMessageSize = 1000;


    final static BenchQueueEnum queue = BenchQueueEnum.QUEUE_BenchmarkTest;//"BenchmarkTest";
    final static int messageSize = 3500;
    final static int delayTimeSeconds = 0;


    final static String MESSAGE_BODY =  buildMessageBySize(messageSize);

    public static void main(String[] args){

        final Producer producer = CMQFactory.createProducer(ConfigManager.getProducerProp());

        final CountDownLatch countDownLatch = new CountDownLatch(threadCount);


        System.out.printf("queue %s threadCount %d messageSize %d %n", queue, threadCount, messageSize);

        final ExecutorService sendThreadPool = Executors.newFixedThreadPool(threadCount);

        final StatsBenchmarkProducer statsBenchmark = new StatsBenchmarkProducer();

        final Timer timer = new Timer("BenchmarkTimerThread", true);

        final LinkedList<Long[]> snapshotList = new LinkedList<Long[]>();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                snapshotList.addLast(statsBenchmark.createSnapshot());
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

                    final long sendTps = (long) (((end[3] - begin[3]) / (double) (end[0] - begin[0])) * 1000L);
                    final double averageRT = (end[5] - begin[5]) / (double) (end[3] - begin[3]);

                    System.out.printf("Send TPS: %d, Max RT: %d, Average RT: %7.3f, Send Failed: %d, Response Failed: %d%n",
                            sendTps, statsBenchmark.getSendMessageMaxRT().longValue(), averageRT, end[2], end[4]);
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


        producer.start();

        for (int i = 0; i < threadCount; i++) {
            sendThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    int total = threadMessageSize;
                    while (total > 0) {
                        try {
                            final CMQMessage msg = buildMessage();
                            final long beginTimestamp = System.currentTimeMillis();
                            if (delayTimeSeconds > 0) {
                                msg.setDelayTimeSeconds(delayTimeSeconds);
                            }
                            SendResult send = producer.send(msg, (t) -> JsonUtil.toJson(t));
                            if(send.isSuccess()){
                                statsBenchmark.getSendRequestSuccessCount().increment();
                                statsBenchmark.getReceiveResponseSuccessCount().increment();
                                final long currentRT = System.currentTimeMillis() - beginTimestamp;
                                statsBenchmark.getSendMessageSuccessTimeTotal().add(currentRT);
                                long prevMaxRT = statsBenchmark.getSendMessageMaxRT().longValue();
                                while (currentRT > prevMaxRT) {
                                    boolean updated = statsBenchmark.getSendMessageMaxRT().compareAndSet(prevMaxRT, currentRT);
                                    if (updated)
                                        break;

                                    prevMaxRT = statsBenchmark.getSendMessageMaxRT().longValue();
                                }
                            }else{
                                if(send.getErrCode() > 0){
                                    statsBenchmark.getReceiveResponseFailedCount().increment();
                                }else{
                                    statsBenchmark.getSendRequestFailedCount().increment();
                                }
                            }
                        } catch (Exception e) {
                            if(e.getCause() == null){//MQClientException
                                statsBenchmark.getSendRequestFailedCount().increment();
//                                System.out.printf("Current failed times:%d, [BENCHMARK_PRODUCER] Send Exception:%s%n", times, e.getMessage());
                                /*try {
                                    Thread.sleep(3000);
                                } catch (InterruptedException e1) {
                                }*/
                            }else{
                                statsBenchmark.getReceiveResponseFailedCount().increment();
//                                System.out.printf("Current failed times:%d, [BENCHMARK_PRODUCER] Send Exception:%s%n", times, e.getMessage());
                                /*try {
                                    Thread.sleep(3000);
                                } catch (InterruptedException ignored) {
                                }*/
                            }
                        }
                        //--
                        total--;
                    }

                    countDownLatch.countDown();
                    System.out.printf("%s send message finished!%n", Thread.currentThread().getName());
                }
            });
        }


        // 完成
        try {

            countDownLatch.await();
            System.out.println("------------------------------------------------");
            statsBenchmark.print();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        timer.cancel();
        sendThreadPool.shutdown();
        producer.shutdown();


    }

    private static CMQMessage buildMessage() {
        MsgBody msgBody = new MsgBody();
        CMQMessage<MsgBody> msg = new CMQMessage<>(queue, Message.getInstance(msgBody));
        msgBody.setContent(MESSAGE_BODY);
        msgBody.setIndex(messageSize);
        return msg;
    }

    private static String buildMessageBySize(int messageSize) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < messageSize; i += 10) {
            sb.append("hello baby");
        }
        //.getBytes(CommonConstants.UTF8_CHARSET);
        return sb.toString();
    }
}

class StatsBenchmarkProducer {
    private final LongAdder sendRequestSuccessCount = new LongAdder();

    private final LongAdder sendRequestFailedCount = new LongAdder();

    private final LongAdder receiveResponseSuccessCount = new LongAdder();

    private final LongAdder receiveResponseFailedCount = new LongAdder();

    private final LongAdder sendMessageSuccessTimeTotal = new LongAdder();

    private final AtomicLong sendMessageMaxRT = new AtomicLong();

    public Long[] createSnapshot() {
        Long[] snap = new Long[]{
                System.currentTimeMillis(),
                this.sendRequestSuccessCount.longValue(),
                this.sendRequestFailedCount.longValue(),
                this.receiveResponseSuccessCount.longValue(),
                this.receiveResponseFailedCount.longValue(),
                this.sendMessageSuccessTimeTotal.longValue(),
        };

        return snap;
    }

    public void print() {
        Long[] snap = new Long[]{
                this.sendRequestSuccessCount.longValue(),
                this.sendRequestFailedCount.longValue(),
                this.receiveResponseSuccessCount.longValue(),
                this.receiveResponseFailedCount.longValue(),
                this.sendMessageSuccessTimeTotal.longValue(),
        };
        System.out.printf("sendRequestSuccessCount: %d, sendRequestFailedCount: %d, receiveResponseSuccessCount: %d, receiveResponseFailedCount: %d, sendMessageSuccessTimeTotal: %d%n", snap);
    }

    public LongAdder getSendRequestSuccessCount() {
        return sendRequestSuccessCount;
    }

    public LongAdder getSendRequestFailedCount() {
        return sendRequestFailedCount;
    }

    public LongAdder getReceiveResponseSuccessCount() {
        return receiveResponseSuccessCount;
    }

    public LongAdder getReceiveResponseFailedCount() {
        return receiveResponseFailedCount;
    }

    public LongAdder getSendMessageSuccessTimeTotal() {
        return sendMessageSuccessTimeTotal;
    }

    public AtomicLong getSendMessageMaxRT() {
        return sendMessageMaxRT;
    }
}

