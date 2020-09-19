package com.gradel.parent.component.jwt.security.entity;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Data;

import java.util.Map;

/**
 * AIdentity
 *
 * @Date 2019/7/17 上午10:32
 * @Author sdeven
 */
@Data
public class AIdentity {
    private Long userId;
    /**
     * 唯一码类型
     */
    private Integer identifierType;
    /**
     * 唯一码值
     */
    private String identifierValue;

    /**
     * 凭据类型
     */
    private Integer credentialType;
    /**
     * 凭据值
     */
    private String credentialValue;

    /**
     * 数据包
     */
    private Map<String, Object> data;

    /**
     * 邀请码
     */
    private String invitationCode;
    /**
     * ip地址
     */
    private String ip;

    /**
     * token
     */
    private String token;
    /**
     * 刷新token时间
     */
    private Long refreshTokenTime;
    /**
     * 创建token时间
     */
    private Long createTokenTime;
    /**
     * token过期时间
     */
    private Long expireTokenTime;

    public String getIdentityAuthenticatorType() {
        return String.format("%s_%s", identifierType, credentialType);
    }
}
