package com.gradel.parent.ali.rocketmq.exception;


import com.gradel.parent.common.util.api.error.CommonErrCodeEnum;
import com.gradel.parent.common.util.exception.SystemException;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @date: 2017-11-23
 * @Description:
 */
public class SerializationException extends SystemException {

    public SerializationException() {
        super(CommonErrCodeEnum.OBJECT_SERIALIZATION_ERROR);
    }

    public SerializationException(String msg) {
        super(CommonErrCodeEnum.OBJECT_SERIALIZATION_ERROR, msg);
    }

    public SerializationException(String msg, Throwable cause) {
        super(CommonErrCodeEnum.OBJECT_SERIALIZATION_ERROR, msg, cause);
    }

    public SerializationException(Throwable cause) {
        super(CommonErrCodeEnum.ERR_ALI_SEARCH_ERROR, cause);
    }
}
