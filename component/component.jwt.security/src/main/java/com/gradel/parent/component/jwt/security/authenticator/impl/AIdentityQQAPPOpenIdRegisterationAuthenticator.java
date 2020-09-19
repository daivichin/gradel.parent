package com.gradel.parent.component.jwt.security.authenticator.impl;

import com.gradel.parent.component.jwt.security.authenticator.AIdentityAuthenticator;
import com.gradel.parent.component.jwt.security.entity.ACredentialType;
import com.gradel.parent.component.jwt.security.entity.AIdentifierType;
import com.gradel.parent.component.jwt.security.entity.AIdentity;

/**
 * AIdentityQQAppOpenIdSocialAuthenticator
 * QQ应用openId三方认证
 * @Date 2019/7/17 下午8:36
 * @Author sdeven
 */
public class AIdentityQQAPPOpenIdRegisterationAuthenticator extends AIdentityAbstractRegisterationAuthenticator implements AIdentityAuthenticator {

    @Override
    public AIdentity supports() {
        AIdentity aIdentity = new AIdentity();
        aIdentity.setIdentifierType(AIdentifierType.QQ_APP_OPENID.ordinal());
        aIdentity.setCredentialType(ACredentialType.REGISTERATION.ordinal());
        return aIdentity;
    }
}
