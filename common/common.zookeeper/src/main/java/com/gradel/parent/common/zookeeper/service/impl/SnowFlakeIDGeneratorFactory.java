package com.gradel.parent.common.zookeeper.service.impl;

import com.gradel.parent.common.zookeeper.configuration.IDGeneratorProperties;
import com.gradel.parent.common.zookeeper.service.IDGenerator;
import com.gradel.parent.common.zookeeper.service.ZookeeperService;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SnowFlakeIDGeneratorFactory implements IDGenerator {
    private ZookeeperService zkService;
    private IDGeneratorProperties properties;
    private Map<String, SnowFlakeIDGenerator> map;
    private final static String GLOBAL_BUSINESS_NAME = "__GLOBAL_BUSINESS";

    public SnowFlakeIDGeneratorFactory(ZookeeperService zkService, IDGeneratorProperties properties) {
        this.zkService = zkService;
        this.properties = properties;
        this.map = new ConcurrentHashMap<>();
    }

    @Override
    public long nextId(String name) {
        SnowFlakeIDGenerator snowFlakeIDGenerator = map.computeIfAbsent(name, o -> {
            try {
                return new SnowFlakeIDGenerator(zkService, properties, name);
            } catch (Exception e) {
                throw new IllegalArgumentException(String.format("SnowFlakeIDGenerator[%s] init failed", name));
            }
        });
        return snowFlakeIDGenerator.nextId();
    }

    @Override
    public long nextId() {
        return nextId(GLOBAL_BUSINESS_NAME);
    }
}
