package com.gradel.parent.component.jwt.security.handler;

import com.gradel.parent.component.jwt.security.jwt.AJWTService;
import com.gradel.parent.component.jwt.security.util.RequestHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * AJwtClearLogoutHandler
 * JWT登出后清除适配器
 * @Date 2019/7/21 下午3:19
 * @Author sdeven
 */
public class AJwtClearLogoutHandler implements LogoutHandler {
    private AJWTService jwtService;

    public AJwtClearLogoutHandler(AJWTService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String token = RequestHelper.getToken(request);
        jwtService.remove(token);
    }
}
