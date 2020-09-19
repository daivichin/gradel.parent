package com.gradel.parent.common.util.exception;

import com.gradel.parent.common.util.api.error.CommonErrCodeEnum;

/**
* User: sdeven.chen.dongwei@gmail.com
 * @date: 2016-2-29
 * Depiction:实例化class出错
 */
public class InstantiationObjectException extends SystemException{

    private static final long serialVersionUID = -8320463974695997038L;

    public InstantiationObjectException() {
        super(CommonErrCodeEnum.ERR_INSTANTIATION_ERROR);
    }

    public InstantiationObjectException(String msg) {
        super(CommonErrCodeEnum.ERR_INSTANTIATION_ERROR, msg);
    }

    public InstantiationObjectException(String msg, Throwable cause) {
        super(CommonErrCodeEnum.ERR_INSTANTIATION_ERROR, msg, cause);
    }

    public InstantiationObjectException(Throwable cause) {
        super(CommonErrCodeEnum.ERR_INSTANTIATION_ERROR, cause);
    }
}

