package com.gradel.parent.common.util.api.interfaces;

/**
* User: sdeven.chen.dongwei@gmail.com
 * @date: 2016/12/15
 * @Description: 从文件读取java对象回调接口
 */
@FunctionalInterface
public interface ReadCallBack<T> {
    /**
     * 回调
     * @param data 读到的数据
     * @param hasNext 是否还有数据T
     */
    void callBack(T data, boolean hasNext);
}
