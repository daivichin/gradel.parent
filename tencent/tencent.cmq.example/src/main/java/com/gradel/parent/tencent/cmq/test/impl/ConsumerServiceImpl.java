package com.gradel.parent.tencent.cmq.test.impl;

import com.alibaba.fastjson.TypeReference;

import com.gradel.parent.common.util.api.model.BaseMessage;
import com.gradel.parent.common.util.util.JsonUtil;
import com.gradel.parent.tencent.cmq.api.Action;
import com.gradel.parent.tencent.cmq.api.CMQMessageListener;
import com.gradel.parent.tencent.cmq.api.exception.SerializationException;
import com.gradel.parent.tencent.cmq.api.model.CMQMessageBody;
import com.gradel.parent.tencent.cmq.api.model.MessageContext;
import com.gradel.parent.tencent.cmq.test.quickstart.MsgBody;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.lang.reflect.Type;
import java.util.Date;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @date: 2017-12-15
 * @Description:
 */
public class ConsumerServiceImpl implements CMQMessageListener<MsgBody> {

    final static Type MESSAGEBODY_TYPE = new TypeReference<CMQMessageBody<MsgBody>>() {
    }.getType();

    @Override
    public Action consume(BaseMessage<MsgBody> message, MessageContext context) {
        try {
            System.out.printf("DequeueCount:%d, startTime:%s, Receive New Message:%s%n", context.getDequeueCount(), DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"), message);
        } catch (Exception e) {
            e.printStackTrace();
            return Action.ReconsumeLater;
        }
        return Action.ReconsumeLater;
    }

    @Override
    public BaseMessage<MsgBody> deserialize(String json) throws SerializationException {
        //1.json方式---统一使用json方式
        BaseMessage<MsgBody> body = JsonUtil.fromJson(json, MESSAGEBODY_TYPE);

        //2.protostuff方式
        //Object unserialize = SerializerUtil.unserialize(bytes);


        return body;
    }
}
