package com.gradel.parent.tencent.cmq.api.impl;

import com.gradel.parent.tencent.cmq.api.PropertyKeyConst;
import com.gradel.parent.tencent.qcloud.cmq.CMQClientException;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Properties;

public abstract class CMQConfigAbstract {
    protected String CURRENT_VERSION = "SDK_JAVA_1.3";
    // 请求最长的Polling等待时间(单位：秒)
    public static int DEFAULT_TIMEOUT = 10;
    public static boolean DEFAULT_ISKEEPALIVE = true;
    //CMQ 最大值为16
    public static int DEFAULT_MAX_BATCH_RECEIVE_MSG_SIZE = 16;
    //CMQ 单个业务线程消费消息数
    public static int DEFAULT_CONSUME_BATCH_MSG_SIZE = 4;
    //CMQ 单个队列拉取消息的线程数
    public static int DEFAULT_CONSUME_PULL_THREAD_NUM = 4;
    //最大业务线程数
    public static int DEFAULT_CONSUME_THREAD_MAX = 1000;
    //用户拉取消息默认等待时间(单位：毫秒)
    public static int DEFAULT_USER_POLLING_WAIT_MILLISECOND = DEFAULT_TIMEOUT * 1000;

    @Getter
    private int defaultLiveTimeSeconds = 5;

    @Getter
    private String version = CURRENT_VERSION;
    @Getter
    private String host = "";
    @Getter
    private String signatureMethod = "";
    @Getter
    private String endpoint;
    @Getter
    private String secretId;
    @Getter
    private String secretKey;
    @Getter
    private String path = "/v2/index.php";
    @Getter
    private String method = "POST";
    @Getter
    private String signMethod = "sha1";
    @Getter
    private boolean isKeepalive = DEFAULT_ISKEEPALIVE;
    /**
     * 拉取消息等待时间 (单位：秒)
     */
    @Getter
    private int pollingWaitSeconds = DEFAULT_TIMEOUT;

    /**
     * 单个队列一次拉取消息数
     */
    @Getter
    private int batchReceiveMsgSize = DEFAULT_MAX_BATCH_RECEIVE_MSG_SIZE;//CMQ 最大值为16
    /**
     * 业务线程一次消费消息最大数
     */
    @Getter
    private int consumeBatchMsgSize = DEFAULT_CONSUME_BATCH_MSG_SIZE;
    /**
     * 业务线程池最小线程数
     */
    @Getter
    private int consumeThreadMin = 20;
    /**
     * 业务线程池最大线程数
     */
    @Getter
    private int consumeThreadMax = 64;
    /**
     * 业务线程池队列大小//默认为线程池的3倍
     */
    @Getter
    private int consumeThreadQueueSize = consumeThreadMax * 3;
    /**
     * 单个队列拉取消息的线程数
     */
    @Getter
    private int consumePullThreadNum = DEFAULT_CONSUME_PULL_THREAD_NUM;

