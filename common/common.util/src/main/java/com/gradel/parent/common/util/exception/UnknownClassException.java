package com.gradel.parent.common.util.exception;

import com.gradel.parent.common.util.api.error.CommonErrCodeEnum;

/**
* User: sdeven.chen.dongwei@gmail.com
 * @date: 2016-2-29
 * Depiction:加载class出错
 */
public class UnknownClassException extends SystemException{

    private static final long serialVersionUID = 7434775285630684831L;

    public UnknownClassException() {
        super(CommonErrCodeEnum.ERR_UNKNOWN_CLASS_ERROR);
    }

    public UnknownClassException(String msg) {
        super(CommonErrCodeEnum.ERR_UNKNOWN_CLASS_ERROR, msg);
    }

    public UnknownClassException(String msg, Throwable cause) {
        super(CommonErrCodeEnum.ERR_UNKNOWN_CLASS_ERROR, msg, cause);
    }

    public UnknownClassException(Throwable cause) {
        super(CommonErrCodeEnum.ERR_UNKNOWN_CLASS_ERROR, cause);
    }
}

