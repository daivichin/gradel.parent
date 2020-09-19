package com.gradel.parent.ali.rocketmq.topic;

import com.aliyun.openservices.ons.api.bean.Subscription;
import com.gradel.parent.common.util.api.topic.MqTopic;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @date: 2016/11/25
 * @Description:
 */
public class SubscibeTopic extends Subscription {
    public SubscibeTopic() {
    }

    public SubscibeTopic(MqTopic topic) {
        setTopic(topic.getCode());
        setExpression(topic.getTag());
    }

    public void setRockMqTopic(MqTopic topic){
        setTopic(topic.getCode());
        setExpression(topic.getTag());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getTopic() == null) ? 0 : getTopic().hashCode()) + ((getExpression() == null) ? 0 : getExpression().hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Subscription other = (Subscription) obj;
        if (getTopic() == null) {
            if (other.getTopic() != null)
                return false;
        } else if (!getTopic().equals(other.getTopic()))
            return false;

        if (getExpression() == null) {
            if (other.getExpression() != null)
                return false;
        } else if (!getExpression().equals(other.getExpression()))
            return false;
        return true;
    }


    @Override
    public String toString() {
        return "SubscibeTopic [topic=" + getTopic() + ", expression=" + getExpression() + "]";
    }
}
