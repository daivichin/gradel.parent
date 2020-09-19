package com.gradel.parent.tencent.cmq.test.benchmark;

import com.gradel.parent.common.util.util.EnumUtil;
import com.gradel.parent.tencent.cmq.api.QueueName;
import lombok.Getter;
import lombok.Setter;

public enum BenchQueueEnum implements QueueName {

    /**
     * 测试主题
     */
    QUEUE_BenchmarkTest("MyQueueBenchmark-test", "测试主题"),

    ;

    private BenchQueueEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Getter @Setter
    private String code;
    @Getter @Setter
    private String desc;

    public static BenchQueueEnum resolve(String value) {
        return EnumUtil.fromEnumValue(BenchQueueEnum.class, "code", value);
    }

}