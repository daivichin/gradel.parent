package com.gradel.parent.component.jwt.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 不能注册异常
 */
public class ANotRegisterException extends AuthenticationException {
    private String registeration;

    public ANotRegisterException(String registeration, String msg) {
        super(msg);
        this.registeration = registeration;
    }

    public String getRegisterCode() {
        return  registeration;
    }
}
