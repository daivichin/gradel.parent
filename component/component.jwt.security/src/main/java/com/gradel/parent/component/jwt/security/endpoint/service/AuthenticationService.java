package com.gradel.parent.component.jwt.security.endpoint.service;

import com.gradel.parent.component.jwt.security.endpoint.entity.ACredential;
import com.gradel.parent.component.jwt.security.endpoint.entity.AIdentifier;
import com.gradel.parent.component.jwt.security.endpoint.entity.APermission;
import com.gradel.parent.component.jwt.security.endpoint.entity.ASocialDetail;

import java.util.List;
import java.util.Map;

/**
 * AuthenticationService
 *
 * @Date 2019/8/14 下午2:57
 * @Author sdeven
 */
public interface AuthenticationService {
    // 查询Identifier
    AIdentifier queryIdentifier(Integer identifierType, String identifierValue);
    //保存Identifier，自动注册时需要
    void saveIdentifier(Long userId, Integer identifierType, String identifierValue);

    // 查询Credential
    ACredential queryCredential(Long userId, Integer credentialType);

    // 查询用户的Permission
    List<String> queryPermissionIdList(Long userId);
    // 查询所有的Permission
    List<? extends APermission> queryAllPermission();

    /**
     * 查询ValidCode
     * @param mobile
     * @return
     */

    String queryValidCode(String mobile);

    /**
     * 获取登陆URL
     * @return
     */
    default String getLoginUrl() {
        return "/login";
    }

    /**
     * 获取登出URL
     * @return
     */
    default String getLogoutUrl() {
        return "/logout";
    }

    /**
     * 无需登录就可访问的url
     * @return
     */
    default String[] getPermissiveUrl() {
        return new String[] {
                this.getLoginUrl(),
                this.getLogoutUrl()
        };
    }

    /**
     * 保存社交账号信息，自动注册时需要，可选
     * @param identifierType
     * @param socialDetail
     * @return
     */
    String newRegisteration(Integer identifierType, ASocialDetail socialDetail);

    /**
     * 更新社交账号信息，可选
     * @param identifierType
     * @param socialDetail
     */
    void updateUserWithSocial(Integer identifierType, ASocialDetail socialDetail);

    /**
     * 保存社交账号信息，自动注册时需要，可选
     * @param mobile
     * @param invitationCode
     * @return
     */
    Long newUserWithMobile(String mobile, String invitationCode);
    /**
     * 保存社交账号信息，自动注册时需要，可选
     * @param mobile
     * @param invitationCode
     * @return
     */
    Long newUserWithMobile(String registeration, String mobile, String invitationCode);
    /**
     * 保存社交账号信息，自动注册时需要，可选
     * @param registeration
     * @param socialData
     * @param invitationCode
     * @return
     */
    Long newUserWithSocial(String registeration, Map<String, Object> socialData, String invitationCode);
}
