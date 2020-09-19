package com.gradel.parent.common.util.api.response;

import com.gradel.parent.common.util.api.constants.CommonApiConstants;
import com.gradel.parent.common.util.api.error.CommonError;

import java.io.Serializable;
import java.util.Collection;

/**
* User: sdeven.chen.dongwei@gmail.com
 * @date: 2016-3-7
 * Depiction: 响应参数基类
 */
public class CommonResponse<T> implements Serializable {

    private static final long serialVersionUID = -655403293051655566L;

    private T result;  //获取调用返回值

    private int errorCode; //获取错误码

    private String errorMsg;

    private boolean success = false;  //表示为true表示getResult--》不为空,如果为false,则调用errorCode/errorMsg来获取出错信息

    public CommonResponse() {
    }

    public CommonResponse(T result) {
        setResult(result);
    }

    public CommonResponse(int errorCode, String errorMsg) {
        setErrorCode(errorCode);
        setErrorMsg(errorMsg);
    }

    public CommonResponse(T result, int errorCode, String errorMsg) {
        setErrorCode(errorCode);
        setErrorMsg(errorMsg);
        setResult(result);
    }

    public CommonResponse(T result, CommonError error) {
        setError(error);
        setResult(result);
    }

    public void setError(int errorCode, String errorMsg) {
        setErrorCode(errorCode);
        setErrorMsg(errorMsg);
    }

    public void setError(CommonError error) {
        setError(error.getErrorCode(), error.getErrorDesc());
    }

    public void setResult(T result) {
        if (result != null) {
            success = true;
            this.result = result;
        } else {
            success = false;
        }
    }

    public T getResult() {
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CommonResponse response = (CommonResponse) o;
        if (errorCode != response.errorCode) {
            return false;
        }
        if (!errorMsg.equals(response.errorMsg)) {
            return false;
        }
        if (!result.equals(response.result)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result1 = 1;
        result1 = 31 * result1 + result.hashCode();
        result1 = 31 * result1 + errorCode;
        result1 = 31 * result1 + errorMsg.hashCode();
        return result1;
    }

    @Override
    public String toString() {
        String resultTemp = null;
        if (getResult() != null) {
            if (getResult() instanceof Collection && ((Collection) getResult()).size() > CommonApiConstants.DEFAULT_MAX_LOG_DATA_SIZE) {
                resultTemp = getResult().getClass().getSimpleName() + "[...]";
            } else {
                resultTemp = getResult().toString();
            }
        }
        return toSimpleString(resultTemp);
    }

    private String toSimpleString(String resultTemp) {
        return "CommonResponse{" +
                "result=" + resultTemp +
                ", errorCode='" + errorCode + '\'' +
                ", errorMsg='" + errorMsg + '\'' +
                ", success=" + success +
                '}';
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}