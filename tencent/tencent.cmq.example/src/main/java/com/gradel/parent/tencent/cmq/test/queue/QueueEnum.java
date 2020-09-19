package com.gradel.parent.tencent.cmq.test.queue;

import com.gradel.parent.common.util.api.model.MsgResult;
import com.gradel.parent.common.util.util.EnumUtil;
import com.gradel.parent.tencent.cmq.api.QueueName;
import com.gradel.parent.tencent.cmq.ext.queue.env.IQueueEnv;
import com.gradel.parent.tencent.cmq.ext.queue.env.QueueEnv;
import com.gradel.parent.tencent.cmq.ext.queue.env.QueueEnvBean;
import com.gradel.parent.tencent.cmq.test.util.SpringUtil;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/** User: sdeven.chen.dongwei@gmail.com Date: 2016/9/14
 * @Description:MQ主题定义  启动的时候必须要先初始化env属性，
 * 才能使用，否则报错空指针（不设置默认值，防止忘了修改而导致环镜错误）
 *
 * @see QueueEnvBean
 * <p>
 * 一、阿里MQ 发布topic注意事项：
 * 1.只能包含字母，数字，短横线(-)
 * 2.名称长度限制在3-64字节之间，长于64字节将被自动截取
 * 3.一旦创建后不能再修改Queue名称
 */
public enum QueueEnum implements QueueName {

    /**
     1.只能包含字母，数字，短横线(-)和下划线(_)
     2.名称长度限制在3-64字节之间，长于64字节将被自动截取
     3.一旦创建后不能再修改Topic名称
     */

    /**
     * 测试主题
     */
    QUEUE_TEST("myQueue", "测试主题"),

    /**
     * 测试主题
     */
    QUEUE_BenchmarkTest("MyQueueBenchmark-test", "测试主题"),

    ;


    /**
     * 主题
     * code + evn.code  例如：code:jpush  evn:测试环镜
     * 则 返回的值为jpush_test
     */
    private String code;

    private String desc;

    /**
     * 启动的时候必须要先初始化env，才能使用
     *
     * @see QueueEnvBean
     */
    public static IQueueEnv env;

    @Override
    public String getCode() {
        if (env == null) {
            synchronized (QueueEnum.class) {
                if (env == null) {
                    env = SpringUtil.getBean(IQueueEnv.class);
                }
            }
        }
        return env.getCode(code);
    }

    @Override
    public String getDesc() {
        return desc;
    }

    public static void setEnv(IQueueEnv env) {
        QueueEnum.env = env;
    }

    public void env(IQueueEnv env) {
        QueueEnum.env = env;
    }

    private QueueEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDesc(String code) {
        for (QueueEnum enumObj : QueueEnum.values()) {
            if (enumObj.code.equals(code)) {
                return enumObj.desc;
            }
        }
        return code + "";
    }

    public static QueueEnum resolve(String value) {
        return EnumUtil.fromEnumValue(QueueEnum.class, "code", value);
    }


    @ToString(callSuper = true)
    public static class CheckResult extends MsgResult {
        public CheckResult() {
        }

        public CheckResult(String code, String msg, boolean success) {
            setMsg(msg);
            setCode(code);
            setSuccess(success);
        }

        @Override
        public String getName() {
            return "topic";
        }
    }

    /**
     * 1.只能包含字母，数字，短横线(-)
     * 2.名称长度限制在3-64字节之间，长于64字节将被自动截取
     * 3.一旦创建后不能再修改Queue名称
     *
     * @return 返回不符合以上规则的code
     */
    public static CheckResult checkErrorCode() {
        Pattern pattern = Pattern.compile("[0-9a-zA-Z-]*");
        Set<String> topicSet = new HashSet<>();
        for (QueueEnum topicEnum : QueueEnum.values()) {
            if (topicEnum.getCode().length() > 64) {
                return new CheckResult(topicEnum.getCode(), "top长度大于64", false);
            }

            if (!pattern.matcher(topicEnum.getCode()).matches()) {
                //只能包含字母，数字，短横线(-)和下划线(_)
                return new CheckResult(topicEnum.getCode(), "只能包含字母，数字，短横线(-)", false);
            }
        }
        return new CheckResult("", "检测通过", true);
    }

    public static void main(String[] a) {
        QueueEnum.env = QueueEnv.TEST;
        CheckResult checkResult = checkErrorCode();
        System.out.println(checkResult);
        //获取最长的code
        int maxLen = 0;
        String maxVal = "";
        for (QueueEnum topicEnum : QueueEnum.values()) {
            if (topicEnum.getCode().length() > maxLen) {
                maxLen = topicEnum.getCode().length();
                maxVal = topicEnum.getCode();
            }
        }
        System.out.println("maxVal:" + maxVal + " len:" + maxLen);
    }
}
