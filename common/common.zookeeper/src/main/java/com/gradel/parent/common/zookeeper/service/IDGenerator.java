package com.gradel.parent.common.zookeeper.service;

/**
 * IDGenerator
 *
 * @Date 2020/2/17 下午6:13
 * @Author  sdeven.chen.dongwei@gmail.com
 */
public interface IDGenerator {
    long nextId(String name);
    long nextId();
}
