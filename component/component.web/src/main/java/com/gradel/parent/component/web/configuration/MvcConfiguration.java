package com.gradel.parent.component.web.configuration;

import com.gradel.parent.component.web.service.WebSecurityService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器注册
 */
@Configuration
public class MvcConfiguration implements WebMvcConfigurer {

    WebSecurityService webSecurityService;

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(new SecurityServiceInterceptor())
                .addPathPatterns(webSecurityService.checkPathPatterns())
                .excludePathPatterns(webSecurityService.checkExcludePathPatterns());
}
}
