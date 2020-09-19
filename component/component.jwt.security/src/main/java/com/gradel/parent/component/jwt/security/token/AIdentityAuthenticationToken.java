package com.gradel.parent.component.jwt.security.token;

import com.gradel.parent.component.jwt.security.entity.AGrantedAuthority;
import com.gradel.parent.component.jwt.security.entity.AIdentity;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AIdentityAuthenticationToken
 * 身份鉴定Token
 * @Date 2019/7/17 下午2:15
 * @Author sdeven
 */
public class AIdentityAuthenticationToken extends AbstractAuthenticationToken {
    public AIdentityAuthenticationToken(AIdentity identity) {
        super(null);
        this.setAuthenticated(false);
        this.setDetails(identity);
    }
    public AIdentityAuthenticationToken(AIdentity identity, List<String> permissionIdList) {
        super(permissionIdList.stream().map(AGrantedAuthority::new).collect(Collectors.toList()));
        this.setAuthenticated(true);
        this.setDetails(identity);
    }

    @Deprecated
    @Override
    public Object getCredentials() {
        return null;
    }

    @Deprecated
    @Override
    public Object getPrincipal() {
        return null;
    }
}
