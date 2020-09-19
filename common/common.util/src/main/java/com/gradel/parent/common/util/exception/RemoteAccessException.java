package com.gradel.parent.common.util.exception;

import com.gradel.parent.common.util.api.error.CommonErrCodeEnum;

/**
* User: sdeven.chen.dongwei@gmail.com
 * @date: 2016-2-29
 * Depiction:远程访问出错
 */
public class RemoteAccessException extends SystemException{

    private static final long serialVersionUID = -8998707909342242357L;

    public RemoteAccessException() {
        super(CommonErrCodeEnum.ERR_REMOTE_ACCESS_ERROR);
    }

    public RemoteAccessException(String msg) {
        super(CommonErrCodeEnum.ERR_REMOTE_ACCESS_ERROR, msg);
    }

    public RemoteAccessException(String msg, Throwable cause) {
        super(CommonErrCodeEnum.ERR_REMOTE_ACCESS_ERROR, msg, cause);
    }

    public RemoteAccessException(Throwable cause) {
        super(CommonErrCodeEnum.ERR_REMOTE_ACCESS_ERROR, cause);
    }
}

