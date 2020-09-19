package com.gradel.parent.common.util.api.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @date 2019/01/24 下午9:28
 */
public abstract class StaticFieldUtil {

    public static Object getInstance(String staticField) {
        if (staticField == null) {
            return null;
        }

        // Try to parse static field into class and field.
        int lastDotIndex = staticField.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == staticField.length()) {
            throw new IllegalArgumentException(
                    "staticField must be a fully qualified class plus static field name: " +
                            "e.g. 'example.MyExampleClass.MY_EXAMPLE_FIELD'");
        }
        String className = staticField.substring(0, lastDotIndex);
        String fieldName = staticField.substring(lastDotIndex + 1);
        Class<?> targetClass = null;
        try {
            targetClass = Class.forName(className);
            Field fieldObject = targetClass.getField(fieldName);
            if (fieldObject == null) {
                throw new IllegalArgumentException("staticField must be a fully qualified class plus static field name: e.g. 'example.MyExampleClass.MY_EXAMPLE_FIELD'");
            }
            makeAccessible(fieldObject);
            // class field
            return fieldObject.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    public static void makeAccessible(Field field) {
        if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers()) || Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
            field.setAccessible(true);
        }

    }

}
