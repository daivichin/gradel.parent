package com.gradel.parent.component.jwt.security.provider;

import com.gradel.parent.component.jwt.security.authenticator.AIdentityAuthenticator;
import com.gradel.parent.component.jwt.security.endpoint.service.AuthenticationService;
import com.gradel.parent.component.jwt.security.authenticator.AIdentityAuthenticatorHolder;
import com.gradel.parent.component.jwt.security.entity.AIdentity;
import com.gradel.parent.component.jwt.security.token.AIdentityAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * AIdentityAuthenticationProvider
 * 身份鉴定提供者
 * @Date 2019/7/17 下午3:08
 * @Author sdeven
 */
public class AIdentityAuthenticationProvider implements AuthenticationProvider {
    private AuthenticationService authenticationService;

    public AIdentityAuthenticationProvider(
            AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * #4 身份验证提供者
     * @param authentication
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        AIdentity identity = (AIdentity) authentication.getDetails();
        AIdentityAuthenticator authenticator = AIdentityAuthenticatorHolder.get(identity);

        return authenticator.authenticate(authentication, authenticationService);
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return AIdentityAuthenticationToken.class.isAssignableFrom(aClass);
    }
}
