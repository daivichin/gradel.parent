package com.gradel.parent.component.jwt.security.endpoint.entity;

/**
 * ClassName: APermission
 * Function:  APermission
 * 权限
 * @date:      2019/6/17 下午2:48
 * Author     sdeven
 * Version    V1.0
 */

public interface APermission {
    String getPermissionId();
    String getRequestUrl();
    String getRequestMethod();
}
