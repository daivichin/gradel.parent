package com.gradel.parent.component.jwt.security.jwt;

import com.auth0.jwt.interfaces.DecodedJWT;

/**
 * AJWTService
 * JWT 服务
 * @Date 2019/7/18 下午5:28
 * @Author sdeven
 */
public interface AJWTService {
    /**
     * 创建token
     * @return
     */
    String create();
    /**
     * 刷新token
     * @return
     */
    String refresh();
    /**
     * 删除token
     * @return
     */
    void remove(String token);

    /**
     * 验证token
     * @param token
     * @param ip
     * @return
     */
    DecodedJWT validate(String token, String ip);

    default void onSuccess() {}
}
