package com.gradel.parent.common.util.api.env;


import com.gradel.parent.common.util.api.util.StaticFieldUtil;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @date 2019/01/22 下午6:23
 * @Description: 初始化MQ主题环境管理器
 *
 * 1.一定要先初始化
 *      TopicEnvManager.getInstance().setTopicEnvByFullClassName();
 *      or
 *      TopicEnvManager.getInstance().setTopicEnv();
 *
 * 2.获取环境
 *      TopicEnvManager.topicEnv()
 */
public class TopicEnvManager {

    //默认不需要通过后缀来区分环境
    private static ITopicEnv topicEnv = TopicEnv.EMPTY;

    private static TopicEnvManager instance;

    public static TopicEnvManager getInstance(){
        if(instance == null){
            synchronized (TopicEnvManager.class){
                if(instance == null){
                    instance = new TopicEnvManager();
                }
            }
        }

        return instance;
    }

    public static ITopicEnv topicEnv() {
        return topicEnv;
    }

    public ITopicEnv getTopicEnv() {
        return topicEnv();
    }

    public void setTopicEnv(ITopicEnv topicEnv) {
        topicEnv(topicEnv);
    }

    public static void topicEnv(ITopicEnv topicEnv) {
        TopicEnvManager.topicEnv = topicEnv;
    }

    /**
     * @param topicFullClassFieldName com.ml.common.topic.env.TopicEnv.DEV
     */
    public void setTopicEnvByFullClassName(String topicFullClassFieldName) {
        topicEnvByFullClassName(topicFullClassFieldName);
    }

    /**
     * @param topicFullClassFieldName com.ml.common.topic.env.TopicEnv.DEV
     */
    public static void topicEnvByFullClassName(String topicFullClassFieldName) {
        ITopicEnv topicEnv = (ITopicEnv) StaticFieldUtil.getInstance(topicFullClassFieldName);
        topicEnv(topicEnv);
    }

    public static void main(String[] args){
        TopicEnv dev1 = TopicEnv.resolveByName("DEV");
        TopicEnv dev2 = (TopicEnv) StaticFieldUtil.getInstance("com.ml.common.topic.env.TopicEnv.DEV");
        System.out.println(dev1.getCode(""));
        System.out.println(dev2.getCode(""));


    }

}
