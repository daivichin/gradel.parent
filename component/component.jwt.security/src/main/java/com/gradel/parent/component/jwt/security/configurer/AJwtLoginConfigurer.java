package com.gradel.parent.component.jwt.security.configurer;

import com.gradel.parent.component.jwt.security.filter.AJwtAuthenticationFilter;
import com.gradel.parent.component.jwt.security.handler.AJwtAuthenticationFailureHandler;
import com.gradel.parent.component.jwt.security.handler.AJwtAuthenticationSuccessHandler;
import com.gradel.parent.component.jwt.security.jwt.AJWTService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.logout.LogoutFilter;

/**
 * ARestLoginConfigurer
 * jwt登陆配置
 * @Date 2019/7/18 下午8:37
 * @Author sdeven
 */
public class AJwtLoginConfigurer<T extends AJwtLoginConfigurer<T, B>, B extends HttpSecurityBuilder<B>> extends AbstractHttpConfigurer<T, B> {
    private AJwtAuthenticationFilter authFilter;
    private AJWTService jwtService;

    public AJwtLoginConfigurer(AJWTService jwtService, String... permissive) {
        this.authFilter = new AJwtAuthenticationFilter(permissive);
        this.jwtService = jwtService;
    }

    @Override
    public void configure(B http) {
        authFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        authFilter.setAuthenticationFailureHandler(new AJwtAuthenticationFailureHandler());
        authFilter.setAuthenticationSuccessHandler(new AJwtAuthenticationSuccessHandler(jwtService));

        AJwtAuthenticationFilter filter = this.postProcess(authFilter);
        http.addFilterAfter(filter, LogoutFilter.class);
    }
}
