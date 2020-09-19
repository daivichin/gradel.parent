package com.gradel.parent.tencent.cmq.test.config;

import com.gradel.parent.common.util.util.StringUtil;
import com.gradel.parent.tencent.cmq.test.util.PropertiesUtil;

import java.net.URL;
import java.util.Properties;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @date: 2017-12-13
 * @Description:
 */
public class ConfigManager {

    static volatile String pro_base_dir = null;

    final static String consumerConfigName = "/properties/mq-consumer.properties";
    final static String producerConfigName = "/properties/mq-producer.properties";

    public static Properties getConsumerProp(){
        return PropertiesUtil.getProperties(getAbsolutePath(consumerConfigName));
    }

    public static Properties getProducerProp(){
        return PropertiesUtil.getProperties(getAbsolutePath(producerConfigName));
    }

    private static String getProBasePath() {
        URL resource = ConfigManager.class.getClassLoader().getResource("");
        return resource != null ? resource.getPath() : "";
    }

    private static String getAbsolutePath(String path){
        if(pro_base_dir == null){
            synchronized (ConfigManager.class){
                if(pro_base_dir == null){
                    pro_base_dir = System.getProperty("PRO_BASE_DIR", getProBasePath());
                }
                if(pro_base_dir != null){
                    if(pro_base_dir.endsWith("/")){
                        pro_base_dir = pro_base_dir.substring(0, pro_base_dir.length() -1);
                    }
                }else{
                    pro_base_dir = "";
                }
            }
        }

        String absolutePath = pro_base_dir + StringUtil.defaultString(path, "");
        return absolutePath;
    }
}
