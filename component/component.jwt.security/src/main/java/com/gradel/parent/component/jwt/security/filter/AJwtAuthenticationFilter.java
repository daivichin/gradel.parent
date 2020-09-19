package com.gradel.parent.component.jwt.security.filter;

import com.gradel.parent.component.jwt.security.token.AJwtAuthenticationToken;
import com.gradel.parent.component.jwt.security.util.RequestHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * AJwtAuthenticationFilter
 * #1 登陆验证token
 * JWT鉴定过滤器
 * @Date 2019/7/20 下午6:20
 * @Author sdeven
 */
public class AJwtAuthenticationFilter extends OncePerRequestFilter {
    private List<RequestMatcher> permissiveRequestMatchers;
    private AuthenticationManager authenticationManager;

    private AuthenticationSuccessHandler successHandler;
    private AuthenticationFailureHandler failureHandler;

    public AJwtAuthenticationFilter(String... permissive) {
        if (null != permissive) {
            permissiveRequestMatchers = new ArrayList<>();
            for(String url : permissive) {
                permissiveRequestMatchers.add(new AntPathRequestMatcher(url));
            }
        }
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {
        if (!permissiveRequest(request)) {
            Authentication authResult = null;
            AuthenticationException failed = null;
            try {
                //#2获取token
                String token = RequestHelper.getToken(request);
                //#3获取ip
                String ip = RequestHelper.getIp(request);
                //通过token与IP的结合绑定有效的ip信息对应的token
                AJwtAuthenticationToken authToken = new AJwtAuthenticationToken(token, ip);
                authResult = this.getAuthenticationManager().authenticate(authToken);
            } catch (AuthenticationException e) {
                failed = e;
            } catch(Exception e) {
                failed = new InsufficientAuthenticationException("JWT format error", e);
            }
            if(authResult != null) {
                successfulAuthentication(request, response, filterChain, authResult);
            } else {
                unsuccessfulAuthentication(request, response, failed);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        failureHandler.onAuthenticationFailure(request, response, failed);
    }

    private void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException{
        SecurityContextHolder.getContext().setAuthentication(authResult);
        successHandler.onAuthenticationSuccess(request, response, authResult);
    }

    private AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    private boolean permissiveRequest(HttpServletRequest request) {
        return null != permissiveRequestMatchers && permissiveRequestMatchers.stream().anyMatch(o -> o.matches(request));
    }

    public void setAuthenticationSuccessHandler(AuthenticationSuccessHandler successHandler) {
        this.successHandler = successHandler;
    }

    public void setAuthenticationFailureHandler(AuthenticationFailureHandler failureHandler) {
        this.failureHandler = failureHandler;
    }
}
