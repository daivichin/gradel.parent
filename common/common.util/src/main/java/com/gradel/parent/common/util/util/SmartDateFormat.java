package com.gradel.parent.common.util.util;

import com.gradel.parent.common.util.date.DateUtil;

import java.text.FieldPosition;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
* User: sdeven.chen.dongwei@gmail.com
 * @date: 2016-3-13
 * @Description:处理json时间转换
 */
public class SmartDateFormat extends SimpleDateFormat {
    @Override
    public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition pos) {
        return new StringBuffer(DateUtil.smartFormat(date));
    }

    @Override
    public Date parse(String text) throws ParseException {
        return DateUtil.smartFormat(text);
    }

}
