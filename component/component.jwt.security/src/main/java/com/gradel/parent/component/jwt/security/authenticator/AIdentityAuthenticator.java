package com.gradel.parent.component.jwt.security.authenticator;

import com.gradel.parent.component.jwt.security.entity.AIdentity;
import com.gradel.parent.component.jwt.security.endpoint.service.AuthenticationService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * AIdentityAuthenticator
 * 身份认证
 * @Date 2019/7/17 下午3:08
 * @Author sdeven
 */
public interface AIdentityAuthenticator {

    Authentication authenticate(Authentication authentication,
                                AuthenticationService authenticationService) throws AuthenticationException;

    AIdentity supports();
}
