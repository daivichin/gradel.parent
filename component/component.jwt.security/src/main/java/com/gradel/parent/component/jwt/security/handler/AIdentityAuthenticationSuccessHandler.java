package com.gradel.parent.component.jwt.security.handler;

import com.gradel.parent.component.jwt.security.entity.AIdentity;
import com.gradel.parent.component.jwt.security.jwt.AJWTService;
import com.gradel.parent.component.jwt.security.util.ResponseHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * AIdentityAuthenticationSuccessHandler
 * 身份鉴定成功适配器
 * @Date 2019/7/17 下午3:08
 * @Author sdeven
 */
public class AIdentityAuthenticationSuccessHandler
        implements AuthenticationSuccessHandler {
    private AJWTService jwtService;

    public AIdentityAuthenticationSuccessHandler(AJWTService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String token = jwtService.create();
        AIdentity identity = (AIdentity) authentication.getDetails();
        identity.setToken(token);
        ResponseHelper.setToken(response, token);
        jwtService.onSuccess();
    }
}
