package com.gradel.parent.component.jwt.security.authenticator.impl;

import com.gradel.parent.component.jwt.security.authenticator.AIdentityAuthenticator;
import com.gradel.parent.component.jwt.security.endpoint.entity.AIdentifier;
import com.gradel.parent.component.jwt.security.endpoint.entity.ASocialDetail;
import com.gradel.parent.component.jwt.security.endpoint.service.AuthenticationService;
import com.gradel.parent.component.jwt.security.entity.AIdentity;
import com.gradel.parent.component.jwt.security.exception.ANotRegisterException;
import com.gradel.parent.component.jwt.security.social.ASocialProvider;
import com.gradel.parent.component.jwt.security.social.ASocialProviderHolder;
import com.gradel.parent.component.jwt.security.token.AIdentityAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.List;

/**
 * AIdentityAbstractSocialAuthenticator
 * 三方身份抽象认证器
 * @Date 2019/7/17 下午8:36
 * @Author sdeven
 */
public abstract class AIdentityAbstractSocialAuthenticator implements AIdentityAuthenticator {
    @Override
    public Authentication authenticate(
            Authentication authentication,
            AuthenticationService authenticationService) throws AuthenticationException {
        AIdentity aIdentity = (AIdentity) authentication.getDetails();
        if (null == aIdentity.getIdentifierValue() || null == aIdentity.getCredentialValue()) {
            throw new BadCredentialsException("Authentication Failed");
        }
        int socialType = Integer.parseInt(aIdentity.getIdentifierValue());
        ASocialProvider socialProvider = ASocialProviderHolder.get(socialType);
        ASocialDetail socialDetail = socialProvider.querySocialDetail(aIdentity.getCredentialValue());
        if (null == socialDetail || null == socialDetail.getOpenid()) {
            throw new BadCredentialsException("Authentication Failed");
        }

        AIdentifier uIdentifier =  authenticationService.queryIdentifier(aIdentity.getIdentifierType(), socialDetail.getOpenid());

        if (null == uIdentifier) {
            String registeration = authenticationService.newRegisteration(aIdentity.getIdentifierType(), socialDetail);
            throw new ANotRegisterException(registeration, "Authentication Failed");
        }
        else {
            authenticationService.updateUserWithSocial(aIdentity.getIdentifierType(), socialDetail);
            aIdentity.setUserId(uIdentifier.getUserId());

            List<String> permissionIdList = authenticationService.queryPermissionIdList(aIdentity.getUserId());
            return new AIdentityAuthenticationToken(aIdentity, permissionIdList);
        }
    }
}
