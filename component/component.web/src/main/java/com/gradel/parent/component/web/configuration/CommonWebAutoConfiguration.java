package com.gradel.parent.component.web.configuration;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.gradel.parent.component.web.advice.UExceptionHandler;
import com.gradel.parent.component.web.resolver.OrderItemArgumentResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class CommonWebAutoConfiguration implements WebMvcConfigurer {

    @Bean
    public OrderItemArgumentResolver orderItemArgumentResolver() {
        return new OrderItemArgumentResolver();
    }

    @Bean
    public UExceptionHandler cExceptionHandler() {
        return new UExceptionHandler();
    }

    @Bean
    public FastJsonHttpMessageConverter fastJsonHttpMessageConverter() {
        // 创建消息转换器
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();

        // 创建配置类
        FastJsonConfig config = new FastJsonConfig();
        config.setSerializerFeatures(SerializerFeature.DisableCircularReferenceDetect,
                SerializerFeature.BrowserSecure,
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteNullListAsEmpty,
                SerializerFeature.WriteNullStringAsEmpty,
                SerializerFeature.WriteNullBooleanAsFalse);
        fastConverter.setFastJsonConfig(config);

        //附加：处理中文乱码
        List<MediaType> fastMedisTypes = new ArrayList<>();
        fastMedisTypes.add(MediaType.APPLICATION_JSON_UTF8);
        fastConverter.setSupportedMediaTypes(fastMedisTypes);

        return fastConverter;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(orderItemArgumentResolver());
    }
}
