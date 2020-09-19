package com.gradel.parent.common.util.api.model;

import com.gradel.parent.common.util.api.constants.CommonApiConstants;
import lombok.Builder;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * wx:sdeven.chen.dongwei@gmail.com
 * Time: 2017/3/17 17:18.
 * 分页参数类
 *
 */
@Builder
public class TPageParameter implements Serializable {
    private static final long serialVersionUID = -2050903899112211502L;

    public static final int DEFAULT_PAGE_SIZE = 10;

    private int pageSize;
    private int currentPage;
    private int totalPage;
    private int totalCount;

    public TPageParameter() {
        this.currentPage = 1;
        this.pageSize = DEFAULT_PAGE_SIZE;
    }

    /**
     *
     * @param currentPage
     * @param pageSize
     */
    public TPageParameter(int currentPage, int pageSize) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }

    public TPageParameter(int pageSize, int currentPage, int totalPage, int totalCount) {
        this.pageSize = pageSize;
        this.currentPage = currentPage;
        this.totalPage = totalPage;
        this.totalCount = totalCount;
    }

    public int getCurrentPage() {
        return currentPage <= 0 ?1 : currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return this.pageSize <= 0 ? CommonApiConstants.DEFAULT_PAGE_SIZE : Math.min(this.pageSize, CommonApiConstants.DEFAULT_MAX_PAGE_SIZE);
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TPageParameter that = (TPageParameter) o;
        return pageSize == that.pageSize &&
                currentPage == that.currentPage &&
                totalPage == that.totalPage &&
                totalCount == that.totalCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pageSize, currentPage, totalPage, totalCount);
    }

    @Override
    public String toString() {
        return "TPageParameter{" +
                "pageSize=" + pageSize +
                ", currentPage=" + currentPage +
                ", totalPage=" + totalPage +
                ", totalCount=" + totalCount +
                '}';
    }
}
