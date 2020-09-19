package com.gradel.parent.tencent.cmq.api;

public class PropertyKeyConst {
    // cmq
    public static final String CMQ_ENDPOINT = "cmq.endpoint";
    public static final String CMQ_SECRETID = "cmq.secretId";
    public static final String CMQ_SECRETKEY = "cmq.secretKey";
    public static final String CMQ_CONSUME_POLLINGWAITSECONDS = "cmq.consumePollingWaitSeconds";//拉取消息等待时间
    public static final String CMQ_CONSUME_BATCHRECEIVEMSGSIZE = "cmq.consumeBatchReceiveMsgSize";//单个队列一次拉取消息数
    public static final String CMQ_CONSUME_BATCHMSGSIZE = "cmq.consumeBatchMsgSize";//业务线程一次消费消息最大数
    public static final String CMQ_CONSUME_THREADMIN = "cmq.consumeThreadMin";//业务线程池最小线程数
    public static final String CMQ_CONSUME_THREADMAX = "cmq.consumeThreadMax";//业务线程池最大线程数

    public static final String CMQ_CONSUME_THREAD_QUEUE_SIZE = "cmq.consumeThreadQueueSize";//业务线程池队列大小
    public static final String CMQ_CONSUME_PULL_THREAD_NUM = "cmq.consumePullThreadNum";//单个队列拉取消息的线程数
    public static final String CMQ_PATH = "cmq.path";
    public static final String CMQ_METHOD = "cmq.method";
    public static final String CMQ_SIGNMETHOD = "cmq.signMethod";
    public static final String CMQ_ISKEEPALIVE = "cmq.isKeepalive";


    //用户拉取(http)消息等待时间(毫秒)
    public static final String SOCKET_READ_USERPOLLINGWAIT_MILLISECOND = "UserpollingWaitSeconds";
    //CMQ服务器拉取超时时间(秒)
    public static final String CMQ_PARAM_POLLINGWAITSECONDS = "pollingWaitSeconds";

    //mq http ConnectTimeout 用户连接CMQ服务器(http)消息等待时间(毫秒)
    public static final String MQ_HTTP_CONNECT_WAIT_MILLISECOND = "mqHttpConnectWaitMillisecond";
    //mq http ReadTimeout 用户读取CMQ服务器(http)消息等待时间(毫秒)
    public static final String MQ_HTTP_READ_WAIT_MILLISECOND = "mqHttpReadWaitMillisecond";

}
