package com.gradel.parent.common.util.date;

import com.gradel.parent.common.util.api.enums.DataMsgFormatTemplate;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @date: 2016/9/20
 * @Description:
 */
public enum DataMsgFormat implements DataMsgFormatTemplate {
    FORMAT_SPLIT("{0}-{1}-{2}"),
    FORMAT_CN("{0}年{1}月{2}日");
    private String msgFormat;

    DataMsgFormat(String msgFormat) {
        this.msgFormat = msgFormat;
    }

    @Override
    public String getMsgFormat() {
        return msgFormat;
    }
}
