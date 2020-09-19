package com.gradel.parent.common.zookeeper.configuration;

import com.gradel.parent.common.zookeeper.service.IDGenerator;
import com.gradel.parent.common.zookeeper.service.ZookeeperService;
import com.gradel.parent.common.zookeeper.service.impl.SnowFlakeIDGeneratorFactory;
import com.gradel.parent.common.zookeeper.service.impl.ZookeeperServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * ZookeeperAutoConfigurer
 *
 * @Date 2020/2/17 下午4:38
 * @Author  sdeven.chen.dongwei@gmail.com
 */
@Configuration
public class ZookeeperAutoConfigurer {

    @ConfigurationProperties("spring.zookeeper.idgenerator")
    @Bean
    public IDGeneratorProperties idGeneratorProperties() { return new IDGeneratorProperties(); }

    @ConfigurationProperties("spring.zookeeper")
    @Bean
    public ZookeeperProperties zookeeperProperties() { return new ZookeeperProperties(); }

    @Bean
    public CuratorFramework zkClient () {
        ZookeeperProperties properties = zookeeperProperties();
        //重试策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(
                properties.getBaseSleepTimeMs(),
                properties.getMaxRetries());
        //通过工厂创建Curator
        CuratorFrameworkFactory.Builder builder   = CuratorFrameworkFactory.builder()
                .connectString(properties.getUrl())
                .retryPolicy(retryPolicy)
                .sessionTimeoutMs(properties.getSessionTimeoutMs())
                .connectionTimeoutMs(properties.getConnectionTimeoutMs())
                .namespace(properties.getNamespace());
        if(StringUtils.isNotEmpty(properties.getScheme()) && StringUtils.isNotEmpty(properties.getAuth())){
            builder.authorization(properties.getScheme(), properties.getAuth().getBytes(StandardCharsets.UTF_8));
            builder.aclProvider(new ACLProvider() {
                @Override
                public List<ACL> getDefaultAcl() {
                    return ZooDefs.Ids.CREATOR_ALL_ACL;
                }

                @Override
                public List<ACL> getAclForPath(final String path) {
                    return ZooDefs.Ids.CREATOR_ALL_ACL;
                }
            });
        }
        CuratorFramework client = builder.build();
        client.start();
        return client;
    }
    @Bean
    public ZookeeperService zkService() { return new ZookeeperServiceImpl(zkClient()); }

    @Bean
    public IDGenerator idGeneratorFactory() {
        return new SnowFlakeIDGeneratorFactory(zkService(), idGeneratorProperties());
    }
}
