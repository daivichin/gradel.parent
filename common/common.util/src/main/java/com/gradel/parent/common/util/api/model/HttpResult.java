package com.gradel.parent.common.util.api.model;

import lombok.Data;
import lombok.ToString;

/**
* User: sdeven.chen.dongwei@gmail.com
 * @date: 2016/7/4
 * @Description:
 */
@Data
@ToString
public class HttpResult<T> {
    /**
     * 状态码
     */
    private Integer status;
    /**
     * 返回数据
     */
    private T data;

    /**
     * 当 status == 200 时，则为true
     */
    private boolean success;

    private static final HttpResult TEMP_HTTPRESULT = HttpResult.create(0, null);

    public HttpResult() {
    }

    private HttpResult(Integer status, T data) {
        this.status = status;
        this.data = data;
        success = (status != null && status == 200);
    }

    public static <T> HttpResult<T> create(Integer status, T data) {
        return new HttpResult(status, data);
    }

    public static <T> HttpResult<T> failure() {
        return TEMP_HTTPRESULT;
    }
}
