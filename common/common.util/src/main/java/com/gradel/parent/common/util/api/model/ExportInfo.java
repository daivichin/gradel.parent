package com.gradel.parent.common.util.api.model;

import com.gradel.parent.common.util.api.common.CommonRequest;
import com.gradel.parent.common.util.api.constants.CommonApiConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * wx:sdeven.chen.dongwei@gmail.com
 * @date:2015/11/10 11:16
 * 导出 基本信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExportInfo<D> extends CommonRequest implements Serializable {

    private static final long serialVersionUID = -2013871710341290844L;
    /**
     * dataList 数据列表
     */
    private List<D> dataList;

    /**
     * sheetNameList sheet名称列表
     */
    private List sheetNameList;

    /**
     * beanParams bean 参数
     */
    private Map beanParams;

    /**
     * beanName bean名称
     */
    private String beanName;

    /**
     * startSheetNum SheetNo从startSheetNum开始
     */
    private int startSheetNum=0;

    /**
     * tplFileName 模板文件名称
     * 例如/WEB-INF/tpl/acctTypeTrade.xls ,那么 tplFileName 就是：acctTypeTrade
     */
    private String tplFileName;

    /**
     * 导出文件的名称
     */
    private String outFileName;

    /**
     * multiple 是否导出多个sheet
     */
    private boolean multiple = true;

    /**
     * worBookProcessor 自定义Excel处理器
     */
    private Object worBookProcessor;

    @Override
    public String toString() {
        String dataListTemp = null;
        if(getDataList() != null){
            if(getDataList().size() <= CommonApiConstants.MAX_LOG_DATA_SIZE) {
                dataListTemp = getDataList().toString();
            }else{
                dataListTemp = "...";
            }
        }
        return "ExportInfo(dataList=" + dataListTemp + ", sheetNameList=" + getSheetNameList() + ", beanParams=" + getBeanParams() + ", beanName=" + getBeanName() + ", startSheetNum=" + getStartSheetNum() + ", tplFileName=" + getTplFileName() + ", outFileName=" + getOutFileName() + ", multiple=" + isMultiple() + ")";
    }
}
