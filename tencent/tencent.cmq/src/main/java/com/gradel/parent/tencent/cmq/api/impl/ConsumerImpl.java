package com.gradel.parent.tencent.cmq.api.impl;

import com.gradel.parent.tencent.cmq.api.CMQMessageListener;
import com.gradel.parent.tencent.cmq.api.Consumer;
import com.gradel.parent.tencent.cmq.api.QueueName;
import com.gradel.parent.tencent.cmq.api.model.SendResult;
import com.gradel.parent.tencent.cmq.api.util.ClientLogger;
import com.gradel.parent.tencent.qcloud.cmq.CMQClientException;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


public class ConsumerImpl extends CMQConfigAbstract implements Consumer {
    private final static Logger log = ClientLogger.getLog();
    private final Map<String/* queue */, CMQMessageListener> subscribeQueue = new ConcurrentHashMap<>();
    private final Map<String/* queue */, List<DefaultPullMessageThread>> subscribePullThreadsMap = new ConcurrentHashMap<>();

    private final AtomicBoolean started = new AtomicBoolean(false);

    private ExecutorService threadPool;

    private Properties properties;

    @Getter
    private CMQClientRemoteAPI cmqClientRemoteAPI;
    @Getter
    private ConsumeMessageConcurrentlyService messageConcurrentlyService;

    public ConsumerImpl() {
        cmqClientRemoteAPI = new CMQClientRemoteAPI(this);
        messageConcurrentlyService = new ConsumeMessageConcurrentlyService(this);
    }

    public ConsumerImpl(Properties properties) {
        this();
        this.properties = properties;
    }

    @Override
    public void start() {
        if(subscribeQueue.size() <= 0){
            if(log.isWarnEnabled()){
                log.warn("CMQ has no Consumers!!!");
                return;
            }
        }
        try {
            if (this.started.compareAndSet(false, true)) {
                super.init(properties);
                SendResult<List<String>> sendResult = cmqClientRemoteAPI.listAllQueue();
                if (!sendResult.isSuccess()) {
                    log.error("Can not load all Queue from cmq, errCode:{}, errMsg:{}", sendResult.getErrCode(), sendResult.getErrMsg());
                    throw new CMQClientException("Can not load all Queue from cmq, errCode:" + sendResult.getErrCode() + ", errMsg:" + sendResult.getErrMsg());
                }

                List<String> vtQueue = sendResult.getData();
                int consumePullThreadNum = getConsumePullThreadNum();
                threadPool = Executors.newFixedThreadPool(subscribeQueue.size() * consumePullThreadNum);
                subscribeQueue.forEach((k, v) -> {
                    if (!vtQueue.contains(k)) {
                        log.error("Can not found Queue[{}] from cmq, please Create the queue first", k);
                        throw new CMQClientException("Can not found Queue[" + k + "] from cmq, please Create the queue first, see:https://cloud.tencent.com/document/product/406/6902");
                    }
                    List<DefaultPullMessageThread> pullThreads = new ArrayList<>(consumePullThreadNum);
                    subscribePullThreadsMap.put(k, pullThreads);
                    for (int i = 0; i < consumePullThreadNum; i++) {
                        DefaultPullMessageThread pullMsgThread = new DefaultPullMessageThread(k, v, ConsumerImpl.this);
                        pullThreads.add(pullMsgThread);
                        threadPool.execute(pullMsgThread);

                    }
                });
            }
        } catch (Exception e) {
            throw new CMQClientException(e.getMessage());
        }
    }


    @Override
    public void shutdown() {
        if (this.started.compareAndSet(true, false)) {
            this.threadPool.shutdown();
            unsubscribeAll();
            try {
                this.threadPool.awaitTermination(15, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
            }
            this.messageConcurrentlyService.shutdown();
        }
    }

    private void unsubscribeAll() {
        for (Iterator<String> it = subscribePullThreadsMap.keySet().iterator(); it.hasNext(); ) {
            unsubscribe(it.next());
        }
    }

    @Override
    public void subscribe(String queue, CMQMessageListener listener) {
        if (StringUtils.isBlank(queue)) {
            throw new CMQClientException("subscribe queue is null");
        }

        if (null == listener) {
            throw new CMQClientException("subscribe listener is null");
        }

        if (this.subscribeQueue.put(queue, listener) != null) {
            log.error("Repeat the subscription queue[{}], cconsumer:[{}]", queue, listener);
            throw new IllegalArgumentException("CMQConsumer[" + listener.getClass() + "] subscribe queue[" + queue + "] is exist!");
        }
    }

    @Override
    public void unsubscribe(String queue) {
        if (null != queue) {
            List<DefaultPullMessageThread> pullMsgThreads = subscribePullThreadsMap.get(queue);
            if (pullMsgThreads != null) {
                for (DefaultPullMessageThread pullMsgThread : pullMsgThreads) {
                    if (pullMsgThread != null && pullMsgThread.isRunning().get()) {
                        pullMsgThread.shutdown();
                    }
                }
                this.subscribePullThreadsMap.remove(queue);
            }
        }
    }

    @Override
    public boolean isStarted() {
        return started.get();
    }


    @Override
    public boolean isClosed() {
        return !isStarted();
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    /**
     * set for Map
     *
     * @param subscribeQueue
     */
    public void setSubscribeQueue(Map<String, CMQMessageListener> subscribeQueue) {
        if (subscribeQueue != null) {
            subscribeQueue.forEach((k, v) -> {
                subscribe(k, v);
            });
        }
    }

    /**
     * set for Map
     *
     * @param subscribeQueue
     */
    public void setSubscribeQueueName(Map<QueueName, CMQMessageListener> subscribeQueue) {
        if (subscribeQueue != null) {
            subscribeQueue.forEach((k, v) -> {
                subscribe(k.getCode(), v);
            });
        }
    }

    /**
     * set for List
     *
     * @param subscriptionList
     */
    public void setSubscriptionList(List<CMQMessageListener> subscriptionList) {
        if (subscriptionList != null) {
            initTopicTagListenerList(subscriptionList);
        }
    }

    private void initTopicTagListenerList(List<CMQMessageListener> subscriptionList) {
        if (subscriptionList != null) {
            subscriptionList.forEach(listener -> {
                if (listener.subscriQueue() != null) {
                    subscribe(listener.subscriQueue().getCode(), listener);
                }
            });
        }
    }
}
