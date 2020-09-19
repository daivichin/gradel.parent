package com.gradel.parent.common.util.api.model;

import com.gradel.parent.common.util.api.constants.CommonApiConstants;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
* User: sdeven.chen.dongwei@gmail.com
 * @date: 2016-3-7
 * Depiction:分页查询结果
 */
@Data
public class ExtensionsPageResult<PD> extends CommonPageResult<PD> {

    /**
     * 扩展数据
     */
    private Map<String, Serializable> extensions = new HashMap<>();

    @Override
    public String toString() {
        String dataTemp = null;
        if(getData() != null){
            if(getData().size() <= CommonApiConstants.DEFAULT_MAX_LOG_DATA_SIZE) {
                dataTemp = getData().toString();
            }else{
                dataTemp = "[...]";
            }
        }
        return "ExtensionsPageResult(totalCount=" + getTotalCount() + ", data=" + dataTemp +", extensions_keys=" + toString(extensions) + ")";
    }


    private String toString(Map<String, Serializable> extensions) {
        if (extensions.size() <= 0){
            return "[]";
        }

        if(extensions.size() > CommonApiConstants.DEFAULT_MAX_LOG_DATA_SIZE){
            return "[...]";
        }

        StringBuilder sb = new StringBuilder();
        sb.append('[');

        for (Iterator<String> it = extensions.keySet().iterator();;) {
            sb.append(it.next());
            if (!it.hasNext()) {
                return sb.append(']').toString();
            }
            sb.append(',').append(' ');
        }
    }
}
