package com.gradel.parent.tencent.cmq.api.model;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class SendResult<T> {
    private String requestId;
    private String queue;

    private T data;
    private int errCode;
    private String errMsg;
    private boolean success;

    public SendResult() {
    }

    public SendResult(int errCode, String errMsg, boolean success) {
        this.errCode = errCode;
        this.errMsg = errMsg;
        this.success = success;
    }

    public SendResult(String requestId, String queue, int errCode, String errMsg, boolean success) {
        this.requestId = requestId;
        this.queue = queue;
        this.errCode = errCode;
        this.errMsg = errMsg;
        this.success = success;
    }

    public static SendResult newInstance(int errCode, String errMsg){
        SendResult instance = new SendResult();
        instance.setErrCode(errCode);
        instance.setErrMsg(errMsg);
        if(errCode == 0){
            instance.setSuccess(true);
        }
        return instance;
    }
}
