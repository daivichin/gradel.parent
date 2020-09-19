package com.gradel.parent.component.jwt.security.endpoint.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.gradel.parent.component.jwt.security.entity.AIdentity;
import com.gradel.parent.component.jwt.security.jwt.AJWTService;
import com.gradel.parent.component.jwt.security.token.AIdentityAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Date;
import java.util.HashMap;

/**
 * AJWTAbstractService
 *
 * @Date 2019/7/18 下午5:31
 * @Author sdeven
 */

public abstract class AJWTAbstractService implements AJWTService {
    private static final String JWT_TYP = "JWT";
    private static final String JWT_ALG = "HS256";
    private static final String JWT_ISSUER = "sdeven";
    private static final String JWT_HMAC256 = "sdeven";

    protected abstract void saveToken(String id, String token);
    protected abstract void removeToken(String id);
    protected abstract String queryToken(String id);
    protected abstract String getJWTID(AIdentity identity);
    protected abstract int getExpireDays();
    protected abstract int getRefreshDays();

    @Override
    public String create() {
        long now = System.currentTimeMillis();
        return this.build(now, now);
    }

    private String build(long createTime, long beginTime) {
        try {
            AIdentityAuthenticationToken authentication = (AIdentityAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
            AIdentity identity = (AIdentity) authentication.getDetails();

            try {
                long expireTime = beginTime + this.getExpireDays() * 24 * 3600000;
                long refreshTime = expireTime - this.getRefreshDays() * 24 * 3600000;

                String[] audience = new String[]{
                        String.valueOf(identity.getIdentifierType()),
                        identity.getIdentifierValue()
                };
                String jwtID = this.getJWTID(identity);
                String token = JWT.create()
                        /*设置头部信息 Header*/
                        .withHeader(new HashMap<String, Object>(){{
                            put("alg", JWT_ALG);
                            put("typ", JWT_TYP);
                        }})
                        /*设置 载荷 Payload*/
                        .withJWTId(jwtID)
                        .withIssuer(JWT_ISSUER)//签名是由谁生成 例如 服务器
                        .withSubject(String.valueOf(identity.getUserId()))//签名的主题
                        .withAudience(audience)//签名的观众 也可以理解谁接受签名的
                        .withArrayClaim("authority", authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toArray(String[]::new))
                        .withClaim("ip", identity.getIp())

                        .withIssuedAt(new Date(createTime))//生成签名的时间
                        .withClaim("refreshTime", refreshTime)//过期前 签名刷新的时间
                        .withExpiresAt(new Date(expireTime))//签名过期的时间
                        /*签名 Signature */
                        .sign(Algorithm.HMAC256(JWT_HMAC256));

                this.saveToken(jwtID, token);
                return token;
            } catch (JWTCreationException exception){
                exception.printStackTrace();
                return null;
            }
        } catch (JWTCreationException exception){
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public String refresh() {
        AIdentityAuthenticationToken authentication = (AIdentityAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        AIdentity identity = (AIdentity) authentication.getDetails();
        long now = System.currentTimeMillis();
        if (identity.getRefreshTokenTime() < now && now < identity.getExpireTokenTime()) {
            return this.build(identity.getCreateTokenTime(), identity.getRefreshTokenTime());
        }
        else {
            return identity.getToken();
        }
    }

    @Override
    public void remove(String token) {
        DecodedJWT jwt = JWT.require(Algorithm.HMAC256(JWT_HMAC256))
                .withIssuer(JWT_ISSUER)
                .build()
                .verify(token);
        this.removeToken(jwt.getId());
    }

    @Override
    public DecodedJWT validate(String token, String ip) {
        DecodedJWT jwt = JWT.require(Algorithm.HMAC256(JWT_HMAC256))
                .withIssuer(JWT_ISSUER)
                .withClaim("ip", ip)
                .build()
                .verify(token);
        String savedToken = this.queryToken(jwt.getId());
        if (token.equals(savedToken)) {
            return jwt;
        }
        else {
            return null;
        }
    }

}
