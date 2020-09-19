package com.gradel.parent.ali.rocketmq.consumer;

import com.gradel.parent.ali.rocketmq.util.MqUtil;
import com.gradel.parent.common.util.constants.CommonConstants;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @date 2019/03/06 下午10:32
 * @Description: 检查订阅关系是否一致, 只能检查当前GroupId(consumerId)是否有多个实例，不能检查订阅不同topic
 * <p>
 * 同一个GID订阅的是两个不同的topic，所以出现了订阅关系不一致的情况
 */
public interface ConsumerCheck {
    ConcurrentHashMap<String, Object> consumerIds = new ConcurrentHashMap<>();

    default void check(String consumerId) throws IllegalArgumentException {
        if (consumerIds.putIfAbsent(consumerId, CommonConstants.EMPTY_OBJECT) != null) {
            throw new IllegalArgumentException("GroupId(consumerId):" + consumerId + " 订阅关系不一致(只能检查当前GroupId(consumerId)是否有多个实例，不能检查是否订阅不同topic，只能通过日志ons.log或者控制台查询)");
        }
    }

    public static void main(String[] args){

        MqUtil.check("1213");
        MqUtil.check("1213");

    }
}
