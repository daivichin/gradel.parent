package com.gradel.parent.component.web.entity;

import lombok.Data;

/**
 * ClassName: UMessage
 * Function:  UMessage
 * @date:      2020/1/12 下午2:01
 * Author     sdeven.chen.dongwei@gmail.com
 * Version    V1.0
 */
@Data
public class UMessage {
    public final static UMessage SUCCESS = new UMessage(0, "成功");
    public final static UMessage UNKNOWN_EXCEPTION = new UMessage(1, "系统异常:{0}");
    public final static UMessage VALID_BIND_EXCEPTION = new UMessage(2, "参数错误：{0}");
    public final static UMessage UN_MATCH_VALID_CODE = new UMessage(3, "验证码错误");
    public final static UMessage UN_MATCH_STATUS = new UMessage(4, "当前状态无权限操作");
    public final static UMessage NOT_SUPPORT = new UMessage(5, "不支持该操作！");
    public final static UMessage FAILED = new UMessage(6, "操作失败:{0}");

    private Integer code;
    private String message;

    public UMessage(Integer code, String message) {
        this.code = code;
        this.message = message.replaceAll("\\{\\d+\\}", "%s");
    }
}
