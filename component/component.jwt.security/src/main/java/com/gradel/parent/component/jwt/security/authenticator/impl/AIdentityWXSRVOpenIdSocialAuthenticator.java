package com.gradel.parent.component.jwt.security.authenticator.impl;

import com.gradel.parent.component.jwt.security.authenticator.AIdentityAuthenticator;
import com.gradel.parent.component.jwt.security.entity.ACredentialType;
import com.gradel.parent.component.jwt.security.entity.AIdentifierType;
import com.gradel.parent.component.jwt.security.entity.AIdentity;

/**
 * AIdentityWXSRVOpenIdSocialAuthenticator
 *
 * @Date 2019/7/17 下午8:36
 * @Author sdeven
 */
public class AIdentityWXSRVOpenIdSocialAuthenticator extends AIdentityAbstractSocialAuthenticator implements AIdentityAuthenticator {

    @Override
    public AIdentity supports() {
        AIdentity aIdentity = new AIdentity();
        aIdentity.setIdentifierType(AIdentifierType.WX_SRV_OPENID.ordinal());
        aIdentity.setCredentialType(ACredentialType.GRANT_CODE.ordinal());
        return aIdentity;
    }
}
