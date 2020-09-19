package com.gradel.parent.ali.rocketmq.util;

import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.transaction.TransactionStatus;
import com.gradel.parent.ali.rocketmq.model.MessageContext;
import com.gradel.parent.ali.rocketmq.model.transaction.TransactionMessageContext;
import com.gradel.parent.common.util.constants.CommonConstants;
import com.gradel.parent.common.util.threadlocal.SerialNo;
import lombok.extern.slf4j.Slf4j;

import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @date 2019/03/06 下午9:48
 */
@Slf4j
public class MqUtil {
    static ConcurrentHashMap<String, Object> consumerIds = new ConcurrentHashMap<>();

    private final static ThreadLocal<TransactionMessageContext> LOCAL_TRANSACTION_CONTEXT_HOLDER = new ThreadLocal();

    public static int DEFAULT_TRANSACTION_MAX_CHECK_TIME_MILS = 1000 * 60 * 5;//最大的事务回查重试时间，超过这个时间则会回滚

    /**
     * 事务最大回查次数
     */
//    public static int DEFAULT_TRANSACTION_MAX_RETRY_TIMES = 100;//最大的事务回查重试次数，超过这个时间则会回滚

    /**
     * mysql数据默认读超时无限，max_execution_time=0毫秒，所以建议修改默认的写超时为30000
     * 默认65s
     */
    public static int DEFAULT_LOCAL_TRANSACTION_EXECUTE_MAX_TIME_MILS = 1000 * (60 * 1 + 5);//本地事务最大执行时间，超过这个时间则需要回滚

    /**
     * 检查订阅关系是否一致
     * 同一个GID订阅的是两个不同的topic，所以出现了订阅关系不一致的情况
     * @param consumerId
     * @throws IllegalArgumentException
     */
    public static void check(String consumerId) throws IllegalArgumentException {
        if (consumerIds.putIfAbsent(consumerId, CommonConstants.EMPTY_OBJECT) != null) {
            throw new IllegalArgumentException("GroupId(consumerId):" + consumerId + " 订阅关系不一致");
        }
    }

    public static String getConsumerId(Properties properties){
        return properties.getProperty(PropertyKeyConst.GROUP_ID, properties.getProperty(PropertyKeyConst.ConsumerId));
    }

    public static String getProductId(Properties properties){
        return properties.getProperty(PropertyKeyConst.GROUP_ID, properties.getProperty(PropertyKeyConst.ProducerId));
    }

    /**
     * 默认的事务处理方式：重试(Unknow) 或者 回滚(RollbackTransaction)，(比较消息生产时间，默认5分钟)
     * @param messageContext
     * @return
     */
    public static TransactionStatus retryOrRollbackTransactionByCompareBornTimestamp(MessageContext messageContext){
        return retryOrRollbackTransactionByCompareBornTimestamp(System.currentTimeMillis(), messageContext);
    }

    /**
     * 默认的事务处理方式：重试(Unknow) 或者 回滚(RollbackTransaction)，(比较消息生产时间，默认5分钟)
     * @param currentMils 系统的当前时间（毫秒）
     * @param messageContext
     * @return
     */
    public static TransactionStatus retryOrRollbackTransactionByCompareBornTimestamp(long currentMils, MessageContext messageContext){
        //如果消息的生产时间小于系统最大的时间
        if(compareTimeWithTransationMaxTime(currentMils, messageContext)){
            Object[] params = new Object[]{SerialNo.getSerialNo(), messageContext.getMsgID(), messageContext.getUniqMsgId(), messageContext.getTopic(), messageContext.getTag(), messageContext.getKey(), messageContext.getBornTimestamp()};
            log.warn("[{}]Mq transation message, born time is less then transation max time, return unknow(retry). msgId:[{}], uniqMsgId:[{}], topic[{}], tag[{}], key[{}], bornTimestamp:[{}]", params);
            return TransactionStatus.Unknow;
        }else{
            Object[] params = new Object[]{SerialNo.getSerialNo(), messageContext.getMsgID(), messageContext.getUniqMsgId(), messageContext.getTopic(), messageContext.getTag(), messageContext.getKey(), messageContext.getBornTimestamp()};
            log.warn("[{}]Mq transation message, born time is greater then transation max time, return rollback(delete). msgId:[{}], uniqMsgId:[{}], topic[{}], tag[{}], key[{}], bornTimestamp:[{}]", params);
            return TransactionStatus.RollbackTransaction;
        }
    }

    /**
     *
     * @param currentSysMils 系统的当前时间（毫秒）
     * @param msgBornTimestamp 消息的生产时间（毫秒）
     * @return
     */
    public static boolean compareTimeWithTransationMaxTime(long currentSysMils, long msgBornTimestamp){
        return (currentSysMils - msgBornTimestamp) <= DEFAULT_TRANSACTION_MAX_CHECK_TIME_MILS;
    }

    /**
     *
     * @param currentSysMils 系统的当前时间（毫秒）
     * @param messageContext 消息的上下文，消息的生产时间（毫秒）
     * @return
     */
    public static boolean compareTimeWithTransationMaxTime(long currentSysMils, MessageContext messageContext){
        return compareTimeWithTransationMaxTime(currentSysMils, messageContext.getBornTimestamp());
    }

    /******************** 本地事务 ***********************/
    /**
     * 检测本地事务执行时间是否超过默认的最大事务时间，本地事务需要根据返回来的状态判断是否需要回滚当前事务（同时也要回滚事务消息）
     * @param messageContext
     * @return
     */
    public static boolean isNeedToRollbackLocalTransactionByDefaultMaxTransactionTimestamp(TransactionMessageContext messageContext){
        //如果本地事务的执行时间超过系统默认的最大事务执行时间
        if(!compareTimeWithLocalTransationMaxTime(messageContext)){
            Object[] params = new Object[]{SerialNo.getSerialNo(), messageContext.getMsgID(), messageContext.getUniqMsgId(), messageContext.getTopic(), messageContext.getTag(), messageContext.getKey(), messageContext.getBornTimestamp()};
            log.warn("[{}]Mq transation message, local transaction execute time is greater then transation max time, need to rollback(transaction). msgId:[{}], uniqMsgId:[{}], topic[{}], tag[{}], key[{}], bornTimestamp:[{}]", params);
            return true;
        }
        return false;
    }

    /**
     * 检测本地事务执行时间是否超过默认的最大事务时间，本地事务需要根据返回来的状态判断是否需要回滚当前事务（同时也要回滚事务消息）
     * @return
     */
    public static boolean isNeedToRollbackLocalTransactionByTransactionMessageContextHolder(){
        TransactionMessageContext transactionMessageContext = TransactionMessageContextHolder.getTransactionMessageContext();
        return isNeedToRollbackLocalTransactionByDefaultMaxTransactionTimestamp(transactionMessageContext);
    }

    public static boolean compareTimeWithLocalTransationMaxTime(TransactionMessageContext transactionMessageContext){
        return compareTimeWithLocalTransationMaxTime(System.currentTimeMillis(), transactionMessageContext.getCurrentSysTimeMil());
    }

    public static boolean compareTimeWithLocalTransationMaxTime(long currentSysMils, long startExecuteTime){
        return (currentSysMils - startExecuteTime) <= DEFAULT_LOCAL_TRANSACTION_EXECUTE_MAX_TIME_MILS;
    }



    /*public static String getProductId(Properties properties){
        return properties.getProperty(PropertyKeyConst.GROUP_ID, properties.getProperty(PropertyKeyConst.ProducerId));
    }*/
}
