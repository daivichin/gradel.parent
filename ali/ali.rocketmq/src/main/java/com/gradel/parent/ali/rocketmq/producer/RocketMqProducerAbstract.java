package com.gradel.parent.ali.rocketmq.producer;

import com.aliyun.openservices.ons.api.exception.ONSClientException;
import com.gradel.parent.ali.rocketmq.model.RockMqMessage;
import com.gradel.parent.common.util.threadlocal.SerialNo;
import lombok.extern.slf4j.Slf4j;

import java.util.Properties;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @date 2019/01/11 下午3:50
 */
@Slf4j
public abstract class RocketMqProducerAbstract {

    //设置发送消息大小，单位字节,官方默认消息大小不得超过4MB字节，否则消息会被丢弃。
    static final String SendMsgBodyMaxBytes = "SendMsgBodyMaxBytes";

    //警告大小
    static final int Default_Warning_Send_Msg_Body_MaxBytes = 1 * 1024 * 1024;//1M

    static final int Default_Send_Msg_Body_MaxBytes = 2 * 1024 * 1024;//2M

    //rocketMq服务端限制最大消息体大小
    static final int MQ_Default_Send_Msg_Body_MaxBytes = 4 * 1024 * 1024 - 1024;//4M 因为要排除头大小(1KB)

    //设置发送消息大小，单位字节,官方默认消息大小不得超过4MB字节，否则消息会被丢弃。
    //1Mb
    private int sendMsgBodyMaxBytes = Default_Send_Msg_Body_MaxBytes;

    public void init(Properties properties) {
        String bodyMaxBytesStr = properties.getProperty(SendMsgBodyMaxBytes);
        if (bodyMaxBytesStr != null) {
            try {
                sendMsgBodyMaxBytes = Integer.parseInt(bodyMaxBytesStr);
            } catch (Exception e) {
                throw new ONSClientException("Mq SendMsgBodyMaxBytes must be number!");
            }
        }
        if (sendMsgBodyMaxBytes <= 0) {
            sendMsgBodyMaxBytes = Default_Send_Msg_Body_MaxBytes;
        }

    }

    protected boolean checkBeforeSendMsg(RockMqMessage<?> message) {
        int currentLen = message.getBody().length;

        //消息体大小超过用户设置的大小，记录日志，并返回失败
        if (currentLen > sendMsgBodyMaxBytes) {
            Object[] params = {SerialNo.getSerialNo(), currentLen, sendMsgBodyMaxBytes, message.getTopic(), message.getTag(), message.getKey()};
            log.error("[{}] Mq send Failure, Because Message size is {} greater than {}, topic:[{}], tag:[{}], key:[{}]", params);
            return false;
        }

        //消息体大小超过MQ服务器设置的大小，记录日志，并返回失败
        if (currentLen > MQ_Default_Send_Msg_Body_MaxBytes) {
            Object[] params = {SerialNo.getSerialNo(), currentLen, MQ_Default_Send_Msg_Body_MaxBytes, message.getTopic(), message.getTag(), message.getKey()};
            log.error("[{}] Mq send Failure, Because Message size is {} greater than {}, topic:[{}], tag:[{}], key:[{}]", params);
            return false;
        }

        //消息体大小超过1M，记录日志
        if (currentLen > Default_Warning_Send_Msg_Body_MaxBytes) {
            Object[] params = {SerialNo.getSerialNo(), currentLen, message.getTopic(), message.getTag(), message.getKey()};
            log.warn("[{}] Mq send big Message size is {}, topic:[{}], tag:[{}], key:[{}]", params);
        }
        return true;
    }
}
