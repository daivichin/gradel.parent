package com.gradel.parent.tencent.cmq.api.impl;

import com.gradel.parent.tencent.cmq.api.Action;
import com.gradel.parent.tencent.cmq.api.common.ThreadFactoryImpl;
import com.gradel.parent.tencent.cmq.api.model.SendResult;
import com.gradel.parent.tencent.cmq.api.util.ClientLogger;
import com.gradel.parent.tencent.qcloud.cmq.Message;
import com.gradel.parent.common.task.thread.WorkThread;
import com.gradel.parent.common.util.util.ExceptionUtil;
import lombok.Getter;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 消费统一入口（提交到线程池进行消费）
 */
public class ConsumeMessageConcurrentlyService /*implements ConsumeMessageService*/ {
    private static final Logger log = ClientLogger.getLog();

    @Getter
    private final CMQConfigAbstract cmqConfig;
    private final ThreadPoolExecutor consumeExecutor;
    private final LinkedBlockingQueue blockingQueue;

    private final HashSet<ConsumeRequest> consumeRequestSet;

    @Getter
    private volatile boolean isRuning = true;

    public ConsumeMessageConcurrentlyService(CMQConfigAbstract cmqConfig) {
        this.cmqConfig = cmqConfig;
        this.blockingQueue = new LinkedBlockingQueue(this.cmqConfig.getConsumeThreadQueueSize());
        this.consumeRequestSet = new HashSet<>(this.cmqConfig.getConsumeThreadMax() + 1);
        this.consumeExecutor = new ThreadPoolExecutor(
                this.cmqConfig.getConsumeThreadMin(),
                this.cmqConfig.getConsumeThreadMax(),
                1000 * 60,
                TimeUnit.MILLISECONDS,
                this.blockingQueue,
                new ThreadFactoryImpl("CMQConsumeMessageThread_"));

    }

    public void shutdown() {
        if (log.isInfoEnabled()) {
            log.info("Starting Shutting ConsumeMessageConcurrentlyService ... waiting for 60s");
        }
        isRuning = false;
        consumeExecutor.shutdown();
        shutdownAllConsumeThread();
        if (log.isInfoEnabled()) {
            log.info("Shutdown thread ConsumeMessageConcurrentlyService Complete!");
        }
    }

    public boolean isShutdown() {
        return consumeExecutor.isShutdown();
    }

    private void shutdownAllConsumeThread() {
        for(Iterator<ConsumeRequest> it = consumeRequestSet.iterator(); it.hasNext();){
            it.next().shutdown();
        }
    }

    private void removeConsumeRequestThread(ConsumeRequest consumeRequest) {
        consumeRequestSet.remove(consumeRequest);
    }

    public int remainingCapacity() {
        return this.blockingQueue.remainingCapacity();
    }

    /**
     *
     * @param msgs
     * @param defaultConsumber
     * @return
     */
    public boolean submitConsumeRequest(final List<Message> msgs, final DefaultConsumber defaultConsumber) {
        if (remainingCapacity() > 0 && isRuning) {
            ConsumeRequest consumeRequest = new ConsumeRequest(msgs, defaultConsumber);
            try {
                this.consumeExecutor.execute(consumeRequest);
                this.consumeRequestSet.add(consumeRequest);
            } catch (RejectedExecutionException e) {
                return false;
            }
            return true;
        }
        return false;

    }

    class ConsumeRequest extends WorkThread {
        private final List<Message> msgs;
        private final DefaultConsumber defaultConsumber;
        //        private final int startTimeSeconds;//进入时间
        //        private final int maxLiveTimeSeconds;//最大存活时间

        public ConsumeRequest(List<Message> msgs, DefaultConsumber defaultConsumber/*, int startTimeSeconds*/) {
            super("ConsumeMsgThread[" + defaultConsumber.getQueue()+"]", false);
            this.msgs = msgs;
            this.defaultConsumber = defaultConsumber;
//            this.startTimeSeconds = startTimeSeconds;
//            this.maxLiveTimeSeconds = maxLiveTimeSeconds;
        }

        @Override
        public void workOnce() {
            if (!ConsumeMessageConcurrentlyService.this.isRuning) {
                return;
            }
            List<String> receiptHandleList = new ArrayList<>(msgs.size());
            List<String> msgIdList = new ArrayList<>(msgs.size());
            for (int i = 0; i < msgs.size(); i++) {
                Message message = msgs.get(i);
                if (message == null) {
                    continue;
                }
                //判断消息是否超过可见时间
                if (!ConsumeMessageConcurrentlyService.this.isExpireMsg(message) && ConsumeMessageConcurrentlyService.this.isRuning) {
                    Action action = defaultConsumber.consumeMessage(message);
                    if (action != null && Action.CommitMessage == action) {
                        receiptHandleList.add(message.receiptHandle);
                        msgIdList.add(message.msgId);
                    }
                } else {
                    break;
                }
            }
            if (!receiptHandleList.isEmpty()) {
                try {
                    SendResult sendResult = defaultConsumber.getCmqClientRemoteAPI().batchDeleteMessage(defaultConsumber.getQueue(), receiptHandleList);
                    if (!sendResult.isSuccess()) {
                        Object[] params = new Object[]{defaultConsumber.getQueue(), sendResult.getErrCode(), sendResult.getErrMsg(), sendResult.getRequestId(), receiptHandleList};
                        log.error("BatchDeleteMessage failure, queue:[{}], code:[{}], errMsg:[{}], requestId:[{}], msgIdList:[{}], receiptHandleList:[{}]", params);
                    }
                } catch (Exception e) {
                    Object[] params = new Object[]{defaultConsumber.getQueue(), msgIdList, receiptHandleList, ExceptionUtil.getAsString(e)};
                    log.error("BatchDeleteMessage exception, queue:[{}], msgIdList:[{}], receiptHandleList:[{}], Exception:{}", params);
                }
//                receiptHandleList.clear();
            }

        }

        @Override
        public void shutdown() {
            if (log.isInfoEnabled()) {
                log.info("Starting Shutting {}", getName());
            }
            super.shutdown();
            removeConsumeRequestThread(this);
            if (log.isInfoEnabled()) {
                log.info("Shutdown thread {} Complete!", getName());
            }
        }

        @Override
        public void doShutDownComplete() {
            super.doShutDownComplete();
            removeConsumeRequestThread(this);
        }
    }

    /**
     * 是否过了可见时间
     *
     * @param message
     * @return
     */
    public boolean isExpireMsg(Message message) {
        return message.nextVisibleTime - getCurrentTimeSeconds() < cmqConfig.getDefaultLiveTimeSeconds();
    }

    private long getCurrentTimeSeconds() {
        return System.currentTimeMillis() / 1000;
    }
}
