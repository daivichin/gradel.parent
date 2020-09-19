package com.gradel.parent.common.util.api.convert;


import com.gradel.parent.common.util.api.util.ConverterUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @date: 2016/4/14
 * Time: 9:12
 */
public abstract class Converter<T, E> {
    public abstract T convertFrom(E src);

    public List<T> convertList(List<E> srcList) {
        if (srcList == null || srcList.isEmpty()) {
            return new ArrayList<T>();
        }
        return ConverterUtil.convertList(srcList, this);
    }

    public boolean isValid(E obj) {
        return obj != null;
    }


}
