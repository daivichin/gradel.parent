package com.gradel.parent.common.util.exception;


import com.gradel.parent.common.util.api.enums.ErrorCodeEnum;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @date: 2016/2/27
 * @Description:
 */
public class ValidationException extends SystemException {

    public ValidationException(String errReason) {
        super(ErrorCodeEnum.ERR_CHECK_PARAM.getErrorCode(), errReason);
    }
}
