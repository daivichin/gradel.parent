package com.gradel.parent.common.util.exception;


import com.gradel.parent.common.util.api.error.CommonErrCodeEnum;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @date: 2016-2-29
 * Depiction:加载配置出错
 */
public class ConfigurationException extends SystemException{

    private static final long serialVersionUID = -8021168803375567021L;

    public ConfigurationException() {
        super(CommonErrCodeEnum.ERR_LOAD_CONFIG_ERROR);
    }

    public ConfigurationException(String msg) {
        super(CommonErrCodeEnum.ERR_LOAD_CONFIG_ERROR, msg);
    }

    public ConfigurationException(String msg, Throwable cause) {
        super(CommonErrCodeEnum.ERR_LOAD_CONFIG_ERROR, msg, cause);
    }

    public ConfigurationException(Throwable cause) {
        super(CommonErrCodeEnum.ERR_LOAD_CONFIG_ERROR, cause);
    }
}

