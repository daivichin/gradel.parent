package com.gradel.parent.tencent.cmq.api.impl;

import com.gradel.parent.tencent.cmq.api.CMQFactoryAPI;
import com.gradel.parent.tencent.cmq.api.Consumer;
import com.gradel.parent.tencent.cmq.api.Producer;

import java.util.Properties;


/**
 * 客户端工厂类，用来创建客户端对象
 */
public class CMQFactory {
    private static CMQFactoryAPI cmqFactory = null;

    static {
        try {
            Class<?> factoryClass =
                    CMQFactory.class.getClassLoader().loadClass(
                            "com.gradel.parent.tencent.cmq.api.impl.CMQFactoryImpl");
            cmqFactory = (CMQFactoryAPI) factoryClass.newInstance();
        } catch (Throwable e1) {
            e1.printStackTrace();
        }
    }


    /**
     * 创建Producer
     *
     * @param properties Producer的配置参数
     * @return
     */
    public static Producer createProducer(final Properties properties) {
        return getCmqFactory().createProducer(properties);
    }

    /**
     * 创建Consumer
     *
     * @param properties Consumer的配置参数
     * @return
     */
    public static Consumer createConsumer(final Properties properties) {
        return getCmqFactory().createConsumer(properties);
    }

    public static CMQFactoryAPI getCmqFactory() {
        return cmqFactory;
    }

    public static void cmqFactory(CMQFactoryAPI cmqFactory) {
        CMQFactory.cmqFactory = cmqFactory;
    }

    public void setCmqFactory(CMQFactoryAPI cmqFactory) {
        CMQFactory.cmqFactory = cmqFactory;
    }
}
