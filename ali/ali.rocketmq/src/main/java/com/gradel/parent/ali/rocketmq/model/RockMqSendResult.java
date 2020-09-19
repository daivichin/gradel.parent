package com.gradel.parent.ali.rocketmq.model;

import com.aliyun.openservices.ons.api.SendResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;

/**
 * 发送结果
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RockMqSendResult {

    private static final RockMqSendResult FAIL_INSTANCE = new RockMqSendResult(false, null, null);

    /**
     * 消息是否发送成功，若发送成功（事务消息半状态发送成功），则messageId和topic都有值，否则为null
     */
    private boolean success;
    /**
     * 已发送消息的ID
     *
     * null:发送失败
     */
    @Nullable
    private String messageId;

    /**
     * 已发送消息的主题（真实主题）
     *
     * null:发送失败
     */
    @Nullable
    private String topic;

    public static RockMqSendResult successIfNotNull(SendResult sendResult){
        if(sendResult != null){
            return new RockMqSendResult(true, sendResult.getMessageId(), sendResult.getTopic());
        }else {
            return fail();
        }

    }

    public static RockMqSendResult fail(){
        return FAIL_INSTANCE;
    }
}
