package com.gradel.parent.component.web.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ClassName: UException
 * Function:  UException
 * @date:      2019/6/18 下午2:03
 * Author     sdeven.chen.dongwei@gmail.com
 * Version    V1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UException extends RuntimeException {//AuthenticationException
    private static final long serialVersionUID = -6370612186038915645L;

    private Integer code;
    private Object data;

    public UException(Object data, UMessage message, Object... args) {
        super(String.format(message.getMessage(), args));

        this.setData(data);
        this.setCode(message.getCode());
    }
}
