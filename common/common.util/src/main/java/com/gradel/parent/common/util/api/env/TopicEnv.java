package com.gradel.parent.common.util.api.env;

/**
* User: sdeven.chen.dongwei@gmail.com
 * @date: 2016/11/26
 * @Description: MQ主题 环镜
 *
 * @see TopicEnvManager
 */
public enum TopicEnv implements ITopicEnv {
    /**
     * 测试环镜
     */
    DEV {
        @Override
        public String getCode(String code) {
            return code + "_dev";
        }
    },
    /**
     * 测试环镜
     */
    TEST {
        @Override
        public String getCode(String code) {
            return code + "_test";
        }
    },
    /**
     * uat环镜
     */
    UAT {
        @Override
        public String getCode(String code) {
            return code + "_uat";
        }
    },
    /**
     * PRE环镜
     */
    PRE {
        @Override
        public String getCode(String code) {
            return code + "_pre";
        }
    },
    /**
     * 生产环境
     */
    PROD{
        @Override
        public String getCode(String code) {
            return code + "_prod";
        }
    },
    /**
     *　不需要通过后缀来区分环境
     */
    EMPTY{
        @Override
        public String getCode(String code) {
            return code;
        }
    },
    ;

    private TopicEnv() {

    }

    public static TopicEnv resolveByName(String name) {
        return TopicEnv.valueOf(name);
    }
}