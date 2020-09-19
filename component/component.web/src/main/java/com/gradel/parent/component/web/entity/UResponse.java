package com.gradel.parent.component.web.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * ClassName: UResponse
 * Function:  UResponse
 * @date:      2020/1/12 下午2:01
 * Author     sdeven.chen.dongwei@gmail.com
 * Version    V1.0
 */
@Data
public class UResponse<T> implements Serializable {
    private static final long serialVersionUID = -4505655308965878999L;
    //返回数据
    private T data;
    //返回码
    private Integer code;
    //返回描述
    private String message;

    public UResponse(T data, UMessage message, Object... args){
        this.code = message.getCode();
        this.message = String.format(message.getMessage(), args);
        this.data = data;
    }
}
