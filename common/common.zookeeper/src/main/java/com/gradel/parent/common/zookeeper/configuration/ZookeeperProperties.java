package com.gradel.parent.common.zookeeper.configuration;

import lombok.Data;

/**
 * ZookeeperProperties
 *
 * @Date 2020/2/17 下午4:37
 * @Author  sdeven.chen.dongwei@gmail.com
 */
@Data
public class ZookeeperProperties {
    private Integer baseSleepTimeMs;
    private Integer maxRetries;
    private Boolean enabled;
    private String url;
    private String scheme;
    private String auth;
    private Integer sessionTimeoutMs;
    private Integer connectionTimeoutMs;
    private String namespace;
    private Integer dataCenterId;
}
