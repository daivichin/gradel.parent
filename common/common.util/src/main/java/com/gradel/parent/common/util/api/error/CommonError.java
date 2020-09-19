package com.gradel.parent.common.util.api.error;

/**
* User: sdeven.chen.dongwei@gmail.com
 * @date: 2016/6/12
 * @Description:
 */
public interface CommonError {
    /**
     * 错误码
     * @return
     */
    public int getErrorCode();

    /**
     * 错误描述
     * @return
     */
    public String getErrorDesc();
}
