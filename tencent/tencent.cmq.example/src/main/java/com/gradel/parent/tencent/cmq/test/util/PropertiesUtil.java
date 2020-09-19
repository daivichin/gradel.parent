package com.gradel.parent.tencent.cmq.test.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.util.Enumeration;
import java.util.Properties;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @date: 2017-12-13
 * @Description:
 */
public class PropertiesUtil {

    public static Properties getProperties(String configFileName) {
        Properties props = new Properties();
        FileInputStream istream = null;
        try {
            istream = new FileInputStream(configFileName);
            props.load(istream);
            istream.close();
        } catch (Exception e) {
            throw new RuntimeException("Load properties Exception:" + e.getMessage(), e);
        } finally {
            if (istream != null) {
                try {
                    istream.close();
                } catch (InterruptedIOException ignore) {
                    Thread.currentThread().interrupt();
                } catch (Throwable ignore) {
                }

            }
        }
        return props;
    }

    public static Properties getProperties(InputStream configFileInputStream) {
        Properties props = new Properties();
        try {
            props.load(configFileInputStream);
            configFileInputStream.close();
        } catch (Exception e) {
            throw new RuntimeException("Load properties Exception:" + e.getMessage(), e);
        } finally {
            if (configFileInputStream != null) {
                try {
                    configFileInputStream.close();
                } catch (InterruptedIOException ignore) {
                    Thread.currentThread().interrupt();
                } catch (Throwable ignore) {
                }

            }
        }
        return props;
    }

    public static Properties getProperties(File configFile) {
        return getProperties(configFile.getPath());
    }

    public static String toString(Properties properties){
        //第一种方法遍历Properties:
        //use Enumeration to visit the properties
        StringBuilder stringBuilder = new StringBuilder("propertyNames:\n");
        Enumeration<?> enumeration = properties.propertyNames();
        while(enumeration.hasMoreElements()){
            String value = (String) enumeration.nextElement();
            stringBuilder.append(value + "=" + properties.getProperty(value)+"\n");
        }
        return stringBuilder.toString();
    }

}
