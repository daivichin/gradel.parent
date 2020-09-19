package com.gradel.parent.common.util.api.enums;

/**
* User: sdeven.chen.dongwei@gmail.com
 * @date: 2016/6/12
 * @Description:
 */
public interface BaseEnum<T> {
    /**
     * 编码
     * @return
     */
    T getCode();

    /**
     * 描述
     * @return
     */
    String getDesc();
}
