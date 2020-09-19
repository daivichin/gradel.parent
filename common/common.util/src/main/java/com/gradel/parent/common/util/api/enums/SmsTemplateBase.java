package com.gradel.parent.common.util.api.enums;

import java.text.MessageFormat;

/**
* User: sdeven.chen.dongwei@gmail.com
 * @date: 2016/9/20
 * @Description:
 */
public interface SmsTemplateBase {
    String getMsgFormat();

    String getSmsTemplateCode();

    default String format(String... params) {
        return MessageFormat.format(this.getMsgFormat(), params);
    }
}
