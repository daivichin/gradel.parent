package com.gradel.parent.common.util.api.model;

import com.gradel.parent.common.util.api.common.CommonRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * wx:sdeven.chen.dongwei@gmail.com
 * @date:2015/11/12 9:48
 * 查询导出Excel 请求参数
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
public abstract class ExportRequest<R extends CommonRequest> extends CommonRequest implements Serializable {
    private static final long serialVersionUID = -2311444081611382751L;

    /**
     * excel 表达式中根级的name
     */
    private String beanName;

    /**
     * tplFileName 模板文件名称
     */
    private String tplFileName;

    /**
     * outFileName 导出文件的名称
     */
    private String outFileName;

    public R commonRequest;
}
