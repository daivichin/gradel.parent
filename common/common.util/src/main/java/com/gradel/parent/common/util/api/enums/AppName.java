package com.gradel.parent.common.util.api.enums;

import com.gradel.parent.common.util.util.EnumUtil;
import lombok.Getter;
import lombok.ToString;

/**
 * 应用名称
 */
@ToString
public enum AppName implements AppNameBase {
    DOUBO_SC("DOUBO_SC", "服务中心", "s"),
    ROCKETMQ_SC("ROCKETMQ_SC", "RocketMQ服务中心", "r"),
    APACHE_ROCKETMQ_SC("APACHE_ROCKETMQ_SC", "Apache RocketMQ服务中心", "r")
    ;

    @Getter
    private String code;

    @Getter
    private String desc;

    @Getter
    private String codeNumber;

    private AppName(String code, String desc, String codeNumber) {
        this.code = code;
        this.desc = desc;
        this.codeNumber = codeNumber;
    }

    public static AppName resolveCodeNumber(String codeNumber) {
        return EnumUtil.fromEnumValue(AppName.class, "codeNumber", codeNumber);
    }

    public static AppName resolve(String code) {
        return EnumUtil.fromEnumValue(AppName.class, "code", code);
    }
}
