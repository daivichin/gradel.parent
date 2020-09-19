package com.gradel.parent.common.zookeeper.configuration;

import lombok.Data;

/**
 * IDGeneratorProperties
 *
 * @Date 2020/2/17 下午4:37
 * @Author  sdeven.chen.dongwei@gmail.com
 */
@Data
public class IDGeneratorProperties {
    private Integer dataCenterId;
    private String zkIdAppPath;
}
