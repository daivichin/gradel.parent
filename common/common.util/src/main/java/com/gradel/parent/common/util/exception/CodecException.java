package com.gradel.parent.common.util.exception;

/**
* User: sdeven.chen.dongwei@gmail.com
 * @date: 2016/6/29
 * @Description:
 */
public class CodecException extends CommonRuntimeException {
    public CodecException() {
    }

    public CodecException(String message) {
        super(message);
    }

    public CodecException(String message, Throwable cause) {
        super(message, cause);
    }

    public CodecException(Throwable cause) {
        super(cause);
    }

    public CodecException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
