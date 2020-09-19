package com.gradel.parent.tencent.cmq.api.impl;

import com.gradel.parent.tencent.cmq.api.enums.ErrorCodeEnum;
import com.gradel.parent.tencent.cmq.api.model.SendResult;
import com.gradel.parent.tencent.cmq.api.util.ClientLogger;
import com.gradel.parent.tencent.qcloud.cmq.CMQClientException;
import com.gradel.parent.tencent.qcloud.cmq.CMQServerException;
import com.gradel.parent.common.util.util.ExceptionUtil;
import org.slf4j.Logger;


public class DefaultSendMessage {

    private final static Logger log = ClientLogger.getLog();

    private CMQClientRemoteAPI cmqClientRemoteAPI;

    public DefaultSendMessage(CMQConfigAbstract cmqConfig) {
        this.cmqClientRemoteAPI = new CMQClientRemoteAPI(cmqConfig);
    }

    public SendResult<String> sendMessage(String queueName, String msgBody, int delayTime, int connectTimeoutMilliseconds, int readTimeoutMilliseconds) {
        SendResult<String> sendResult = null;
        try {
//            long startTime = System.currentTimeMillis();
            sendResult = cmqClientRemoteAPI.sendMessageByParam(queueName, msgBody, delayTime, connectTimeoutMilliseconds, readTimeoutMilliseconds);
            if (!sendResult.isSuccess()) {
                Object[] params = new Object[]{queueName, msgBody, delayTime, sendResult.getErrCode(), sendResult.getErrMsg(), sendResult.getRequestId()};
                log.error("SendMessage failure, queueName:{}, body:{}, delayTime:{}, errCode:{}, errMsg:{}, requestId:{}", params);
            }
//            log.info("SendMessage to queue[{}], message size:[{}], costTime:[{}]ms", queueName, msgBody.length(), (System.currentTimeMillis() - startTime));
        } catch (Throwable e) {
            sendResult = new SendResult();
            if (e instanceof CMQServerException) {
                CMQServerException e1 = (CMQServerException) e;
                sendResult.setErrCode(e1.getErrorCode());
                sendResult.setErrMsg(e1.getErrorMessage());
                sendResult.setRequestId(e1.getRequestId());
                Object[] params = new Object[]{queueName, msgBody, delayTime, sendResult.getErrCode(), sendResult.getErrMsg(), sendResult.getRequestId()};
                log.error("SendMessage failure, queueName:{}, body:{}, delayTime:{}, errCode:{}, errMsg:{}, requestId:{}", params);
            } else if (e instanceof CMQClientException) {
                sendResult.setErrCode(ErrorCodeEnum.FAILURE_MSG_LEN_MAX.getCode());
                sendResult.setErrMsg(ErrorCodeEnum.FAILURE_MSG_LEN_MAX.getDesc());
                Object[] params = new Object[]{queueName, msgBody, delayTime, sendResult.getErrCode(), sendResult.getErrMsg(), sendResult.getRequestId()};
                log.error("SendMessage failure, queueName:{}, body:{}, delayTime:{}, errCode:{}, errMsg:{}, requestId:{}", params);
            } else {
                Object[] params = new Object[]{queueName, msgBody, delayTime, connectTimeoutMilliseconds, readTimeoutMilliseconds, ExceptionUtil.getAsString(e)};
                log.error("SendMessage exception, queueName:{}, body:{}, delayTime:{}, connectTimeoutMill:{}, readTimeoutMill:{}, Exception:{}", params);
            }
        }/* finally {
        }*/
        return sendResult;
    }

}
