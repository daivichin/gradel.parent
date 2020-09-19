package com.gradel.parent.tencent.cmq.test.benchmark.whiletrue;


import com.gradel.parent.common.util.api.model.Message;
import com.gradel.parent.common.util.util.JsonUtil;
import com.gradel.parent.tencent.cmq.api.Producer;
import com.gradel.parent.tencent.cmq.api.impl.CMQFactory;
import com.gradel.parent.tencent.cmq.api.model.CMQMessage;
import com.gradel.parent.tencent.cmq.api.model.SendResult;
import com.gradel.parent.tencent.cmq.test.benchmark.BenchQueueEnum;
import com.gradel.parent.tencent.cmq.test.config.ConfigManager;
import com.gradel.parent.tencent.cmq.test.quickstart.MsgBody;
import com.gradel.parent.tencent.cmq.test.util.PropertiesUtil;
import com.gradel.parent.tencent.cmq.test.util.ServerUtil;
import org.apache.commons.cli.*;

import java.util.LinkedList;
import java.util.Properties;
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
public class ProducerBenchmarkLoopTest {

    /**
     * 线程数
     */
    public final static int Default_ThreadCount = 200;
    final static BenchQueueEnum Default_Queue = BenchQueueEnum.QUEUE_BenchmarkTest;//"BenchmarkTest";
    final static int Default_MessageSize = 128;
    final static int Default_DelayTimeSeconds = 0;

    public static void main(String[] args) {

        Options options = ServerUtil.buildCommandlineOptions(new Options());
        CommandLine commandLine = ServerUtil.parseCmdLine("ProducerBenchmarkLoopTest", args, buildCommandlineOptions(options), new DefaultParser());
        if (null == commandLine) {
            System.exit(-1);
        }

        final int threadCount = commandLine.hasOption('c') ? Integer.parseInt(commandLine.getOptionValue('c')) : Default_ThreadCount;
        final int messageSize = commandLine.hasOption('s') ? Integer.parseInt(commandLine.getOptionValue('s')) : Default_MessageSize;
        final String queueName = commandLine.hasOption('q') ? commandLine.getOptionValue('q').trim() : Default_Queue.getCode();
        final int delayTimeSeconds = commandLine.hasOption('d') ? Integer.parseInt(commandLine.getOptionValue('d')) : Default_DelayTimeSeconds;

        final String MESSAGE_BODY = buildMessageBySize(messageSize);

        BenchQueueEnum tempQueue = null;
        try {
            tempQueue = BenchQueueEnum.resolve(queueName);
        } catch (Exception e) {
        }

        if (tempQueue == null) {
            tempQueue = Default_Queue;
        }

        final BenchQueueEnum queue = tempQueue;

        Properties producerProp = ConfigManager.getProducerProp();

        System.out.printf("%s%n", PropertiesUtil.toString(producerProp));

        final Producer producer = CMQFactory.createProducer(producerProp);

        System.out.printf("Queue: %s messageSize: %d %n", queue, messageSize);

        final ExecutorService sendThreadPool = Executors.newFixedThreadPool(threadCount);
        final CountDownLatch countDownLatch = new CountDownLatch(threadCount);

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
                    while (true) {
                        try {
                            final CMQMessage msg = buildMessage(queue, MESSAGE_BODY, messageSize);
                            final long beginTimestamp = System.currentTimeMillis();
                            if (delayTimeSeconds > 0) {
                                msg.setDelayTimeSeconds(delayTimeSeconds);
                            }
                            SendResult send = producer.send(msg, (t) -> JsonUtil.toJson(t));
                            if (send.isSuccess()) {

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
                            } else {
                                if (send.getErrCode() > 0) {
                                    statsBenchmark.getReceiveResponseFailedCount().increment();
                                } else {
                                    statsBenchmark.getSendRequestFailedCount().increment();
                                }
                            }
                        } catch (Exception e) {
                            if (e.getCause() == null) {//MQClientException
                                statsBenchmark.getSendRequestFailedCount().increment();
//                                System.out.printf("Current failed times:%d, [BENCHMARK_PRODUCER] Send Exception:%s%n", times, e.getMessage());
                                /*try {
                                    Thread.sleep(3000);
                                } catch (InterruptedException e1) {
                                }*/
                            } else {
                                statsBenchmark.getReceiveResponseFailedCount().increment();
//                                System.out.printf("Current failed times:%d, [BENCHMARK_PRODUCER] Send Exception:%s%n", times, e.getMessage());
                                /*try {
                                    Thread.sleep(3000);
                                } catch (InterruptedException ignored) {
                                }*/
                            }
                        }
                    }
//                    countDownLatch.countDown();
                }
            });
        }


        // 完成
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        timer.cancel();
        sendThreadPool.shutdown();
        producer.shutdown();


    }

    private static CMQMessage buildMessage(BenchQueueEnum queueEnum, String mesBody, int msgSize) {
        MsgBody msgBody = new MsgBody();
        CMQMessage<MsgBody> msg = new CMQMessage<>(queueEnum, Message.getInstance(msgBody));
        msgBody.setContent(mesBody);
        msgBody.setIndex(msgSize);
        return msg;
    }


    private static String buildMessageBySize(int messageSize) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < messageSize; i += 10) {
            sb.append("hello baby");
        }
        return sb.toString();//.getBytes(CommonConstants.UTF8_CHARSET);
    }

    public static Options buildCommandlineOptions(final Options options) {
        Option opt = new Option("c", "threadCount", true, "Thread count, Default: " + Default_ThreadCount);
        opt.setRequired(false);
        options.addOption(opt);

        opt = new Option("s", "messageSize", true, "Message Size, Default: " + Default_MessageSize);
        opt.setRequired(false);
        options.addOption(opt);

        opt = new Option("q", "queue", true, "Queue name, Default: " + Default_Queue.getCode());
        opt.setRequired(false);
        options.addOption(opt);

        opt = new Option("d", "delayTimeSeconds", true, "Delay Time Seconds, Default: " + Default_DelayTimeSeconds);
        opt.setRequired(false);
        options.addOption(opt);

        return options;
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