    public void init(Properties properties) {

        if (properties == null) {
            throw new CMQClientException("cmq properties must not be null");
        }

        String endpoint = properties.getProperty(PropertyKeyConst.CMQ_ENDPOINT);
        if (StringUtils.isNotBlank(endpoint)) {
            this.endpoint = endpoint;
        } else {
            throw new CMQClientException("please set cmq endpoint");
        }

        String secretId = properties.getProperty(PropertyKeyConst.CMQ_SECRETID);
        if (StringUtils.isNotBlank(secretId)) {
            this.secretId = secretId;
        } else {
            throw new CMQClientException("please set cmq secretId");
        }

        String secretKey = properties.getProperty(PropertyKeyConst.CMQ_SECRETKEY);
        if (StringUtils.isNotBlank(secretKey)) {
            this.secretKey = secretKey;
        } else {
            throw new CMQClientException("please set cmq secretKey");
        }

        String signMethod = properties.getProperty(PropertyKeyConst.CMQ_SIGNMETHOD);
        if (StringUtils.isNotBlank(signMethod)) {
            if (signMethod == "sha1" || signMethod == "sha256") {
                this.signMethod = signMethod;
            } else {
                throw new CMQClientException("signMethod Only support sha256 or sha1");
            }
        }

        //拉取消息等待时间
        String pollingWaitSecondsStr = properties.getProperty(PropertyKeyConst.CMQ_CONSUME_POLLINGWAITSECONDS);
        if (StringUtils.isNotBlank(pollingWaitSecondsStr)) {
            try {
                pollingWaitSeconds = Integer.parseInt(pollingWaitSecondsStr);
            } catch (Exception e) {
                throw new CMQClientException("cmq pollingWaitSeconds must be a number");
            }
            if (pollingWaitSeconds < 0) {
                throw new CMQClientException("cmq pollingWaitSeconds must be greater than 0");
            }
        }

        //单个队列一次拉取消息数
        String batchReceiveMsgSizeStr = properties.getProperty(PropertyKeyConst.CMQ_CONSUME_BATCHRECEIVEMSGSIZE);
        if (StringUtils.isNotBlank(batchReceiveMsgSizeStr)) {
            try {
                batchReceiveMsgSize = Integer.parseInt(batchReceiveMsgSizeStr);
            } catch (Exception e) {
                throw new CMQClientException("cmq batchReceiveMsgSize must be a number");
            }
            if (batchReceiveMsgSize <= 0 || batchReceiveMsgSize > DEFAULT_MAX_BATCH_RECEIVE_MSG_SIZE) {
                throw new CMQClientException("cmq batchReceiveMsgSize must be 1 .. " + DEFAULT_MAX_BATCH_RECEIVE_MSG_SIZE);
            }
        }

        //业务线程一次消费消息最大数
        String consumeBatchMsgSizeStr = properties.getProperty(PropertyKeyConst.CMQ_CONSUME_BATCHMSGSIZE);
        if (StringUtils.isNotBlank(consumeBatchMsgSizeStr)) {
            try {
                consumeBatchMsgSize = Integer.parseInt(consumeBatchMsgSizeStr);
            } catch (Exception e) {
                throw new CMQClientException("cmq consumeBatchMsgSize must be a number");
            }

            if (consumeBatchMsgSize <= 0 || consumeBatchMsgSize > DEFAULT_MAX_BATCH_RECEIVE_MSG_SIZE) {
                throw new CMQClientException("cmq consumeBatchMsgSize must be 1 .. " + DEFAULT_MAX_BATCH_RECEIVE_MSG_SIZE);
            }
        }

        //业务线程池最大线程数
        String consumeThreadMaxStr = properties.getProperty(PropertyKeyConst.CMQ_CONSUME_THREADMAX);
        if (StringUtils.isNotBlank(consumeThreadMaxStr)) {
            try {
                consumeThreadMax = Integer.parseInt(consumeThreadMaxStr);
            } catch (Exception e) {
                throw new CMQClientException("cmq consumeThreadMax must be a number");
            }
            if (consumeThreadMax <= 0 || consumeThreadMax > DEFAULT_CONSUME_THREAD_MAX) {
                throw new CMQClientException("cmq consumeThreadMax must be 1 .. " + DEFAULT_CONSUME_THREAD_MAX);
            }
        }

        //业务线程池最小线程数
        String consumeThreadMinStr = properties.getProperty(PropertyKeyConst.CMQ_CONSUME_THREADMIN);
        if (StringUtils.isNotBlank(consumeThreadMinStr)) {
            try {
                consumeThreadMin = Integer.parseInt(consumeThreadMinStr);
            } catch (Exception e) {
                throw new CMQClientException("cmq consumeThreadMin must be a number");
            }
            if (consumeThreadMin <= 0 || consumeThreadMin > consumeThreadMax) {
                throw new CMQClientException("cmq consumeThreadMin must be greater than 0, and smaller then consumeThreadMax");
            }
        }

        //业务线程池队列大小 //默认为线程池的3倍
        String consumeThreadQueueSizeStr = properties.getProperty(PropertyKeyConst.CMQ_CONSUME_THREAD_QUEUE_SIZE);
        if (StringUtils.isNotBlank(consumeThreadQueueSizeStr)) {
            try {
                consumeThreadQueueSize = Integer.parseInt(consumeThreadQueueSizeStr);
            } catch (Exception e) {
                throw new CMQClientException("cmq consumeThreadQueueSize must be a number");
            }
            if (consumeThreadQueueSize <= 0) {
                throw new CMQClientException("cmq consumeThreadQueueSize must be greater than 0");
            }
        }else{
            consumeThreadQueueSize = consumeThreadMax * 3;
        }

        //单个队列拉取消息的线程数
        String consumePullThreadNumStr = properties.getProperty(PropertyKeyConst.CMQ_CONSUME_PULL_THREAD_NUM);
        if (StringUtils.isNotBlank(consumePullThreadNumStr)) {
            try {
                consumePullThreadNum = Integer.parseInt(consumePullThreadNumStr);
            } catch (Exception e) {
                throw new CMQClientException("cmq consumeThreadMin must be a number");
            }
            if (consumePullThreadNum <= 0 || consumePullThreadNum > DEFAULT_CONSUME_THREAD_MAX) {
                throw new CMQClientException("cmq consumeThreadMin must be greater than 0, and smaller then " + DEFAULT_CONSUME_THREAD_MAX);
            }
        }

        String isKeepaliveStr = properties.getProperty(PropertyKeyConst.CMQ_ISKEEPALIVE);
        if (StringUtils.isNotBlank(isKeepaliveStr)) {
            try {
                isKeepalive = Boolean.parseBoolean(isKeepaliveStr);
            } catch (Exception e) {
                throw new CMQClientException("cmq isKeepalive must be a boolean");
            }
        }

        String method = properties.getProperty(PropertyKeyConst.CMQ_METHOD);
        if (StringUtils.isNotBlank(method)) {
            this.method = method.toUpperCase();
        }

        String path = properties.getProperty(PropertyKeyConst.CMQ_PATH);
        if (StringUtils.isNotBlank(path)) {
            this.path = path;
        }


        if (this.endpoint.startsWith("https")) {
            host = this.endpoint.substring(8);
        } else {
            host = this.endpoint.substring(7);
        }

        if (this.signMethod == "sha256") {
            signatureMethod = "HmacSHA256";
        } else {
            signatureMethod = "HmacSHA1";
        }
    }
}
