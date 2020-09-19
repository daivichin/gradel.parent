package com.gradel.parent.common.util.util;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Sets;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @date 2019/01/31 下午1:16
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReflectiveOperationUtil {

    private static final String SET_METHOD_PREFIX = "set";

    private static Collection<Class<?>> generalClassType;

    static {
        generalClassType = Sets.<Class<?>>newHashSet(boolean.class, Boolean.class, int.class, Integer.class, long.class, Long.class, String.class);
    }

    public static <T> T getInstance(final Class<T> instanceClass, final Map<String, Object> properties) throws ReflectiveOperationException {
        return instanceClass.cast(getInstance(instanceClass.getName(), properties));
    }

    /**
     * Get instance.
     *
     * @param instanceClassName instance class name
     * @param properties        instance properties
     * @return instance
     * @throws ReflectiveOperationException reflective operation exception
     */
    public static Object getInstance(final String instanceClassName, final Map<String, Object> properties) throws ReflectiveOperationException {
        Object result = Class.forName(instanceClassName).newInstance();
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            callSetterMethod(result, getSetterMethodName(entry.getKey()), null == entry.getValue() ? null : entry.getValue().toString());
        }
        return result;
    }

    public static String getSetterMethodName(final String propertyName) {
        if (propertyName.contains("-")) {
            return CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL, SET_METHOD_PREFIX + "-" + propertyName);
        }
        return SET_METHOD_PREFIX + String.valueOf(propertyName.charAt(0)).toUpperCase() + propertyName.substring(1, propertyName.length());
    }

    public static void callSetterMethod(final Object instance, final String methodName, final String setterValue) throws NoSuchMethodException {
        for (Class<?> each : generalClassType) {
            try {
                Method method = instance.getClass().getMethod(methodName, each);
                if (boolean.class == each || Boolean.class == each) {
                    method.invoke(instance, Boolean.valueOf(setterValue));
                } else if (int.class == each || Integer.class == each) {
                    method.invoke(instance, Integer.parseInt(setterValue));
                } else if (long.class == each || Long.class == each) {
                    method.invoke(instance, Long.parseLong(setterValue));
                } else {
                    method.invoke(instance, setterValue);
                }
                return;
            } catch (final ReflectiveOperationException ignore) {
            }
        }
        throw new NoSuchMethodException("Can not found " + instance.getClass().getName() + " method[" + methodName + "], setterValue:" + setterValue);
    }
}
