package com.gradel.parent.common.zookeeper.service.impl;

import com.gradel.parent.common.zookeeper.service.ZookeeperService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;
import org.apache.zookeeper.CreateMode;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * ZookeeperServiceImpl
 *
 * @Date 2020/2/17 下午5:21
 * @Author  sdeven.chen.dongwei@gmail.com
 */
@Slf4j
public class ZookeeperServiceImpl implements ZookeeperService {
    private CuratorFramework zkClient;

    public ZookeeperServiceImpl(CuratorFramework zkClient) {
        this.zkClient = zkClient;
    }

    @Override
    public boolean isExistNode(String path) {
        zkClient.sync() ;
        try {
            return zkClient.checkExists().forPath(path) != null;
        } catch (Exception e) {
            log.error("isExistNode error...", e);
            e.printStackTrace();
        }
        return false;
    }
    @Override
    public String createNode(CreateMode mode, String path) {
        try {
            // 递归创建所需父节点
            return zkClient.create().creatingParentsIfNeeded().withMode(mode).forPath(path);
        } catch (Exception e) {
            log.error("createNode error...", e);
            return null;
        }
    }

    @Override
    public String createNode(CreateMode mode, String path, String nodeData) {
        try {
            // 创建节点，关联数据
            return zkClient.create().creatingParentsIfNeeded().withMode(mode)
                    .forPath(path,nodeData.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("createNode error...", e);
            return null;
        }
    }
    @Override
    public void setNodeData(String path, String nodeData) {
        try {
            // 设置节点数据
            zkClient.setData().forPath(path, nodeData.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("setNodeData error...", e);
            e.printStackTrace();
        }
    }
    @Override
    public String getNodeData(String path) {
        try {
            // 数据读取和转换
            zkClient.sync();
            byte[] dataByte = zkClient.getData().forPath(path) ;
            String data = new String(dataByte,StandardCharsets.UTF_8) ;
            if (StringUtils.isNotEmpty(data)){
                return data ;
            }
        }catch (Exception e) {
            log.error("getNodeData error...", e);
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public List<String> getNodeChild(String path) {
        List<String> nodeChildDataList = new ArrayList<>();
        try {
            // 节点下数据集
            nodeChildDataList = zkClient.getChildren().forPath(path);
        } catch (Exception e) {
            log.error("getNodeChild error...", e);
            e.printStackTrace();
        }
        return nodeChildDataList;
    }
    @Override
    public void deleteNode(String path, Boolean recursive) {
        try {
            if(recursive) {
                // 递归删除节点
                zkClient.delete().guaranteed().deletingChildrenIfNeeded().forPath(path);
            } else {
                // 删除单个节点
                zkClient.delete().guaranteed().forPath(path);
            }
        } catch (Exception e) {
            log.error("deleteNode error...", e);
            e.printStackTrace();
        }
    }
    @Override
    public InterProcessReadWriteLock getReadWriteLock(String path) {
        // 写锁互斥、读写互斥
        return new InterProcessReadWriteLock(zkClient, path);
    }
}

