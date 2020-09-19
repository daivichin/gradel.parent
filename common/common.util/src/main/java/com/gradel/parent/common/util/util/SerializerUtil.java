package com.gradel.parent.common.util.util;

import com.gradel.parent.common.util.api.error.CommonErrCodeEnum;
import com.gradel.parent.common.util.exception.ServiceException;
import com.gradel.parent.common.util.threadlocal.SerialNo;
import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import lombok.extern.slf4j.Slf4j;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

/**
* User: sdeven.chen.dongwei@gmail.com
 * @date: 2016/3/3
 * @Description:protostuff序列化工具（高效）（系统的第一次执行会慢点，因为要编译）
 * 不支持序列化transient/static字段
 * 若需要支持
 *
 * @see SerializerUtilForJdk
 */
@Slf4j
public class SerializerUtil {

    private static ConcurrentHashMap<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<Class<?>, Schema<?>>();

    public static <T> byte[] serialize(final T source) {
        VO<T> vo = new VO<T>(source);
        final LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            final Schema<VO> schema = getSchema(VO.class);
            return serializeInternal(vo, schema, buffer);
        } catch (final Throwable e) {
            log.error("[{}] [{}] Finish handling .\nSome Exception Occur:[{}]", SerialNo.getSerialNo(), SerializerUtil.class.getName(), ExceptionUtil.getAsString(e));
            throw new ServiceException(CommonErrCodeEnum.BEAN_CONVERT_ERROR);
        } finally {
            buffer.clear();
        }
    }

    public static <T> T unserialize(final byte[] bytes) {
        try {
            Schema<VO> schema = getSchema(VO.class);
            VO vo = deserializeInternal(bytes, schema.newMessage(), schema);
            if (vo != null && vo.getValue() != null) {
                return (T) vo.getValue();
            }
        } catch (final Throwable e) {
            log.error("[{}] [{}] Finish handling .\nSome Exception Occur:[{}]", SerialNo.getSerialNo(), SerializerUtil.class.getName(), ExceptionUtil.getAsString(e));
            throw new ServiceException(CommonErrCodeEnum.BEAN_CONVERT_ERROR);
        }
        return null;
    }

    private static <T> byte[] serializeInternal(final T source, final Schema<T> schema, final LinkedBuffer buffer) {
        return ProtostuffIOUtil.toByteArray(source, schema, buffer);
    }

    private static <T> T deserializeInternal(final byte[] bytes, final T result, final Schema<T> schema) {
        ProtostuffIOUtil.mergeFrom(bytes, result, schema);
        return result;
    }

    private static <T> Schema<T> getSchema(Class<T> clazz) {
        Schema<T> schema = (Schema<T>) cachedSchema.get(clazz);
        if (schema == null) {
            schema = RuntimeSchema.createFrom(clazz);
            cachedSchema.put(clazz, schema);
        }
        return schema;
    }

    private static class VO<T> implements Serializable {
        private T value;

        public VO(T value) {
            this.value = value;
        }

        public VO() {
        }

        public T getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "VO{" +
                    "value=" + value +
                    '}';
        }
    }
}
