package com.gradel.parent.component.jwt.security.authenticator.impl;

import com.gradel.parent.component.jwt.security.endpoint.service.AuthenticationService;
import com.gradel.parent.component.jwt.security.authenticator.AIdentityAuthenticator;
import com.gradel.parent.component.jwt.security.endpoint.entity.AIdentifier;
import com.gradel.parent.component.jwt.security.entity.ACredentialType;
import com.gradel.parent.component.jwt.security.entity.AIdentifierType;
import com.gradel.parent.component.jwt.security.entity.AIdentity;
import com.gradel.parent.component.jwt.security.token.AIdentityAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.List;

/**
 * AIdentityMobileValidCodeAuthenticator
 * 手机邀请码认证
 * @Date 2019/7/17 下午8:36
 * @Author sdeven
 */
public class AIdentityMobileValidCodeAuthenticator  implements AIdentityAuthenticator {
    @Override
    public Authentication authenticate(
            Authentication authentication,
            AuthenticationService authenticationService) throws AuthenticationException {
        AIdentity aIdentity = (AIdentity) authentication.getDetails();

        String mobile = aIdentity.getIdentifierValue();
        String validCode = authenticationService.queryValidCode(mobile);
        if (null == validCode || !validCode.equals(aIdentity.getCredentialValue())) {
            throw new BadCredentialsException("Authentication Failed");
        }

        AIdentifier uIdentifier =  authenticationService.queryIdentifier(aIdentity.getIdentifierType(), mobile);

        if (null == uIdentifier) {
            Long userId = authenticationService.newUserWithMobile(mobile, aIdentity.getInvitationCode());
            aIdentity.setUserId(userId);
        }
        else {
            aIdentity.setUserId(uIdentifier.getUserId());
        }
        List<String> permissionIdList = authenticationService.queryPermissionIdList(aIdentity.getUserId());
        return new AIdentityAuthenticationToken(aIdentity, permissionIdList);
    }

    @Override
    public AIdentity supports() {
        AIdentity aIdentity = new AIdentity();
        aIdentity.setIdentifierType(AIdentifierType.MOBILE.ordinal());
        aIdentity.setCredentialType(ACredentialType.VALID_CODE.ordinal());
        return aIdentity;
    }
}
