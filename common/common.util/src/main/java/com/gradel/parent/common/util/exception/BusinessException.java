package com.gradel.parent.common.util.exception;


import com.gradel.parent.common.util.api.enums.ErrorCodeEnum;
import com.gradel.parent.common.util.api.error.CommonError;

public class BusinessException extends SystemException {

    public BusinessException(String errReason) {
        super(ErrorCodeEnum.BEAN_BIZ_ERROR.getErrorCode(), errReason);
    }
    public BusinessException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public BusinessException(Throwable cause) {
        super(cause);
    }

    public BusinessException(int errCode, String errReason) {
        super(errCode, errReason);
    }

    public BusinessException(int errCode, String errReason, String msg) {
        super(errCode, errReason, msg);
    }

    public BusinessException(int errCode, String errReason, String msg, Throwable cause) {
        super(errCode, errReason, msg, cause);
    }

    public BusinessException(int errCode, String errReason, Throwable cause) {
        super(errCode, errReason, cause);
    }

    public BusinessException(CommonError commonError) {
        super(commonError);
    }

    public BusinessException(CommonError commonError, String msg) {
        super(commonError, msg);
    }

    public BusinessException(CommonError commonError, String msg, Throwable cause) {
        super(commonError, msg, cause);
    }

    public BusinessException(CommonError commonError, Throwable cause) {
        super(commonError, cause);
    }
}
