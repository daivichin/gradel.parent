package com.gradel.parent.ali.ahas.autoconfigure;

import com.alibaba.csp.sentinel.adapter.dubbo.fallback.DefaultDubboFallback;
import com.alibaba.csp.sentinel.adapter.dubbo.fallback.DubboFallbackRegistry;
import com.alibaba.csp.sentinel.adapter.servlet.CommonFilter;
import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlBlockHandler;
import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlCleaner;
import com.alibaba.csp.sentinel.adapter.servlet.callback.WebCallbackManager;
import com.alibaba.csp.sentinel.log.LogBase;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.SentinelRpcException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.gradel.parent.ali.ahas.SentinelRpcExceptionResolver;
import com.gradel.parent.common.util.util.ExceptionUtil;
import com.gradel.parent.common.util.util.JsonUtil;
import com.gradel.parent.common.util.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import java.io.IOException;


/**
 * ahas-config.enabled=true
 * ahas-config.ahas.namespace = dev
 * ahas-config.project.name = gradel-dast-xxx-web 不配置的话。则读取 spring.application.name
 * ahas-config.ahas.license = xxxx
 * ahas-config.dubbo-block-handler.enabled=true
 * ahas-config.web-block-handler.enabled=true
 * ahas-config.web-url-cleaner.enabled=true
 *
 * ahas-config.log.dir=/home/tomcat/gradel_dast/csp_log
 * ahas-config.log.pid=true
 *
 * @author: sdeven.chen.dongwei@gmail.com
 * @date 2018/12/29 下午12:30
 * @Description: ahas的配置
 */
@Slf4j
@ConditionalOnProperty(name = AhasAutoConfiguration.AHAS_CONFIG_ENABLED, havingValue = "true")
@Configuration
public class AhasAutoConfiguration implements EnvironmentAware {

    static final String AHAS_CONFIG_PREFIX = "ahas-config.";
    static final String AHAS_CONFIG_ENABLED = AHAS_CONFIG_PREFIX + "enabled";

    static final String AHAS_CONFIG_DUBBO_BLOCK_HANDLER_ENABLED = AHAS_CONFIG_PREFIX + "dubbo-block-handler.enabled";
    static final String AHAS_CONFIG_WEB_BLOCK_HANDLER_ENABLED = AHAS_CONFIG_PREFIX + "web-block-handler.enabled";
    static final String AHAS_CONFIG_WEB_URL_CLEANER_ENABLED = AHAS_CONFIG_PREFIX + "web-url-cleaner.enabled";

    @ConditionalOnProperty(name = AhasAutoConfiguration.AHAS_CONFIG_WEB_BLOCK_HANDLER_ENABLED, havingValue = "true")
    private static class SentinelCommonFilterAutoConfiguration {

        private SentinelCommonFilterMapping filterMapping;

        @Autowired(required = false)
        public void setSentinelCommonFilterMapping(SentinelCommonFilterMapping filterMapping) {
            this.filterMapping = filterMapping;
        }

        @Bean
        public FilterRegistrationBean sentinelFilterRegistration() {
            FilterRegistrationBean registration = new FilterRegistrationBean();
            registration.setFilter(new CommonFilter());
            if(filterMapping != null){
                registration.setOrder(filterMapping.getOrder());
                registration.setUrlPatterns(filterMapping.getUrlPatterns());
                registration.setName(filterMapping.getFilterBeanName());
            }else{
                registration.setOrder(1);
                registration.addUrlPatterns("/*");
                registration.setName("sentinelCommonFilter");
            }
            return registration;
        }
    }

    private void initWebBlockDefaultCallBack() {
        /**
         * 限流 降级
         */
        WebCallbackManager.setUrlBlockHandler(new UrlBlockHandler() {
            private String blockJson;
            private String flowJson;

            @Override
            public void blocked(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, BlockException ex)
                    throws IOException {
                String json = null;
                if (ex instanceof FlowException) {
                    if (flowJson == null) {
                        flowJson = JsonUtil.toJson(SentinelRpcExceptionResolver.FLOW_BLOCK_RESP);
                    }
                    json = flowJson;
                } else {
                    if (blockJson == null) {
                        blockJson = JsonUtil.toJson(SentinelRpcExceptionResolver.DEGRADE_BLOCK_RESP);
                    }
                    json = blockJson;
                }
                log.warn("Web SentinelRpcException:url[{}]", request.getRequestURI());

                //HttpUtil.writeJsonDate(response, json);
                writeJsonDate(response, json);

            }

            /**
             * 输出json格式提示 并且带对象消息
             *
             * @param response
             * @param json     提示消息
             */
            private void writeJsonDate(javax.servlet.ServletResponse response, String json) {
                try {
                    response.setContentType("application/json;charset=UTF-8");
                    response.setCharacterEncoding("utf-8");
                    response.getWriter().write(json);
                } catch (Exception e) {
                    log.error("Web SentinelRpcException, reponse json Exception Occur:[{}]", ExceptionUtil.getAsString(e));
                }
            }
        });
    }

