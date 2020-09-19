package com.gradel.parent.common.util.api.model;

import com.gradel.parent.common.util.api.constants.CommonApiConstants;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
* User: sdeven.chen.dongwei@gmail.com
 * @date: 2016-3-7
 * Depiction:分页查询结果
 *
 * @see ExtensionsPageResult
 */
@Data
public class CommonPageResult<PD> implements Serializable {

    private static final long serialVersionUID = -6790824298186846296L;
    /**
     * 接口返回信息 总记录数
     */
    private long totalCount=0;
    /**
     * 接口返回信息 总页数
     */
    private long totalPage=0;
    /**
     * 返回的数据集
     */
    private List<PD> data = new ArrayList<PD>();

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
        return "CommonPageResult(totalCount=" + getTotalCount() + ", data=" + dataTemp + ")";
    }
}
