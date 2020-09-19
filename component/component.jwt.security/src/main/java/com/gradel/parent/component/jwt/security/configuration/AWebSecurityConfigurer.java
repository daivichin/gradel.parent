package com.gradel.parent.component.jwt.security.configuration;

import com.gradel.parent.component.jwt.security.configurer.AJwtLoginConfigurer;
import com.gradel.parent.component.jwt.security.configurer.ARestLoginConfigurer;
import com.gradel.parent.component.jwt.security.decide.APermissionBasedVoter;
import com.gradel.parent.component.jwt.security.decide.APermissionFilterInvocationSecurityMetadataSource;
import com.gradel.parent.component.jwt.security.endpoint.service.AuthenticationService;
import com.gradel.parent.component.jwt.security.filter.AOptionsRequestFilter;
import com.gradel.parent.component.jwt.security.handler.AJwtClearLogoutHandler;
import com.gradel.parent.component.jwt.security.jwt.AJWTService;
import com.gradel.parent.component.jwt.security.provider.AIdentityAuthenticationProvider;
import com.gradel.parent.component.jwt.security.provider.AJwtAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;

/**
 * AWebSecurityConfig
 *
 * @Date 2019/7/21 下午2:57
 * @Author sdeven
 */
@EnableWebSecurity
public class AWebSecurityConfigurer extends WebSecurityConfigurerAdapter {
    @Resource
    private AuthenticationService authenticationService;
    private AJWTService aJWTService;

    /**
     * web 安全配置
     * @param authenticationService
     * @param aJWTService
     */
    @Autowired
    public AWebSecurityConfigurer(
            AuthenticationService authenticationService,
            AJWTService aJWTService) {
        this.authenticationService = authenticationService;
        this.aJWTService = aJWTService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .authorizeRequests()
                .anyRequest().authenticated()
                .withObjectPostProcessor(this.permissionPostProcessor())
                .and()
                .csrf().disable()
                .formLogin().disable()
                .cors()
                .and()
                .apply(new ARestLoginConfigurer<>(aJWTService, authenticationService.getLoginUrl()))
                .and()
                .apply(new AJwtLoginConfigurer<>(aJWTService, authenticationService.getPermissiveUrl()))
                .and()
                .addFilterBefore(new AOptionsRequestFilter(), CorsFilter.class)
                .logout().logoutUrl(authenticationService.getLogoutUrl())
                .addLogoutHandler(new AJwtClearLogoutHandler(aJWTService))
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }


    private ObjectPostProcessor permissionPostProcessor() {
        return new ObjectPostProcessor<FilterSecurityInterceptor>() {
            @Override
            public <O extends FilterSecurityInterceptor> O postProcess(O fsi) {
                AffirmativeBased accessDecisionManager = new AffirmativeBased(
                        Collections.singletonList(new APermissionBasedVoter()));
                accessDecisionManager.setAllowIfAllAbstainDecisions(true);

                APermissionFilterInvocationSecurityMetadataSource securityMetadataSource =
                        new APermissionFilterInvocationSecurityMetadataSource(
                                fsi.getSecurityMetadataSource(),
                                authenticationService);

                fsi.setAccessDecisionManager(accessDecisionManager);
                fsi.setSecurityMetadataSource(securityMetadataSource);
                return fsi;
            }
        };
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth
                .authenticationProvider(new AIdentityAuthenticationProvider(authenticationService))
                .authenticationProvider(new AJwtAuthenticationProvider(aJWTService));
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     *  跨域调用配置
     * @return
     */
    @Bean
    protected CorsFilter corsFilter() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(Collections.singletonList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "HEAD", "OPTION"));
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.addExposedHeader("Authorization");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return new CorsFilter(source);
    }
}
