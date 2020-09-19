package com.gradel.parent.common.util.exception;

import com.gradel.parent.common.util.api.error.CommonErrCodeEnum;

/**
* User: sdeven.chen.dongwei@gmail.com
 * @date: 2016/2/27
 * @Description:所有数据库操作异常
 */
public class DataBaseOperateException extends SystemException {

    public DataBaseOperateException() {
        super(CommonErrCodeEnum.DATASOURCE_OPERTE_ERROR);
    }

    public DataBaseOperateException(String msg) {
        super(CommonErrCodeEnum.DATASOURCE_OPERTE_ERROR, msg);
    }

    public DataBaseOperateException(String msg, Throwable cause) {
        super(CommonErrCodeEnum.DATASOURCE_OPERTE_ERROR, msg, cause);
    }

    public DataBaseOperateException(Throwable cause) {
        super(CommonErrCodeEnum.DATASOURCE_OPERTE_ERROR, cause);
    }
}
