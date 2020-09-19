package com.gradel.parent.ali.rocketmq.util;

import com.gradel.parent.ali.rocketmq.model.transaction.TransactionMessageContext;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 只能用于本地事务执行器（RocketMqLocalTransactionExecuter）主线程里（子线程不可以）
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TransactionMessageContextHolder {
    //
    private static final ThreadLocal<TransactionMessageContext> LOCAL_TRANSACTION_CONTEXT_HOLDER = new ThreadLocal<>();

    public static TransactionMessageContext getTransactionMessageContext() {
        if (LOCAL_TRANSACTION_CONTEXT_HOLDER.get() != null) {
            throw new IllegalStateException("MqUtil.LOCAL_TRANSACTION_CONTEXT_HOLDER has previous value, please clear first.");
        }
        return LOCAL_TRANSACTION_CONTEXT_HOLDER.get();
    }

    public static void clearTransactionMessageContext() {
        LOCAL_TRANSACTION_CONTEXT_HOLDER.remove();
    }

    public static void setTransactionMessageContext(TransactionMessageContext transactionMessageContext) {
        LOCAL_TRANSACTION_CONTEXT_HOLDER.set(transactionMessageContext);
    }
}
