package com.gradel.parent.tencent.cmq.api.impl;

import com.gradel.parent.tencent.cmq.api.CMQMessageListener;
import com.gradel.parent.tencent.cmq.api.util.ClientLogger;
import com.gradel.parent.tencent.qcloud.cmq.CMQServerException;
import com.gradel.parent.tencent.qcloud.cmq.Message;
import com.gradel.parent.common.task.thread.ShutdownableThread;
import com.gradel.parent.common.util.util.ExceptionUtil;
import org.slf4j.Logger;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class DefaultPullMessageThread extends ShutdownableThread {

    private final static Logger log = ClientLogger.getLog();

    private static int[] SLEEP_TIME_ARR = new int[]{1, 3, 3, 5, 7, 9, 11};

    private String queue;
    private CMQMessageListener cmqMessageListener;
    private CMQClientRemoteAPI cmqClientRemoteAPI;
    private ConsumeMessageConcurrentlyService consumeMessageConcurrentlyService;
    private DefaultConsumber defaultConsumber;
    private ConsumerImpl consumerImpl;

    public DefaultPullMessageThread(String queue, CMQMessageListener cmqMessageListener, ConsumerImpl consumerImpl) {
        super("DefaultPullMessage[" + queue + "]", true, true);
        this.queue = queue;
        this.cmqMessageListener = cmqMessageListener;
        this.consumerImpl = consumerImpl;
        this.cmqClientRemoteAPI = consumerImpl.getCmqClientRemoteAPI();
        this.consumeMessageConcurrentlyService = consumerImpl.getMessageConcurrentlyService();
        this.defaultConsumber = new DefaultConsumber(this.queue, this.cmqMessageListener, this.cmqClientRemoteAPI);
    }

    @Override
    public void doWork() {
        if (consumeMessageConcurrentlyService.remainingCapacity() > 0) {
            try {
                List<Message> messageList = cmqClientRemoteAPI.batchReceiveMessage(queue);
                if (messageList != null && messageList.size() > 0) {
                    int currentIndex = 0;
                    while (isRunning().get() && consumeMessageConcurrentlyService.isRuning() && currentIndex < messageList.size()) {
                        //拆分消费
                        List<Message> consumeMessageList = getMaxConsumeMessageList(messageList, currentIndex);
                        if (!submitConsumeRequest(consumeMessageList)) {
                            //已超过可见时间，重新拉取消息
                            break;
                        }
                        currentIndex += consumeMessageList.size();
                    }
                }
            } catch (Exception e) {
                if (e instanceof SocketTimeoutException) {
                    log.error("Read queue:[{}] timed out", queue);
                } else if (e instanceof CMQServerException) {
                    CMQServerException e1 = (CMQServerException) e;
                    //errCode:7000, errMsg:(10200)no message
                    if(e1.getErrorCode() == 7000 && e1.getErrorMessage().indexOf("10200") != -1){
                        //TODO ignore this Exception
                    }else{
                        Object[] params = new Object[]{queue, e1.getErrorCode(), e1.getErrorMessage(), e1.getRequestId()};
                        log.error("BatchReceiveMessage failure, queueName:{}, errCode:{}, errMsg:{}, requestId:{}", params);
                    }
                } else {
                    log.error("BatchReceiveMessage exception, queue:{}, Exception:{}", queue, ExceptionUtil.getAsString(e));
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                }
                return;
            }
        } else {
            log.warn("Consume thread blocking Queue is full, consumeThreadQueueSize is " + consumerImpl.getConsumeThreadQueueSize());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
            }
        }
    }

    private boolean submitConsumeRequest(List<Message> consumeMessageList) {
        //判断当前消费队列是否已満
        int current = 0;
        while (isRunning().get() && consumeMessageConcurrentlyService.isRuning() && !consumeMessageConcurrentlyService.submitConsumeRequest(consumeMessageList, defaultConsumber)) {
            if (current >= SLEEP_TIME_ARR.length) {
                current = 0;
            }
            int sleepTime = SLEEP_TIME_ARR[current];
            if(log.isWarnEnabled())
                log.warn("Consume thread blocking Queue[{}] is full, consumeThreadQueueSize is {}, sleep {}s", queue, consumerImpl.getConsumeThreadQueueSize(), sleepTime);
            try {
                Thread.sleep(sleepTime * 1000);
            } catch (InterruptedException e) {
            }
            current++;

            //判断当前消息是否已超过可见时间
            for (int index = 0; index < consumeMessageList.size(); index++) {
                Message message = consumeMessageList.get(index);
                if (message != null) {
                    //只需要判断第一条消息是否已超过可见时间即可
                    if (consumeMessageConcurrentlyService.isExpireMsg(message)) {
                        return false;
                    } else {
                        //未超过可见时间
                        break;
                    }
                }
            }
        }
        return true;
    }

    private List<Message> getMaxConsumeMessageList(final List<Message> msgs, int startIndex) {
        //每个消费者线程最大消费消息的数量
        final int consumeBatchSize = consumeMessageConcurrentlyService.getCmqConfig().getConsumeBatchMsgSize();
        if (msgs.size() <= consumeBatchSize) {
            return msgs;
        } else {
            int initSize = msgs.size() - startIndex;
            List<Message> msgThis = new ArrayList<Message>(initSize > consumeBatchSize ? consumeBatchSize : initSize);
            for (int i = startIndex, num = 0; i < msgs.size() && num < consumeBatchSize; ) {
                msgThis.add(msgs.get(i));
                i++;
                num++;
            }
            return msgThis;
        }

    }

    @Override
    public void shutdown() {
        if (log.isInfoEnabled()) {
            log.info("Starting Shutting of thread {}", getName());
        }
//        super.shutdown();

        //此处只需要设置关闭状态，不需要等完全关闭，因为没有业务消费逻辑，消费逻辑在另外一个线程里[ConsumeMessageConcurrentlyService]
        super.initiateShutdown();

        if (log.isInfoEnabled()) {
            log.info("Shutdown thread {} Complete!", getName());
        }
    }
}
