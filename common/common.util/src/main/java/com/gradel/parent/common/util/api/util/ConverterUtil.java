package com.gradel.parent.common.util.api.util;


import com.gradel.parent.common.util.api.common.CommonMuliPrimaryKeyRequest;
import com.gradel.parent.common.util.api.common.CommonPrimaryKeyRequest;
import com.gradel.parent.common.util.api.common.CommonRequest;
import com.gradel.parent.common.util.api.convert.Converter;
import com.gradel.parent.common.util.api.model.CommonPageResult;
import com.gradel.parent.common.util.api.model.PageParameter;
import com.gradel.parent.common.util.api.model.TPageParameter;
import com.gradel.parent.common.util.api.request.PageQueryRequest;
import com.gradel.parent.common.util.api.response.CommonResponse;
import com.gradel.parent.common.util.api.base.PrimaryKeyRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @author: sdeven.chen.dongwei@gmail.com Date: 16-4-14 Time: 9:04
 */
public class ConverterUtil {
    public static <T, E> List<T> convertList(List<E> srcList, Converter<T, E> converter) {
        List<T> list = new ArrayList<>();
        for (E element : srcList) {
            if (converter.isValid(element)) {
                list.add((T) converter.convertFrom(element));
            }
        }

        return list;
    }

    public static <T, E> List<T> convertListByFun(List<E> srcList, Function<E, T> converter) {
        List<T> list = new ArrayList<>();
        for (E element : srcList) {
            list.add((T) converter.apply(element));
        }

        return list;
    }

    public static int calculateTotalPage(int pageSize, int totalCount) {
        int totalPage = totalCount / pageSize + ((totalCount % pageSize == 0) ? 0 : 1);
        return totalPage;
    }

    /**
     * 转换成dao分页参数
     *
     * @param pageQueryRequest
     * @return
     */
    public static PageParameter convertToPageParameter(PageQueryRequest pageQueryRequest) {
        return convertToPageParameter(pageQueryRequest.getCurrentPage(), pageQueryRequest.getPageSize(), 0, 0);
    }

    /**
     * 转换成dao分页参数
     *
     * @param currentPage
     * @param pageSize
     * @return
     */
    public static PageParameter convertToPageParameter(int currentPage, int pageSize) {
        return convertToPageParameter(currentPage, pageSize, 0, 0);
    }

    /**
     * 转换成dao分页参数
     *
     * @param currentPage
     * @param pageSize
     * @return
     */
    public static PageParameter convertToPageParameter(int currentPage, int pageSize, int totalCount) {
        return convertToPageParameter(currentPage, pageSize, totalCount, calculateTotalPage(pageSize, totalCount));
    }

    /**
     * 转换成dao分页参数
     *
     * @param currentPage
     * @param pageSize
     * @return
     */
    public static PageParameter convertToPageParameter(int currentPage, int pageSize, int totalCount, int totalPage) {
        PageParameter pageParameter = new PageParameter();
        pageParameter.setCurrentPage(currentPage);
        pageParameter.setPageSize(pageSize);
        pageParameter.setTotalCount(totalCount);
        pageParameter.setTotalPage(totalPage);
        return pageParameter;
    }

    /**
     * 转换成dao分页参数
     *
     * @param pageQueryRequest
     * @return
     */
    public static TPageParameter convertToTPageParameter(PageQueryRequest pageQueryRequest) {
        return convertToTPageParameter(pageQueryRequest.getCurrentPage(), pageQueryRequest.getPageSize(), 0, 0);
    }

    public static TPageParameter convertToTPageParameter(int currentPage, int pageSize) {
        return convertToTPageParameter(currentPage, pageSize, 0, 0);
    }

    public static TPageParameter convertToTPageParameter(int currentPage, int pageSize, int totalCount) {
        return convertToTPageParameter(currentPage, pageSize, totalCount, calculateTotalPage(pageSize, totalCount));
    }

    public static TPageParameter convertToTPageParameter(int currentPage, int pageSize, int totalCount, int totalPage) {
        TPageParameter pageParameter = new TPageParameter();
        pageParameter.setCurrentPage(currentPage);
        pageParameter.setPageSize(pageSize);
        pageParameter.setTotalCount(totalCount);
        pageParameter.setTotalPage(totalPage);
        return pageParameter;
    }

    public static TPageParameter convertToPageParameter(PageParameter page) {
        TPageParameter pageParameter = new TPageParameter();
        if (page != null) {
            return convertToTPageParameter(page.getCurrentPage(), page.getPageSize(), page.getTotalCount(), page.getTotalPage());
        }
        return pageParameter;
    }

    public static PageParameter convertToPageParameter(TPageParameter page) {
        PageParameter pageParameter = new PageParameter();
        if (page != null) {
            return convertToPageParameter(page.getCurrentPage(), page.getPageSize(), page.getTotalCount(), page.getTotalPage());
        }
        return pageParameter;
    }

    /**
     * 转换成分页结果
     *
     * @param data
     * @param pageParameter
     * @param <T>
     * @return
     */
    public static <T> CommonPageResult<T> convertToPageResult(List<T> data, PageParameter pageParameter) {
        return convertToPageResult(data, pageParameter.getTotalCount(), pageParameter.getTotalPage());
    }

    /**
     * 转换成分页结果
     *
     * @param data
     * @param pageParameter
     * @param <T>
     * @return
     */
    public static <T> CommonPageResult<T> convertToPageResult(List<T> data, TPageParameter pageParameter) {
        return convertToPageResult(data, pageParameter.getTotalCount(), pageParameter.getTotalPage());
    }

