package com.gradel.parent.component.jwt.security.authenticator.impl;

import com.gradel.parent.component.jwt.security.authenticator.AIdentityAuthenticator;
import com.gradel.parent.component.jwt.security.entity.ACredentialType;
import com.gradel.parent.component.jwt.security.entity.AIdentifierType;
import com.gradel.parent.component.jwt.security.entity.AIdentity;

/**
 * AIdentityQQAppOpenIdSocialAuthenticator
 * 微信小程序openId注册认证
 * @Date 2019/7/17 下午8:36
 * @Author sdeven
 */
public class AIdentityWXXCXOpenIdRegisterationAuthenticator extends AIdentityAbstractRegisterationAuthenticator implements AIdentityAuthenticator {

    @Override
    public AIdentity supports() {
        AIdentity aIdentity = new AIdentity();
        aIdentity.setIdentifierType(AIdentifierType.WX_XCX_OPENID.ordinal());
        aIdentity.setCredentialType(ACredentialType.REGISTERATION.ordinal());
        return aIdentity;
    }
}
