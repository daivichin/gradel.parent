package com.gradel.parent.component.jwt.security.provider;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.gradel.parent.component.jwt.security.jwt.AJWTService;
import com.gradel.parent.component.jwt.security.token.AJwtAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * AJwtAuthenticationProvider
 * JWT鉴定提供者
 * @Date 2019/7/17 下午3:08
 * @Author sdeven
 */
public class AJwtAuthenticationProvider implements AuthenticationProvider {

    private AJWTService jwtService;

    public AJwtAuthenticationProvider(AJWTService jwtService) {
        this.jwtService = jwtService;
    }

    /**
     * #5 Jwt验证提供者
     * @param authentication
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        AJwtAuthenticationToken jwtToken = (AJwtAuthenticationToken) authentication;
        String token = (String) jwtToken.getDetails();
        DecodedJWT jwt = jwtService.validate(token, jwtToken.getIp());
        return new AJwtAuthenticationToken(token, jwt);
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return AJwtAuthenticationToken.class.isAssignableFrom(aClass);
    }
}
