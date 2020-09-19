package com.gradel.parent.common.util.util;

import com.gradel.parent.common.util.api.enums.DataMsgFormatTemplate;
import com.gradel.parent.common.util.date.DataMsgFormat;

import java.text.MessageFormat;

/**
* User: sdeven.chen.dongwei@gmail.com
 * @date: 2016/3/3
 * @Description:消息格式化工具
 */
public class MessageFormatUtil {

    public static String format(DataMsgFormat msgFormat, int year, int month, int day){
        return MessageFormat.format(msgFormat.getMsgFormat(), year, month, day);
    }

    public static String format(DataMsgFormat msgFormat, String year, String month, String day){
        return MessageFormat.format(msgFormat.getMsgFormat(), year, month, day);
    }

    public static String format(String format, Object ...args){
        return MessageFormat.format(format, args);
    }

    public static String format(DataMsgFormatTemplate dataMsgFormatTemplate, Object ...args){
        return MessageFormat.format(dataMsgFormatTemplate.getMsgFormat(), args);
    }
}
