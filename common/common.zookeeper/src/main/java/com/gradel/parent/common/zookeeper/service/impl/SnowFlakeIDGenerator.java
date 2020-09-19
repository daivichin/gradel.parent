package com.gradel.parent.common.zookeeper.service.impl;

import com.gradel.parent.common.zookeeper.configuration.IDGeneratorProperties;
import com.gradel.parent.common.zookeeper.service.ZookeeperService;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.zookeeper.CreateMode;

import java.util.concurrent.TimeUnit;

/**
 * SnowFlake
 *
 * SnowFlake的结构如下(每部分用-分开):<br>
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000 <br>
 * 1位标识，由于long基本类型在Java中是带符号的，最高位是符号位，正数是0，负数是1，所以id一般是正数，最高位是0<br>
 * 41位时间截(毫秒级)，注意，41位时间截不是存储当前时间的时间截，而是存储时间截的差值（当前时间截 - 开始时间截)
 * 得到的值），这里的的开始时间截，一般是我们的id生成器开始使用的时间，由我们程序来指定的（如下下面程序IdWorker类的startTime属性）。
 * 41位的时间截，可以使用69年，年T = (1L << 41) / (1000L * 60 * 60 * 24 * 365) = 69<br>
 * 10位的数据机器位，可以部署在1024个节点，包括5位datacenterId和5位workerId<br>
 * 12位序列，毫秒内的计数，12位的计数顺序号支持每个节点每毫秒(同一机器，同一时间截)产生4096个ID序号<br>
 * 加起来刚好64位，为一个Long型。<br>
 * SnowFlake的优点是，整体上按照时间自增排序，并且整个分布式系统内不会产生ID碰撞(由数据中心ID和机器ID作区分)，
 * 并且效率较高，经测试，SnowFlake每秒能够产生26万ID左右。
 *
 * @Date 2019/8/11 下午3:55
 * @Author  sdeven.chen.dongwei@gmail.com
 */
@Slf4j
public class SnowFlakeIDGenerator {
    // 起始时间戳
    private final static long twepoch = 1577836800000L;// 2020-01-01
    // 机器id所占的位数
    private final static long workerIdBits = 8L;
    // 数据标识id所占的位数
    private final static long dataCenterIdBits = 2L;
    // 序列在id中占的位数
    private final static long sequenceBits = 12L;

    // 机器ID向左移12位
    private final static long workerIdShift = sequenceBits;
    // 数据标识id向左移20位(12+8)
    private final static long dataCenterIdShift = sequenceBits + workerIdBits;
    // 时间截向左移22位(2+8+12)
    private final static long timestampLeftShift = sequenceBits + workerIdBits + dataCenterIdBits;
    // 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095)
    private final static long sequenceMask = ~(-1L << sequenceBits);

    // 工作机器ID(0~2^8)
    private long workerId;
    // 数据中心ID(0~2^2)
    private long dataCenterId;


    // 毫秒内序列(0~4095)
    private long sequence;
    // 上次生成ID的时间截
    private long lastTimestamp;
    // zk 路径
    private String zkWorkerNodePath;
    // 业务
    private String business;

    private ZookeeperService zkService;
    private IDGeneratorProperties properties;

    /**
     * 构造函数
     *
     * @param zkService  zkService
     * @param properties IDGeneratorProperties
     */
    public SnowFlakeIDGenerator(ZookeeperService zkService, IDGeneratorProperties properties, String business) throws Exception {
        // sanity check for workerId
        if (properties.getDataCenterId() > (~(-1L << dataCenterIdBits))) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", (~(-1L << dataCenterIdBits))));
        }

        this.dataCenterId = properties.getDataCenterId();
        this.sequence = 0;
        this.business = business;

        this.zkService = zkService;
        this.properties = properties;

        this.setWorkerId();
    }

    private void setWorkerId() throws Exception {
        String PATH_PERSISTENT = properties.getZkIdAppPath() + "/" + business + "/PERSISTENT/i";
        String PATH_EPHEMERAL = properties.getZkIdAppPath() + "/" + business + "/EPHEMERAL/i";
        String PATH_EPHEMERAL_SEQUENTIAL = properties.getZkIdAppPath() + "/" + business + "/EPHEMERAL_SEQUENTIAL/i";
        String PATH_LOCK = properties.getZkIdAppPath() + "/" + business + "/LOCK/i";

        boolean success = false;
        int n = 1;
        while (n++ < 1000 && !success) {
            String path = zkService.createNode(CreateMode.EPHEMERAL_SEQUENTIAL, PATH_EPHEMERAL_SEQUENTIAL);
            zkService.deleteNode(path, true);

            long id = Long.parseLong(path.trim().substring(PATH_EPHEMERAL_SEQUENTIAL.length())) % (2^workerIdBits);

            if (!zkService.isExistNode(PATH_EPHEMERAL + id)) {
                InterProcessMutex lock = zkService.getReadWriteLock(PATH_LOCK).writeLock();
                if (lock.acquire(3, TimeUnit.SECONDS)) {
                    if (!zkService.isExistNode(PATH_EPHEMERAL + id)) {
                        zkService.createNode(CreateMode.EPHEMERAL, PATH_EPHEMERAL + id);
                        if (zkService.isExistNode(PATH_PERSISTENT + id)) {
                            this.lastTimestamp = Long.parseLong(zkService.getNodeData(PATH_PERSISTENT + id));
                        }
                        else {
                            this.lastTimestamp = timeGen();
                            zkService.createNode(CreateMode.PERSISTENT, PATH_PERSISTENT + id, String.valueOf(0));
                        }
                        this.zkWorkerNodePath = PATH_PERSISTENT + id;
                        this.workerId = id;
                        success = true;
                    }
                    lock.release();
                }
            }
        }
        if (!success) {
            throw new IllegalArgumentException("Worker ID init failed");
        }
    }


    /**
     * 获得下一个ID (该方法是线程安全的)
     *
     * @return SnowflakeId
     */
    public synchronized long nextId() {
        long timestamp = timeGen();

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }

        lastTimestamp = timestamp;
        return ((timestamp - twepoch) << timestampLeftShift) |
                (dataCenterId << dataCenterIdShift) |
                (workerId << workerIdShift) |
                sequence;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     *
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private long timeGen(){
        long timestamp = System.currentTimeMillis();
        if (timestamp < lastTimestamp) {
            timestamp = lastTimestamp + 1;
            zkService.setNodeData(this.zkWorkerNodePath, String.valueOf(timestamp));
        }
        return timestamp;
    }
}
