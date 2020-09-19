package com.gradel.parent.common.util.util;

import com.gradel.parent.common.util.api.error.CommonErrCodeEnum;
import com.gradel.parent.common.util.constants.CommonConstants;
import com.gradel.parent.common.util.exception.ServiceException;
import com.gradel.parent.common.util.threadlocal.SerialNo;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
* User: sdeven.chen.dongwei@gmail.com
 * @date: 2016/3/3
 * @Description:jdk 自带的序列化工具
 * readObject,writeObject(transient/static字段)
 */
@Slf4j
public class SerializerUtilForJdk {

    /**
     * 反序列化
     *
     * @param bytes
     * @return
     */
    public static <T> T unserialize(byte[] bytes) {
        if (isEmpty(bytes)) {
            return null;
        }

        Object result = null;
        try {
            ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
            try {
                ObjectInputStream objectInputStream = new ObjectInputStream(byteStream);
                try {
                    result = objectInputStream.readObject();
                } catch (ClassNotFoundException ex) {
                    log.error("[{}] Failed to deserialize object type, Some Exception Occur:[{}]", SerialNo.getSerialNo(), ExceptionUtil.getAsString(ex));
                    throw new ServiceException(CommonErrCodeEnum.BEAN_CONVERT_ERROR);
                }
            } catch (Throwable ex) {
                log.error("[{}] Failed to deserialize, Some Exception Occur:[{}]", SerialNo.getSerialNo(), ExceptionUtil.getAsString(ex));
                throw new ServiceException(CommonErrCodeEnum.BEAN_CONVERT_ERROR);
            }
        } catch (Exception e) {
            log.error("[{}] Failed to deserialize, Some Exception Occur:[{}]", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
            throw new ServiceException(CommonErrCodeEnum.BEAN_CONVERT_ERROR);
        }
        return (T) result;
    }

    private static boolean isEmpty(byte[] data) {
        return (data == null || data.length == 0);
    }

    /**
     * 序列化
     *
     * @param object
     * @return
     */
    public static byte[] serialize(Object object) {
        if (object == null) {
            return CommonConstants.EMPTY_BYTES;
        }
        byte[] result = null;
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream(128);
            try {
                if (!(object instanceof Serializable)) {
                    log.error("[{}] {} requires a Serializable payload but received an object of type [{}]", SerialNo.getSerialNo(),
                            SerializerUtilForJdk.class.getSimpleName(), object.getClass().getName());
                    throw new ServiceException(CommonErrCodeEnum.BEAN_CONVERT_ERROR);
                }
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteStream);
                objectOutputStream.writeObject(object);
                objectOutputStream.flush();
                result = byteStream.toByteArray();
            } catch (Throwable ex) {
                log.error("[{}] Failed to serialize, Some Exception Occur:[{}]", SerialNo.getSerialNo(), ExceptionUtil.getAsString(ex));
                throw new ServiceException(CommonErrCodeEnum.BEAN_CONVERT_ERROR);
            }
        } catch (Exception ex) {
            log.error("[{}] Failed to serialize, Some Exception Occur:[{}]", SerialNo.getSerialNo(), ExceptionUtil.getAsString(ex));
            throw new ServiceException(CommonErrCodeEnum.BEAN_CONVERT_ERROR);
        }
        return result;
    }

}
