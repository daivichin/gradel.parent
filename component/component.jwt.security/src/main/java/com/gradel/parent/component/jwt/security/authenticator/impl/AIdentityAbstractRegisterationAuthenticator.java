package com.gradel.parent.component.jwt.security.authenticator.impl;

import com.gradel.parent.component.jwt.security.authenticator.AIdentityAuthenticator;
import com.gradel.parent.component.jwt.security.endpoint.service.AuthenticationService;
import com.gradel.parent.component.jwt.security.entity.AIdentifierType;
import com.gradel.parent.component.jwt.security.entity.AIdentity;
import com.gradel.parent.component.jwt.security.token.AIdentityAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.List;

/**
 * AIdentityAbstractSocialAuthenticator
 * 微信 openId、QQ openId、小程序 openId 抽象注册认证器
 * @Date 2019/7/17 下午8:36
 * @Author sdeven
 */
public abstract class AIdentityAbstractRegisterationAuthenticator implements AIdentityAuthenticator {
    @Override
    public Authentication authenticate(
            Authentication authentication,
            AuthenticationService authenticationService) throws AuthenticationException {
        AIdentity aIdentity = (AIdentity) authentication.getDetails();
        if (null == aIdentity.getData()) {
            throw new BadCredentialsException("Authentication Failed");
        }
        Long userId = null;
        /**
         * QQ 应用 openId、微信 应用 openId 注册
         */
        if (AIdentifierType.QQ_APP_OPENID.ordinal() == aIdentity.getIdentifierType()
                || AIdentifierType.WX_APP_OPENID.ordinal() == aIdentity.getIdentifierType()) {
            String mobile = String.valueOf(aIdentity.getData().get("mobile"));
            String validCode = String.valueOf(aIdentity.getData().get("validCode"));
            if (null != mobile && null != validCode && validCode.equals(authenticationService.queryValidCode(mobile))) {
                userId = authenticationService.newUserWithMobile(aIdentity.getCredentialValue(), mobile, aIdentity.getInvitationCode());
            }
            else {
                throw new BadCredentialsException("Authentication Failed");
            }
        }
        /**
         * 微信 小程序应用 openId 注册
         */
        else if (AIdentifierType.WX_XCX_OPENID.ordinal() == aIdentity.getIdentifierType()) {
            userId = authenticationService.newUserWithSocial(aIdentity.getCredentialValue(), aIdentity.getData(), aIdentity.getInvitationCode());
        }
        else {
            throw new BadCredentialsException("Authentication Failed");
        }
        aIdentity.setUserId(userId);

        List<String> permissionIdList = authenticationService.queryPermissionIdList(aIdentity.getUserId());
        return new AIdentityAuthenticationToken(aIdentity, permissionIdList);
    }
}
