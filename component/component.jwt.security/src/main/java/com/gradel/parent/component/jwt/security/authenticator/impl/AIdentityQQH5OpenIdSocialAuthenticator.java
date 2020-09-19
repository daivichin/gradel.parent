package com.gradel.parent.component.jwt.security.authenticator.impl;

import com.gradel.parent.component.jwt.security.authenticator.AIdentityAuthenticator;
import com.gradel.parent.component.jwt.security.entity.ACredentialType;
import com.gradel.parent.component.jwt.security.entity.AIdentity;
import com.gradel.parent.component.jwt.security.entity.AIdentifierType;

/**
 * AIdentityQQH5OpenIdSocialAuthenticator
 * QQ H5 openId 三方认证
 * @Date 2019/7/17 下午8:36
 * @Author sdeven
 */
public class AIdentityQQH5OpenIdSocialAuthenticator extends AIdentityAbstractSocialAuthenticator implements AIdentityAuthenticator {

    @Override
    public AIdentity supports() {
        AIdentity aIdentity = new AIdentity();
        aIdentity.setIdentifierType(AIdentifierType.QQ_H5_OPENID.ordinal());
        aIdentity.setCredentialType(ACredentialType.GRANT_CODE.ordinal());
        return aIdentity;
    }
}
