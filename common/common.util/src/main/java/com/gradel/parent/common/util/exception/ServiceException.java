package com.gradel.parent.common.util.exception;


import com.gradel.parent.common.util.api.error.CommonErrCodeEnum;
import com.gradel.parent.common.util.api.error.CommonError;

/**
* User: sdeven.chen.dongwei@gmail.com
 * @date: 2016/2/27
 * @Description:（业务）服务异常
 */
public class ServiceException extends SystemException {

    public ServiceException() {
        super(CommonErrCodeEnum.BEAN_BIZ_ERROR);
    }

    public ServiceException(String msg) {
        super(CommonErrCodeEnum.BEAN_BIZ_ERROR, msg);
    }

    public ServiceException(String msg, Throwable cause) {
        super(CommonErrCodeEnum.BEAN_BIZ_ERROR, msg, cause);
    }

    public ServiceException(Throwable cause) {
        super(CommonErrCodeEnum.BEAN_BIZ_ERROR, cause);
    }

    public ServiceException(int errCode, String errReason) {
        super(errCode, errReason);
    }

    public ServiceException(int errCode, String errReason, String msg) {
        super(errCode, errReason, msg);
    }

    public ServiceException(int errCode, String errReason, String msg, Throwable cause) {
        super(errCode, errReason, msg, cause);
    }

    public ServiceException(int errCode, String errReason, Throwable cause) {
        super(errCode, errReason, cause);
    }

    public ServiceException(CommonError commonError) {
        super(commonError);
    }

    public ServiceException(CommonError commonError, String msg) {
        super(commonError, msg);
    }

    public ServiceException(CommonError commonError, String msg, Throwable cause) {
        super(commonError, msg, cause);
    }

    public ServiceException(CommonError commonError, Throwable cause) {
        super(commonError, cause);
    }
}
