package com.gradel.parent.common.util.util;

import com.gradel.parent.common.util.threadlocal.SerialNo;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;

/**
* User: sdeven.chen.dongwei@gmail.com
 * @date: 2016/11/2
 * @Description: IO 关闭工具类
 */
@Slf4j
public final class IOUtil {
    /**
     * Quietly (no exceptions) close Closable resource. In case of error it will
     * be printed to class logger.
     *
     * @param closeable resource to close
     */
    public static void closeQuietly(final Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception exc) {
                log.error("[{}] Unable to close resource: {}, Exception:{}", SerialNo.getSerialNo(), closeable, ExceptionUtil.getAsString(exc));
            }
        }
    }
}
