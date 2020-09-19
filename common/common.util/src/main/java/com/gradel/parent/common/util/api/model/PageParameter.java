package com.gradel.parent.common.util.api.model;

import com.gradel.parent.common.util.api.constants.CommonApiConstants;
import lombok.Builder;

import java.io.Serializable;

/**
 * 分页参数类
 */
@Builder
public class PageParameter implements Serializable {

    private static final long serialVersionUID = 7778954621301304548L;

    public static final int DEFAULT_PAGE_SIZE = 10;

    private int pageSize;
    private int currentPage;
    private int totalPage;
    private int totalCount;

    public PageParameter() {
        this.currentPage = 1;
        this.pageSize = DEFAULT_PAGE_SIZE;
    }

    /**
     * 
     * @param currentPage
     * @param pageSize
     */
    public PageParameter(int currentPage, int pageSize) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }

    public PageParameter(int pageSize, int currentPage, int totalPage, int totalCount) {
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
        return this.totalPage;
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

        PageParameter that = (PageParameter) o;

        if (pageSize != that.pageSize) {
            return false;
        }
        if (currentPage != that.currentPage) {
            return false;
        }
        if (totalPage != that.totalPage) {
            return false;
        }
        return totalCount == that.totalCount;

    }

    @Override
    public int hashCode() {
        int result = pageSize;
        result = 31 * result + currentPage;
        result = 31 * result + totalPage;
        result = 31 * result + totalCount;
        return result;
    }

    @Override
    public String toString() {
        return "PageParameter{" +
                "pageSize=" + pageSize +
                ", currentPage=" + currentPage +
                ", totalPage=" + totalPage +
                ", totalCount=" + totalCount +
                '}';
    }
}
