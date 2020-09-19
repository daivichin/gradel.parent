package com.gradel.parent.common.util.util;

import com.gradel.parent.common.util.api.error.CommonErrCodeEnum;
import com.gradel.parent.common.util.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
* User: sdeven.chen.dongwei@gmail.com
 * @date: 2016-03-14
 * @Description:bean工具类
 */
@Slf4j
public abstract class BeanUtil {

    public static <M> M createInstance(Class<M> cls){
        try {
            return cls.newInstance();
        } catch (InstantiationException e) {
            //e.printStackTrace();
            log.error("[{}] Finish handling .\nSome Exception Occur:[{}]", BeanUtil.class.getName(), ExceptionUtil.getAsString(e));
            throw new ServiceException(CommonErrCodeEnum.CREATOBJ_ERROR);
        } catch (IllegalAccessException e) {
            //e.printStackTrace();
            log.error("[{}] Finish handling .\nSome Exception Occur:[{}]", BeanUtil.class.getName(), ExceptionUtil.getAsString(e));
            throw new ServiceException(CommonErrCodeEnum.CREATOBJ_ERROR);
        }
    }

    public static List<Type> getActualType(Class cls){
        List<Type> list = new ArrayList<>();
        ParameterizedType clsType = (ParameterizedType)cls.getGenericSuperclass();
        for (Type t1: clsType.getActualTypeArguments()) {
            list.add(t1);
        }
        return list;
    }

    public static Class getFirstActualType(Class cls){
        List<Type> list = getActualType(cls);
        if(list == null || list.size() == 0){
            return null;
        }else{
            return (Class)list.get(0);
        }
    }

    /**
     * 获取对象的属性值
     * @param obj
     * @param fieldName
     * @return
     */
    public static Object getFieldValue(Object obj,String fieldName){
        Object value = null;
        Object source = obj;
        if(obj != null && StringUtil.isNotBlank(fieldName)){
            try {
                String shortField = fieldName;
                while (StringUtil.isNotBlank(shortField)) {
                    String firstFiled = null;
                    if(shortField.contains(".")){
                        firstFiled = shortField.trim().substring(0, shortField.indexOf('.'));
                        shortField = shortField.trim().substring(shortField.indexOf('.')+1);
                    }else{
                        firstFiled = shortField.trim();
                        shortField = "";
                    }
                    if (StringUtil.isNotBlank(firstFiled)) {
                        Class<?> cls = source.getClass();
                        try {
                            Field field = cls.getDeclaredField(firstFiled.trim());
                            if (field != null) {
                                field.setAccessible(true);
                                source = field.get(source);
                            }
                        } catch (NoSuchFieldException e) {
                            try {
                                Method method = cls.getMethod("get"+String.valueOf(firstFiled.trim().charAt(0)).toUpperCase()+firstFiled.trim().substring(1));
                                if (method != null) {
                                    method.setAccessible(true);
                                    source = method.invoke(source, null);
                                }
                            } catch (NoSuchMethodException e1) {
                                log.error("[{}] Finish handling .\nSome Exception Occur:[{}]", BeanUtil.class.getName(), ExceptionUtil.getAsString(e));
                                throw new ServiceException(CommonErrCodeEnum.BEAN_VLUE_ERROR);
                            }
                        }
                    }else{
                        break;
                    }
                }
                value = source;
            }catch (IllegalAccessException e) {
                log.error("[{}] Finish handling .\nSome Exception Occur:[{}]", BeanUtil.class.getName(), ExceptionUtil.getAsString(e));
                throw new ServiceException(CommonErrCodeEnum.BEAN_VLUE_ERROR);
            } catch (InvocationTargetException e) {
                log.error("[{}] Finish handling .\nSome Exception Occur:[{}]", BeanUtil.class.getName(), ExceptionUtil.getAsString(e));
            }
        }
        return value;
    }

    /**
     * map集合转换obj
     * @param map
     * @param obj
     * @return
     */
    public static Object transMap2Bean(Map<String, Object> map, Object obj) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                if (map.containsKey(key)) {
                    Object value = map.get(key);
                    // 得到property对应的setter方法
                    Method setter = property.getWriteMethod();
                    setter.invoke(obj, value);
                }
            }
        } catch (Exception e) {
            log.error("[{}] Finish handling .\nSome Exception Occur:[{}]", BeanUtil.class.getName(), ExceptionUtil.getAsString(e));
            throw new ServiceException(CommonErrCodeEnum.BEAN_CONVERT_ERROR);
        }

        return obj;

    }

    /**
     * bean转换为map
     * @param obj
     * @return
     */
    public static Map<String, Object> transBean2Map(Object obj) {
        if(obj == null){
            return null;
        }
        Map<String, Object> map = new HashMap<>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                // 过滤class属性
                if (!key.equals("class")) {
                    // 得到property对应的getter方法
                    Method getter = property.getReadMethod();
                    Object value = getter.invoke(obj);
                    map.put(key, value);
                }
            }
        } catch (Exception e) {
            log.error("[{}] Finish handling .\nSome Exception Occur:[{}]", BeanUtil.class.getName(), ExceptionUtil.getAsString(e));
            throw new ServiceException(CommonErrCodeEnum.BEAN_CONVERT_ERROR);
        }
        return map;

    }




}
