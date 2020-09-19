package com.gradel.parent.tencent.cmq.ext.queue.env;

import org.springframework.beans.factory.InitializingBean;

/** User: sdeven.chen.dongwei@gmail.com Date: 2016/9/14
 * @Description:MQ主题环镜  启动的时候必须要先初始化env属性，才能使用，否则报错空指针（不设置默认值，防止忘了修改而导致环镜错误）
 */
public class QueueEnvBean implements InitializingBean {

    IQueueEnv qaueueEnv;

    public void setQueueEnv(IQueueEnv qaueueEnv) {
        this.qaueueEnv = qaueueEnv;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.qaueueEnv == null) {
            throw new IllegalArgumentException("Mq topic environment must not null");
        }

        //设置MQ 主题环镜
        //QaueueEnvEnum.env = qaueueEnv;
    }

    public IQueueEnv getQueueEnv() {
        return qaueueEnv;
    }
}