package com.gradel.parent.component.web.service;

import java.util.Map;

/**
 * 权限验证安全服务
 */
public interface PermissionsSecurityService {

    /**
     * 手机端根据RefreshToken 获取权限信息
     * @return Map<String,String>
     */
    Map<String,String> checkMobilePermissionsByAccessToken(String accessToken, String url, String method);

    /**
     * web站根据RefreshToken 获取权限信息
     * @return Map<String,String>
     */
    Map<String,String> checkWebPermissionsByAccessToken(String accessToken, String url, String method);

    /**
     * 三方根据RefreshToken 获取权限信息
     * @return Map<String,String>
     */
    Map<String,String> checkSocialPermissionsByAccessToken(String accessToken, String url, String method);





}
