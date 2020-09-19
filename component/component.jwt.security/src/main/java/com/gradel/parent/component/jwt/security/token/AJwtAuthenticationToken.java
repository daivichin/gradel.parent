package com.gradel.parent.component.jwt.security.token;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.gradel.parent.component.jwt.security.entity.AGrantedAuthority;
import com.gradel.parent.component.jwt.security.entity.AIdentity;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AJwtAuthenticationToken
 * JWT鉴定Token
 * @Date 2019/7/17 下午2:15
 * @Author sdeven
 */
public class AJwtAuthenticationToken extends AbstractAuthenticationToken {
    private String ip;

    public AJwtAuthenticationToken(String jwt, String ip) {
        super(null);
        this.setAuthenticated(false);
        this.setDetails(jwt);
        this.ip = ip;
    }
    public AJwtAuthenticationToken(String token, DecodedJWT jwt) {
        super(jwt.getClaim("authority").asList(String.class).stream().map(AGrantedAuthority::new).collect(Collectors.toList()));
        this.setAuthenticated(true);
        this.ip = jwt.getClaim("ip").asString();

        List<String> audiences = jwt.getAudience();
        Long userId = Long.valueOf(jwt.getSubject());
        int identifierType = Integer.parseInt(audiences.get(0));
        String identifierValue = audiences.get(1);

        AIdentity identity = new AIdentity();
        identity.setIp(this.ip);
        identity.setUserId(userId);
        identity.setIdentifierType(identifierType);
        identity.setIdentifierValue(identifierValue);


        identity.setCreateTokenTime(jwt.getIssuedAt().getTime());
        identity.setRefreshTokenTime(jwt.getClaim("refreshTime").asLong());
        identity.setExpireTokenTime(jwt.getExpiresAt().getTime());
        identity.setToken(token);

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

    public String getIp() {
        return this.ip;
    }
}
