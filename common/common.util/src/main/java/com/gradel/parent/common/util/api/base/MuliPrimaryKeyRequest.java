package com.gradel.parent.common.util.api.base;

import java.util.List;

/**
* User: sdeven.chen.dongwei@gmail.com
 * @date: 2016/7/15
 * @Description:
 */
public interface MuliPrimaryKeyRequest<T> {
    List<T> getIds();
}
