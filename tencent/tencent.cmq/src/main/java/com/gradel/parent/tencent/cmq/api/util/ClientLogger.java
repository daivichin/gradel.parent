package com.gradel.parent.tencent.cmq.api.util;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.net.URL;

public class ClientLogger {
    public static final String CLIENT_LOGGER_NAME = "CMQClient";
    public static final String CLIENT_LOG_ROOT = "cmq.client.logRoot";
    public static final String CLIENT_LOG_MAXINDEX = "cmq.client.logFileMaxIndex";
    public static final String CLIENT_LOG_LEVEL = "cmq.client.logLevel";

    private static Logger log;

    private static Logger createLogger(final String loggerName) {
        String logConfigFilePath = System.getProperty("cmq.client.log.configFile", System.getenv("CMQ_CLIENT_LOG_CONFIGFILE"));
        Boolean isloadconfig =
                Boolean.parseBoolean(System.getProperty("cmq.client.log.loadconfig", "true"));

        final String log4JResourceFile =
                System.getProperty("cmq.client.log4j.resource.fileName", "log4j_cmq_client.xml");

        final String logbackResourceFile =
                System.getProperty("cmq.client.logback.resource.fileName", "logback_cmq_client.xml");

        final String log4J2ResourceFile =
                System.getProperty("cmq.client.log4j2.resource.fileName", "log4j2_cmq_client.xml");

        String clientLogRoot = System.getProperty(CLIENT_LOG_ROOT, System.getProperty("user.home") + "/logs/cmqlogs");
        System.setProperty("log.client.logRoot", clientLogRoot);
        String clientLogLevel = System.getProperty(CLIENT_LOG_LEVEL, "INFO");
        System.setProperty("log.client.logLevel", clientLogLevel);
        String clientLogMaxIndex = System.getProperty(CLIENT_LOG_MAXINDEX, "10");
        System.setProperty("log.client.logFileMaxIndex", clientLogMaxIndex);

        if (isloadconfig) {
            try {
                ILoggerFactory iLoggerFactory = LoggerFactory.getILoggerFactory();
                Class classType = iLoggerFactory.getClass();
                if (classType.getName().equals("org.slf4j.impl.Log4jLoggerFactory")) {
                    Class<?> domconfigurator;
                    Object domconfiguratorobj;
                    domconfigurator = Class.forName("org.apache.log4j.xml.DOMConfigurator");
                    domconfiguratorobj = domconfigurator.newInstance();
                    if (null == logConfigFilePath) {
                        Method configure = domconfiguratorobj.getClass().getMethod("configure", URL.class);
                        URL url = ClientLogger.class.getClassLoader().getResource(log4JResourceFile);
                        configure.invoke(domconfiguratorobj, url);
                    } else {
                        Method configure = domconfiguratorobj.getClass().getMethod("configure", String.class);
                        configure.invoke(domconfiguratorobj, logConfigFilePath);
                    }

                } else if (classType.getName().equals("ch.qos.logback.classic.LoggerContext")) {
                    Class<?> joranConfigurator;
                    Class<?> context = Class.forName("ch.qos.logback.core.Context");
                    Object joranConfiguratoroObj;
                    joranConfigurator = Class.forName("ch.qos.logback.classic.joran.JoranConfigurator");
                    joranConfiguratoroObj = joranConfigurator.newInstance();
                    Method setContext = joranConfiguratoroObj.getClass().getMethod("setContext", context);
                    setContext.invoke(joranConfiguratoroObj, iLoggerFactory);
                    if (null == logConfigFilePath) {
                        URL url = ClientLogger.class.getClassLoader().getResource(logbackResourceFile);
                        Method doConfigure =
                                joranConfiguratoroObj.getClass().getMethod("doConfigure", URL.class);
                        doConfigure.invoke(joranConfiguratoroObj, url);
                    } else {
                        Method doConfigure =
                                joranConfiguratoroObj.getClass().getMethod("doConfigure", String.class);
                        doConfigure.invoke(joranConfiguratoroObj, logConfigFilePath);
                    }

                } else if (classType.getName().equals("org.apache.logging.slf4j.Log4jLoggerFactory")) {
                    Class<?> joranConfigurator = Class.forName("org.apache.logging.log4j.core.config.Configurator");
                    Method initialize = joranConfigurator.getDeclaredMethod("initialize", String.class, String.class);
                    if (null == logConfigFilePath) {
                        initialize.invoke(joranConfigurator, "log4j2", log4J2ResourceFile);
                    } else {
                        initialize.invoke(joranConfigurator, "log4j2", logConfigFilePath);
                    }
                }
            } catch (Exception e) {
                System.err.println(e);
            }
        }
        return LoggerFactory.getLogger(CLIENT_LOGGER_NAME);
    }

    public static Logger getLog() {
        if (log == null) {
            log = createLogger(CLIENT_LOGGER_NAME);
            return log;
        } else {
            return log;
        }
    }

    public static void setLog(Logger log) {
        ClientLogger.log = log;
    }
}