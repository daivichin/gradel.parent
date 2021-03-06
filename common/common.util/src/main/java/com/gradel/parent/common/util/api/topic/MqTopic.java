package com.gradel.parent.common.util.api.topic;

/**
* User: sdeven.chen.dongwei@gmail.com
 * @date: 2016/11/24
 * @Description:
 */
public interface MqTopic extends Topic {
    /**
     * 二级主题 // Message Tag 可理解为Gmail中的标签，对消息进行再归类，方便Consumer指定过滤条件在MQ服务器过滤
     * @return
     */
    default String getTag(){
        return "*";
    }
}
