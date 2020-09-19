package com.gradel.parent.common.util.api.response;

import com.gradel.parent.common.util.api.error.CommonError;

/**
* User: sdeven.chen.dongwei@gmail.com
 * @date: 2018-01-24
 * @Description: 不可改变的CommonResponse
 */
public class ImmutableCommonResponse<T> extends CommonResponse<T> {

    private volatile boolean hasSet = false;

    public ImmutableCommonResponse(T result, int errorCode, String errorMsg) {
        super(result, errorCode, errorMsg);
        hasSet = true;
    }

    public ImmutableCommonResponse(T result, CommonError error) {
        super(result, error);
        hasSet = true;
    }

    @Override
    public void setError(int errorCode, String errorMsg) {
        if(hasSet){
            throw new UnsupportedOperationException();
        }
        super.setError(errorCode, errorMsg);
    }

    @Override
    public void setError(CommonError error) {
        if(hasSet){
            throw new UnsupportedOperationException();
        }
        super.setError(error);
    }

    @Override
    public void setResult(T result) {
        if(hasSet){
            throw new UnsupportedOperationException();
        }
        super.setResult(result);
    }

    @Override
    public void setErrorCode(int errorCode) {
        if(hasSet){
            throw new UnsupportedOperationException();
        }
        super.setErrorCode(errorCode);
    }

    @Override
    public void setErrorMsg(String errorMsg) {
        if(hasSet){
            throw new UnsupportedOperationException();
        }
        super.setErrorMsg(errorMsg);
    }

    @Override
    public void setSuccess(boolean success) {
        if(hasSet){
            throw new UnsupportedOperationException();
        }
        super.setSuccess(success);
    }
};
