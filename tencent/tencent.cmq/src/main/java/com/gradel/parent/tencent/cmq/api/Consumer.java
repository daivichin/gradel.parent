package com.gradel.parent.tencent.cmq.api;

/**
 * 消息消费者，用来订阅消息
 */
public interface Consumer extends Admin {
    /**
     * 启动服务
     */
    void start();


    /**
     * 关闭服务
     */
    void shutdown();


    /**
     * 订阅消息
     *
     * @param queue
     * @param listener
     *         消息回调监听器
     */
    void subscribe(final String queue, final CMQMessageListener listener);


    /**
     * 取消某个queue
     *
     * @param queue
     */
    void unsubscribe(final String queue);
}
