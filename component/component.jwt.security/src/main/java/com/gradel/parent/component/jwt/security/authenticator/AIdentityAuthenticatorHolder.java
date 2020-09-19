package com.gradel.parent.component.jwt.security.authenticator;

import com.gradel.parent.component.jwt.security.entity.AIdentity;
import com.gradel.parent.component.jwt.security.authenticator.impl.*;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * AIdentityAuthenticatorHolder
 * 身份认证bean 厂
 * @Date 2019/7/17 下午3:08
 * @Author sdeven
 */
public class AIdentityAuthenticatorHolder {

    private static final Map<String, AIdentityAuthenticator> map = Stream.of(
            new AIdentityMobilePasswordAuthenticator(),
            new AIdentityMobileValidCodeAuthenticator(),

            new AIdentityWXH5OpenIdSocialAuthenticator(),
            new AIdentityWXAPPOpenIdSocialAuthenticator(),
            new AIdentityWXXCXOpenIdSocialAuthenticator(),
            new AIdentityWXSRVOpenIdSocialAuthenticator(),
            new AIdentityWXSUBOpenIdSocialAuthenticator(),
            new AIdentityQQH5OpenIdSocialAuthenticator(),
            new AIdentityQQAPPOpenIdSocialAuthenticator(),

            new AIdentityWXH5OpenIdRegisterationAuthenticator(),
            new AIdentityWXAPPOpenIdRegisterationAuthenticator(),
            new AIdentityWXXCXOpenIdRegisterationAuthenticator(),
            new AIdentityWXSRVOpenIdRegisterationAuthenticator(),
            new AIdentityWXSUBOpenIdRegisterationAuthenticator(),
            new AIdentityQQH5OpenIdRegisterationAuthenticator(),
            new AIdentityQQAPPOpenIdRegisterationAuthenticator())
            .collect(Collectors.toMap(o -> o.supports().getIdentityAuthenticatorType(), o -> o));

    public static AIdentityAuthenticator get(AIdentity identity) {
        return map.get(identity.getIdentityAuthenticatorType());
    }
}
