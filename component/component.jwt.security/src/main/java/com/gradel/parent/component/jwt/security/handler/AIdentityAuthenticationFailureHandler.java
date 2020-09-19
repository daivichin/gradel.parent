package com.gradel.parent.component.jwt.security.handler;

import com.gradel.parent.component.jwt.security.exception.ANotRegisterException;
import com.gradel.parent.component.jwt.security.util.ResponseHelper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * AIdentityAuthenticationFailureHandler
 * 身份鉴定失败适配器
 * @Date 2019/7/17 下午3:08
 * @Author sdeven
 */
public class AIdentityAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) {
        if (e instanceof ANotRegisterException) {
            ResponseHelper.setRegisteration(httpServletResponse, ((ANotRegisterException) e).getRegisterCode());
        }
        ResponseHelper.setStatus(httpServletResponse, HttpStatus.UNAUTHORIZED);
    }
}
