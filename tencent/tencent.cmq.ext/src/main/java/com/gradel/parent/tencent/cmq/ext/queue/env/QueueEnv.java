package com.gradel.parent.tencent.cmq.ext.queue.env;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @date: 2016/11/26
 * @Description: MQ Queue 环镜
 */
public enum QueueEnv implements IQueueEnv {
    /**
     * 测试环镜
     */
    TEST {
        @Override
        public String getCode(String code) {
            return code + "-test";
        }
    },
    /**
     * uat环镜
     */
    UAT {
        @Override
        public String getCode(String code) {
            return code + "-uat";
        }
    },
    /**
     * PRE环镜
     */
    PRE {
        @Override
        public String getCode(String code) {
            return code + "-pre";
        }
    },
    /**
     * 生产环境
     */
    PRO;

    private QueueEnv(){

    }

    @Override
    public String getCode(String code) {
        return code + "-prod";
    }
}