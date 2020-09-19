package com.gradel.parent.common.util.exception;


import com.gradel.parent.common.util.api.error.CommonErrCodeEnum;

/**
* User: sdeven.chen.dongwei@gmail.com
 * @date: 2016-2-29
 * Depiction:远程访问超时
 */
public class RemoteAccessTimeOutException extends SystemException{

    private static final long serialVersionUID = 3673505993449685753L;

    public RemoteAccessTimeOutException() {
        super(CommonErrCodeEnum.ERR_REMOTE_ACCESS_TIMEOUT_ERROR);
    }

    public RemoteAccessTimeOutException(String msg) {
        super(CommonErrCodeEnum.ERR_REMOTE_ACCESS_TIMEOUT_ERROR, msg);
    }

    public RemoteAccessTimeOutException(String msg, Throwable cause) {
        super(CommonErrCodeEnum.ERR_REMOTE_ACCESS_TIMEOUT_ERROR, msg, cause);
    }

    public RemoteAccessTimeOutException(Throwable cause) {
        super(CommonErrCodeEnum.ERR_REMOTE_ACCESS_TIMEOUT_ERROR, cause);
    }
}

