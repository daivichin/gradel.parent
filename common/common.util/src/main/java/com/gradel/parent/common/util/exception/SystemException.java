package com.gradel.parent.common.util.exception;

import com.gradel.parent.common.util.api.error.CommonErrCodeEnum;
import com.gradel.parent.common.util.api.error.CommonError;
import lombok.Getter;

/**
* User: sdeven.chen.dongwei@gmail.com
 * @date: 2016-2-29
 * Depiction:系统异常
 */
public class SystemException extends CommonRuntimeException {

    private static final long serialVersionUID = -8998707909342242357L;
    @Getter
    private int errCode = CommonErrCodeEnum.ERR_UNKNOW_ERROR.getErrorCode();

    @Getter
    private String errReason = "";

    public SystemException() {
        super("[" + CommonErrCodeEnum.ERR_UNKNOW_ERROR.getErrorCode() + "]" + CommonErrCodeEnum.ERR_UNKNOW_ERROR.getErrorDesc());
        this.errCode = CommonErrCodeEnum.ERR_UNKNOW_ERROR.getErrorCode();
        this.errReason = CommonErrCodeEnum.ERR_UNKNOW_ERROR.getErrorDesc();
    }

    public SystemException(String msg) {
        super(msg);
        this.errCode = CommonErrCodeEnum.ERR_UNKNOW_ERROR.getErrorCode();
        this.errReason = CommonErrCodeEnum.ERR_UNKNOW_ERROR.getErrorDesc();
    }

    public SystemException(String msg, Throwable cause) {
        super(msg, cause);
        this.errCode = CommonErrCodeEnum.ERR_UNKNOW_ERROR.getErrorCode();
        this.errReason = CommonErrCodeEnum.ERR_UNKNOW_ERROR.getErrorDesc();
    }

    public SystemException(Throwable cause) {
        super(cause);
        this.errCode = CommonErrCodeEnum.ERR_UNKNOW_ERROR.getErrorCode();
        this.errReason = CommonErrCodeEnum.ERR_UNKNOW_ERROR.getErrorDesc();
    }

    public SystemException(int errCode, String errReason) {
        super("[" + errCode + "]" + errReason);
        this.errCode = errCode;
        this.errReason = errReason;
    }

    public SystemException(int errCode, String errReason, String msg) {
        super(msg);
        this.errCode = errCode;
        this.errReason = errReason;
    }

    public SystemException(int errCode, String errReason, String msg, Throwable cause) {
        super(msg, cause);
        this.errCode = errCode;
        this.errReason = errReason;
    }

    public SystemException(int errCode, String errReason, Throwable cause) {
        super(cause);
        this.errCode = errCode;
        this.errReason = errReason;
    }

    public SystemException(CommonError commonError) {
        this(commonError.getErrorCode(), commonError.getErrorDesc());
    }

    public SystemException(CommonError commonError, String msg) {
        this(commonError.getErrorCode(), commonError.getErrorDesc(), msg);
    }

    public SystemException(CommonError commonError, String msg, Throwable cause) {
        this(commonError.getErrorCode(), commonError.getErrorDesc(), msg, cause);
    }

    public SystemException(CommonError commonError, Throwable cause) {
        this(commonError.getErrorCode(), commonError.getErrorDesc(), cause);
    }

    @Override
    public String getMessage() {
        return String.format("[%s]%s.", getErrCode(), getErrReason());
    }
}

