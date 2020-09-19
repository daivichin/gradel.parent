package com.gradel.parent.common.util.api.request;

import com.gradel.parent.common.util.api.common.CommonRequest;
import com.gradel.parent.common.util.api.constants.CommonApiConstants;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
* User: sdeven.chen.dongwei@gmail.com
 * @date: 2016-3-7
 * Depiction: 分页查询请求，如需分页查询，需要继承该类
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PageQueryRequest extends CommonRequest implements Serializable {

    private static final long serialVersionUID = 5750491879573083249L;
    /**
     * currentPage 当前页
     */
    @Setter
    private Integer currentPage;

    /**
     * pageSize 每页显示的条数
     */
    @Setter
    private Integer pageSize;

    public Integer getCurrentPage(){
        return this.currentPage == null || this.currentPage <= 0 ? CommonApiConstants.DEFAULT_CURRENT_PAGE : this.currentPage;
    }

    /**
     * 默认为10，最大值为100，防止被外界乱调用
     * @return
     */
    public Integer getPageSize(){
        return this.pageSize == null || this.pageSize <= 0 ? CommonApiConstants.DEFAULT_PAGE_SIZE : Math.min(this.pageSize, CommonApiConstants.DEFAULT_MAX_PAGE_SIZE);
    }
}