    /**
     * 转换成分页结果
     *
     * @param srcPageResult
     * @param converter
     * @param <T>
     * @param <E>
     * @return
     */
    public static <T, E> CommonPageResult<T> convertToPageResult(CommonPageResult<E> srcPageResult,
                                                                 Converter<T, E> converter) {
        List<T> data = null;
        if (srcPageResult.getData() != null) {
            data = converter.convertList(srcPageResult.getData());
        }
        return convertToPageResult(data, srcPageResult.getTotalCount(), srcPageResult.getTotalPage());
    }

    /**
     * 转换成分页结果
     *
     * @param srcPageResult
     * @param converter
     * @param <T>
     * @param <E>
     * @return
     */
    public static <T, E> CommonPageResult<T> convertToPageResult(CommonPageResult<E> srcPageResult,
                                                                 Function<E, T> converter) {
        List<T> data = null;
        if (srcPageResult.getData() != null) {
            data = ConverterUtil.convertListByFun(srcPageResult.getData(), converter);
        }
        return convertToPageResult(data, srcPageResult.getTotalCount(), srcPageResult.getTotalPage());
    }

    /**
     * 转换成分页结果
     *
     * @param data
     * @param totalCount
     * @param totalPage
     * @param <T>
     * @return
     */
    public static <T> CommonPageResult<T> convertToPageResult(List<T> data, long totalCount, long totalPage) {
        CommonPageResult<T> pageResult = new CommonPageResult<T>();
        pageResult.setTotalCount(totalCount);
        pageResult.setTotalPage(totalPage);
        if (data != null) {
            pageResult.setData(data);
        }
        return pageResult;
    }

    /**
     * 转换成分页结果
     *
     * @param srcPageResult
     * @param <T>
     * @param <E>
     * @return
     */
    public static <T, E> CommonPageResult<T> convertToPageResult(CommonPageResult<E> srcPageResult, List<T> datas) {
        CommonPageResult<T> pageResult = new CommonPageResult<T>();
        pageResult.setTotalCount(srcPageResult.getTotalCount());
        pageResult.setTotalPage(srcPageResult.getTotalPage());
        pageResult.setData(datas);
        return pageResult;
    }

    /**
     * 结果转换
     *
     * @param srcResponse
     * @param converter
     * @param <T>
     * @param <E>
     * @return
     */
    public static <T, E> CommonResponse<T> convertToResult(CommonResponse<E> srcResponse, Converter<T, E> converter) {
        CommonResponse<T> response = new CommonResponse<T>();
        response.setErrorMsg(srcResponse.getErrorMsg());
        response.setErrorCode(srcResponse.getErrorCode());
        response.setResult(converter.convertFrom(srcResponse.getResult()));
        response.setSuccess(srcResponse.isSuccess());
        return response;
    }

    public static <T> CommonPrimaryKeyRequest<T> convertToPrimaryKeyRequest(T id, CommonRequest request) {
        CommonPrimaryKeyRequest keyRequest = new CommonPrimaryKeyRequest();
        keyRequest.setId(id);
        keyRequest.setCurrentUserId(request.getCurrentUserId());
        return keyRequest;
    }

    public static <T> CommonPrimaryKeyRequest<T> convertToPrimaryKeyRequest(T id, String currentMemberId) {
        CommonPrimaryKeyRequest keyRequest = new CommonPrimaryKeyRequest();
        keyRequest.setId(id);
        keyRequest.setCurrentUserId(currentMemberId);
        return keyRequest;
    }

    public static <T> CommonPrimaryKeyRequest<T> convertToPrimaryKeyRequest(PrimaryKeyRequest<T> idRequest, String currentMemberId) {
        CommonPrimaryKeyRequest keyRequest = new CommonPrimaryKeyRequest();
        keyRequest.setId(idRequest.getId());
        keyRequest.setCurrentUserId(currentMemberId);
        return keyRequest;
    }

    public static <T> CommonPrimaryKeyRequest<T> convertToPrimaryKeyRequest(PrimaryKeyRequest<T> idRequest, CommonRequest request) {
        CommonPrimaryKeyRequest keyRequest = new CommonPrimaryKeyRequest();
        keyRequest.setId(idRequest.getId());
        keyRequest.setCurrentUserId(request.getCurrentUserId());
        return keyRequest;
    }


    public static <T> CommonPrimaryKeyRequest<T> convertToPrimaryKeyRequest(T id) {
        CommonPrimaryKeyRequest keyRequest = new CommonPrimaryKeyRequest();
        keyRequest.setId(id);
        return keyRequest;
    }

    public static <T> CommonMuliPrimaryKeyRequest<T> convertToMuliPrimaryKeyRequest(List<T> ids,
                                                                                    CommonRequest request) {
        CommonMuliPrimaryKeyRequest keyRequest = new CommonMuliPrimaryKeyRequest();
        keyRequest.setIds(ids);
        keyRequest.setCurrentUserId(request.getCurrentUserId());
        return keyRequest;
    }

    public static <T> CommonMuliPrimaryKeyRequest<T> convertToMuliPrimaryKeyRequest(List<T> ids,
                                                                                    String currentMemberId) {
        CommonMuliPrimaryKeyRequest keyRequest = new CommonMuliPrimaryKeyRequest();
        keyRequest.setIds(ids);
        keyRequest.setCurrentUserId(currentMemberId);
        return keyRequest;
    }
}
