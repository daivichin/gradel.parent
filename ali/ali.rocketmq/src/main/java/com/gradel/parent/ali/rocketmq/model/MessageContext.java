package com.gradel.parent.ali.rocketmq.model;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.impl.rocketmq.ONSUtil;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.common.message.MessageConst;
import lombok.Getter;
import lombok.ToString;
import java.util.Properties;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @date: 2016/11/24
 * @Description:
 * @see ONSUtil
 *
 */
@ToString
public class MessageContext {
    /**
     * 消息主题
     */
    @Getter
    private String topic;
    /**
     * <p> 系统属性 </p>
     */
    @Getter
    private Properties systemProperties;
    /**
     * 用户属性
     */
    @Getter
    private Properties userProperties;
    /**
     * 二级主题分类
     */
//    @Getter
//    private String tag;

    /**
     * 消息ID
     */
//    @Getter
//    private String msgID;

//    @Getter
//    private String key;

    /**
     * 重试次数
     */
//    @Getter
//    private int retryTimes;

    /**
     * 获取消息的生产时间
     */
//    @Getter
//    private long bornTimestamp;

    /**
     * 获取产生消息的主机.
     */
//    @Getter
//    private String bornHost;

//    @Getter
//    private String shardingKey;

    /**
     * @ see MessageConst.PROPERTY_UNIQ_CLIENT_MESSAGE_ID_KEYIDX
     */
//    @Getter
//    private String uniqMsgId;

    public MessageContext() {
    }

    public MessageContext(String topic, Properties userProperties, Properties systemProperties/*, String tag, String msgID, String key, int retryTimes, long bornTimestamp, String bornHost, String shardingKey*/) {
        this.topic = topic;
        this.userProperties = userProperties;
        this.systemProperties = systemProperties;
        /*this.tag = tag;
        this.msgID = msgID;
        this.key = key;
        this.retryTimes = retryTimes;
        this.bornTimestamp = bornTimestamp;
        this.bornHost = bornHost;
        this.shardingKey = shardingKey;*/
    }

    /**
     * 获取消息标签
     *
     * @return 消息标签
     */
    public String getTag() {
        return this.getSystemProperties(Message.SystemPropKey.TAG);
    }

    /**
     * 获取系统键的值
     *
     * @param key 预定义的系统键
     * @return 指定系统键的值
     */
    public String getSystemProperties(final String key) {
        if (null != this.systemProperties) {
            return this.systemProperties.getProperty(key);
        }

        return null;
    }

    /**
     * 获取用户属性
     *
     * @param key 预定义的用户键
     * @return 指定用户键的值
     */
    public String getUserProperties(final String key) {
        if (null != this.userProperties) {
            return this.userProperties.getProperty(key);
        }

        return null;
    }

    /**
     * 获取业务码
     *
     * @return 业务码
     */
    public String getKey() {
        return this.getSystemProperties(Message.SystemPropKey.KEY);
    }

    /**
     * 获取消息ID
     *
     * @return 该消息ID
     */
    public String getMsgID() {
        return this.getSystemProperties(Message.SystemPropKey.MSGID);
    }

    /**
     * 消息消费时, 获取消息已经被重试消费的次数
     *
     * @return 重试消费次数.
     */
    public int getRetryTimes(){
        return getReconsumeTimes();
    }

    /**
     * 消息消费时, 获取消息已经被重试消费的次数
     *
     * @return 重试消费次数.
     */
    public int getReconsumeTimes() {
        String pro = this.getSystemProperties(Message.SystemPropKey.RECONSUMETIMES);
        if (pro != null) {
            return Integer.parseInt(pro);
        }

        return 0;
    }

    /**
     * 获取消息的生产时间
     *
     * @return 消息的生产时间.
     */
    public long getBornTimestamp() {
        String pro = this.getSystemProperties(Message.SystemPropKey.BORNTIMESTAMP);
        if (pro != null) {
            return Long.parseLong(pro);
        }

        return 0L;
    }

    /**
     * 获取产生消息的主机.
     *
     * @return 产生消息的主机
     */
    public String getBornHost() {
        String pro = this.getSystemProperties(Message.SystemPropKey.BORNHOST);
        return pro == null ? "" : pro;
    }

    /**
     * 获取定时消息开始投递时间.
     *
     * @return 定时消息的开始投递时间.
     */
    public long getStartDeliverTime() {
        String pro = this.getSystemProperties(Message.SystemPropKey.STARTDELIVERTIME);
        if (pro != null) {
            return Long.parseLong(pro);
        }

        return 0L;
    }

    public String getShardingKey() {
        String pro = this.getSystemProperties(Message.SystemPropKey.SHARDINGKEY);
        return pro == null ? "" : pro;
    }

    public String getUniqMsgId(){
        String pro = this.getUserProperties(MessageConst.PROPERTY_UNIQ_CLIENT_MESSAGE_ID_KEYIDX);
        return pro == null ? "" : pro;
    }

    /**
     * 客户端开始消息时间（取本地：System.currentTimeMillis()）
     * @return
     */
    public long consumeStartTime(){
        String pro = this.getSystemProperties(MessageConst.PROPERTY_CONSUME_START_TIMESTAMP);
        if (pro != null) {
            return Long.parseLong(pro);
        }

        return 0L;
    }
    /*@Override
    public String toString() {
        return "Message [topic=" + topic + ", systemProperties=" + systemProperties + ", userProperties=" + userProperties;
    }*/
}