    private void initWebUrlDefaultCleanr() {
        /**
         * TODO URL 资源清洗
         * TODO Sentinel Web Servlet Filter 会将每个到来的不同的 URL 都作为不同的资源处理，因此对于 REST 风格的 API，
         * TODO 需要自行实现 UrlCleaner 接口清洗一下资源（比如将满足 /foo/:id 的 URL 都归到 /foo/* 资源下），
         * TODO 然后将其注册至 WebCallbackManager 中。否则会导致资源数量过多，超出资源数量阈值（6000）时多出的资源的规则将 不会生效
         */
        WebCallbackManager.setUrlCleaner(new UrlCleaner() {
            @Override
            public String clean(String originUrl) {
                return originUrl;
            }
        });
    }

    private void initDubboBlockDefaultHandler() {
        DubboFallbackRegistry.setConsumerFallback(new DefaultDubboFallback(){
            @Override
            public Result handle(Invoker<?> invoker, Invocation invocation, BlockException ex) {
                // Just wrap and throw the exception.
                log.error("RPC Consumer SentinelRpcException:service[{}:{}]", invocation.getInvoker().getInterface().getName(), invocation.getMethodName());
                throw new SentinelRpcException(ex);
            }
        });

        DubboFallbackRegistry.setProviderFallback(new DefaultDubboFallback(){
            @Override
            public Result handle(Invoker<?> invoker, Invocation invocation, BlockException ex) {
                // Just wrap and throw the exception.
                log.error("RPC Provider SentinelRpcException:service[{}:{}]", invocation.getInvoker().getInterface().getName(), invocation.getMethodName());
                throw new SentinelRpcException(ex);
            }
        });
    }

    /**
     * ahas-config配置
     *
     * @param environment
     * @return
     */
    private void initAhasConfig(Environment environment) {
        String[] ahasKeyArrs = {"ahas.namespace", "project.name:spring.application.name", "ahas.license"};
        for (String keyAndDefault : ahasKeyArrs) {
            String[] keys = keyAndDefault.split(":");
            String ahasKey = keys[0];
            String value = environment.getProperty(AHAS_CONFIG_PREFIX + ahasKey);
            if (StringUtil.isNotBlank(value)) {
                System.setProperty(ahasKey, value);
            } else if (keys.length > 1) {
                String defaultVal = environment.getProperty(keys[1]);
                if(StringUtil.isNotBlank(defaultVal)){
                    System.setProperty(ahasKey, defaultVal);
                }
            }
        }
        //设置Log
        String logDir = environment.getProperty(AHAS_CONFIG_PREFIX + "log.dir", "/home/tomcat/sibu_mall/csp_log");
        if (StringUtil.isNotBlank(logDir)) {
            System.setProperty(LogBase.LOG_DIR, logDir);
        }

        //设置是否加个进程ID，日志文件
        String logPid = environment.getProperty(AHAS_CONFIG_PREFIX + "log.pid", "true");
        if (StringUtil.isNotBlank(logPid)) {
            System.setProperty(LogBase.LOG_NAME_USE_PID, logPid);
        }
    }

    private boolean isEnable(Environment environment, String key){
        String isEnable = environment.getProperty(key);
        return Boolean.parseBoolean(isEnable);
    }

    private void init(Environment environment) {
        initAhasConfig(environment);

        if(isEnable(environment, AHAS_CONFIG_DUBBO_BLOCK_HANDLER_ENABLED)){
            initDubboBlockDefaultHandler();
        }

        if(isEnable(environment, AHAS_CONFIG_WEB_BLOCK_HANDLER_ENABLED)){
            initWebBlockDefaultCallBack();
        }

        if(isEnable(environment, AHAS_CONFIG_WEB_URL_CLEANER_ENABLED)){
            initWebUrlDefaultCleanr();
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        init(environment);
    }
}
