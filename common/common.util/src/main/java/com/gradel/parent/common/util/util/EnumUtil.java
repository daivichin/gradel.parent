package com.gradel.parent.common.util.util;

import org.apache.commons.lang3.EnumUtils;

import java.util.EnumSet;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @date: 2016/2/22
 * Time: 12:43
 */
public abstract class EnumUtil {
    /**
     * 从指定的枚举类中根据property搜寻匹配指定值的枚举实例
     *
     * @param <T> 指定的枚举类型
     * @param enumClass 指定的枚举类型
     * @param property 指定属性
     * @param propValue 指定属性的值
     * @return 枚举对象实例
     */
    public static <T extends Enum<T>> T fromEnumValue(Class<T> enumClass, String property, Object propValue) {
        T[] enumConstants = enumClass.getEnumConstants();
        String methodName = "get" + StringUtil.capitalize(property);
        for (T t : enumConstants) {
            Object constantPropValue;
            try {
                constantPropValue = t.getDeclaringClass().getDeclaredMethod(methodName).invoke(t);
                if (constantPropValue.equals(propValue)) {
                    return t;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        throw new EnumConstantNotPresentException(enumClass, String.valueOf(propValue));
    }

    /**
     * 通过枚举对象获取指定属性的值
     *
     * @param enumObject 枚举对象
     * @param property   枚举对象中的属性
     * @return 属性对应值
     */
    public static String getEnumProperty(Enum enumObject, String property) {
        String methodName = "get" + StringUtil.capitalize(property);
        Object constantPropValue;
        try {
            constantPropValue = enumObject.getDeclaringClass().getDeclaredMethod(methodName).invoke(enumObject);
        } catch (Exception e) {
            throw new RuntimeException(
                    String.format("could not found settable filed [%s] in [%s]", property, enumObject.getDeclaringClass())
                    , e);
        }

        return constantPropValue.toString();
    }

    /**
     * 通过枚举对象获取枚举描述值（desc)
     *
     * @param enumObject 枚举对象
     * @return 枚举描述值
     */
    public static String getEnumDesc(Enum enumObject) {
        return getEnumProperty(enumObject, "desc");
    }

    /**
     * 通过枚举对象获取枚举描述值（desc)
     *
     * @param code 枚举对象
     * @return 枚举描述值
     */
    public static <T extends Enum<T>> String getEnumDescByCode(String code, Class<T> clazz) {
        Enum<?> enumObject = null;
        try {
            enumObject = EnumUtil.fromEnumValue(clazz, "code", code.trim());
        } catch (EnumConstantNotPresentException ex) {
            return code;
        }
        return getEnumDesc(enumObject);
    }

    public static <T extends Enum<T>> T resolveFromCode(String code, Class<T> clazz) {
        return EnumUtil.fromEnumValue(clazz, "code", code.trim());
    }

    /**
     * 将若干个枚举值转换为long(按bits 1,2,4,8...的方式叠加)，用于使用long保存多个选项的情况.
     */
    public static <E extends Enum<E>> long generateBits(final Class<E> enumClass, final Iterable<? extends E> values) {
        return EnumUtils.generateBitVector(enumClass, values);
    }

    /**
     * 将若干个枚举值转换为long(按bits 1,2,4,8...的方式叠加)，用于使用long保存多个选项的情况.
     */
    public static <E extends Enum<E>> long generateBits(final Class<E> enumClass, final E... values) {
        return EnumUtils.generateBitVector(enumClass, values);
    }

    /**
     * long重新解析为若干个枚举值，用于使用long保存多个选项的情况.
     */
    public static <E extends Enum<E>> EnumSet<E> processBits(final Class<E> enumClass, final long value) { // NOSONAR
        return EnumUtils.processBitVector(enumClass, value);
    }
}
