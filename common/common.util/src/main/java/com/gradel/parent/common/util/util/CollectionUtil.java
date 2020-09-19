package com.gradel.parent.common.util.util;

import com.gradel.parent.common.util.constants.CommonConstants;
import org.apache.commons.beanutils.PropertyUtils;

import java.util.*;

/**
* User: sdeven.chen.dongwei@gmail.com
 * @date: 2016-03-14
 * @Description:集合工具类
 */
public abstract class CollectionUtil {

    /**
     * 判断对象是否为空
     *
     * @param object obj
     * @return true空，false不为空
     */
    public static boolean isEmpty(Object object) {
        return (object == null);
    }

    /**
     * 判断Boolean是否为true
     *
     * @param b obj
     * @return true空，false不为空
     */
    public static boolean isEmpty(Boolean b) {
        return (b == false);
    }

    /**
     * 判断string是否为空
     *
     * @param string string
     * @return true空，false不为空
     */
    public static boolean isEmpty(String string) {
        return (string == null || string.equals(""));
    }

    /**
     * 判断Long值是否为空或者小于1
     *
     * @param l ID
     * @return true空，false不为空
     */
    public static boolean isEmpty(Long l) {
        return (l == null || l < 1L);
    }

    /**
     * 判断Long值是否为空或者小于1
     *
     * @param i ID
     * @return true空，false不为空
     */
    public static boolean isEmpty(Integer i) {
        return (i == null || i < 1);
    }

    /**
     * 判断Collection是否为空
     *
     * @param collection obj
     * @return true空，false不为空
     */
    public static boolean isEmpty(Collection collection) {
        return (collection == null || collection.isEmpty());
    }

    /**
     * 判断Map是否为空
     *
     * @param map obj
     * @return true空，false不为空
     */
    public static boolean isEmpty(Map map) {
        return (map == null || map.isEmpty());
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    public static List arrayToList(Object source) {
        return Arrays.asList(ObjectUtil.toObjectArray(source));
    }

    public static <E> void mergeArrayIntoCollection(Object array, Collection<E> collection) {
        if (array == null) {
            throw new IllegalArgumentException("array must not be null");
        }
        Object[] objs = ObjectUtil.toObjectArray(array);
        for (Object obj : objs) {
            collection.add((E) obj);
        }
    }

    public static <K, V> void mergePropertiesIntoMap(Properties props, Map<K, V> map) {
        if (map == null) {
            throw new IllegalArgumentException("Map must not be null");
        }
        if (props != null) {
            for (Enumeration<?> en = props.propertyNames(); en.hasMoreElements(); ) {
                String key = (String) en.nextElement();
                Object value = props.getProperty(key);
                if (value == null) {
                    // Potentially a non-String value...
                    value = props.get(key);
                }
                map.put((K) key, (V) value);
            }
        }
    }

    public static boolean contains(Iterator<?> iterator, Object element) {
        if (iterator != null) {
            while (iterator.hasNext()) {
                Object candidate = iterator.next();
                if (ObjectUtil.nullSafeEquals(candidate, element)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean contains(Enumeration<?> enumeration, Object element) {
        if (enumeration != null) {
            while (enumeration.hasMoreElements()) {
                Object candidate = enumeration.nextElement();
                if (ObjectUtil.nullSafeEquals(candidate, element)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean containsInstance(Collection<?> collection, Object element) {
        if (collection != null) {
            for (Object candidate : collection) {
                if (candidate == element) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean containsAny(Collection<?> source, Collection<?> candidates) {
        if (isEmpty(source) || isEmpty(candidates)) {
            return false;
        }
        for (Object candidate : candidates) {
            if (source.contains(candidate)) {
                return true;
            }
        }
        return false;
    }

    public static <T> String toString(T[] beans) {
        if (beans == null || beans.length == 0) {
            return "[]";
        }
        StringBuilder stringBuilder = new StringBuilder(100);
        stringBuilder.append("[");
        for (T bean : beans) {
            stringBuilder.append(bean.toString());
            stringBuilder.append(",");
        }
        return stringBuilder.substring(0, stringBuilder.length() - 1) + "]";
    }

    public static <T> String toString(Collection<T> beans) {
        if (beans == null || beans.size() == 0) {
            return "[]";
        }
        StringBuilder stringBuilder = new StringBuilder(100);
        stringBuilder.append("[");
        for (T bean : beans) {
            stringBuilder.append(bean.toString());
            stringBuilder.append(",");
        }
        return stringBuilder.substring(0, stringBuilder.length() - 1) + "]";
    }

    public static <T> String toString(Map<String, Object> map) {
        if (isEmpty(map)) {
            return "{}";
        }
        StringBuilder stringBuilder = new StringBuilder(100);
        stringBuilder.append("{");
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            stringBuilder.append(entry.getKey());
            stringBuilder.append("=");
            stringBuilder.append(entry.getValue().toString());
            stringBuilder.append(",");
        }
        return stringBuilder.substring(0, stringBuilder.length() - 1) + "}";
    }

    public static <T> String toString(Map<String, Object>[] paramMapArr) {
        if (paramMapArr == null || paramMapArr.length == 0) {
            return "[]";
        }
        StringBuilder stringBuilder = new StringBuilder(100);
        stringBuilder.append("[");
        for (Map<String, Object> map : paramMapArr) {
            stringBuilder.append(toString(map));
        }
        return stringBuilder.substring(0, stringBuilder.length() - 1) + "]";
    }

    /**
     * 提取集合中的对象的两个属性(通过Getter函数), 组合成Map.
     *
     * @param collection        来源集合.
     * @param keyPropertyName   要提取为Map中的Key值的属性名.
     * @param valuePropertyName 要提取为Map中的Value值的属性名.
     */
    @SuppressWarnings("unchecked")
    public static Map extractToMap(final Collection collection, final String keyPropertyName,
                                   final String valuePropertyName) {
        Map map = new HashMap(collection.size());

        try {
            for (Object obj : collection) {
                map.put(PropertyUtils.getProperty(obj, keyPropertyName),
                        PropertyUtils.getProperty(obj, valuePropertyName));
            }
        } catch (Exception e) {
            throw ReflectionUtil.convertReflectionExceptionToUnchecked(e);
        }

        return map;
    }

    /**
     * 提取集合中的对象的一个属性(通过Getter函数), 组合成List.
     *
     * @param collection   来源集合.
     * @param propertyName 要提取的属性名.
     */
    @SuppressWarnings("unchecked")
    public static List extractToList(final Collection collection, final String propertyName) {
        List list = new ArrayList(collection.size());

        try {
            for (Object obj : collection) {
                list.add(PropertyUtils.getProperty(obj, propertyName));
            }
        } catch (Exception e) {
            throw ReflectionUtil.convertReflectionExceptionToUnchecked(e);
        }

        return list;
    }

    /**
     * 提取集合中的对象的一个属性(通过Getter函数), 组合成由分割符分隔的字符串.
     *
     * @param collection   来源集合.
     * @param propertyName 要提取的属性名.
     * @param separator    分隔符.
     */
    public static String extractToString(final Collection collection, final String propertyName, final String separator) {
        List list = extractToList(collection, propertyName);
        return StringUtil.join(list, separator);
    }

    /**
     * 转换Collection所有元素(通过toString())为String, 中间以 separator分隔。
     */
    public static String convertToString(final Collection collection, final String separator) {
        return StringUtil.join(collection, separator);
    }

    /**
     * 转换Collection所有元素(通过toString())为String, 每个元素的前面加入prefix，后面加入postfix，如<div>mymessage</div>。
     */
    public static String convertToString(final Collection collection, final String prefix, final String postfix) {
        StringBuilder builder = new StringBuilder();
        for (Object o : collection) {
            builder.append(prefix).append(o).append(postfix);
        }
        return builder.toString();
    }

    /**
     * 转换Collection所有元素(通过toString())为String, 每个元素的前面加入prefix，后面加入postfix，中间以 separator分隔。如<div>mymessage1</div>;<div>mymessage2</div>
     */
    public static String convertToString(final Collection collection, final String separator, final String prefix, final String postfix) {
        if (isNotEmpty(collection)) {
            StringBuilder builder = new StringBuilder();
            collection.stream().forEach(o -> builder.append(prefix).append(o).append(postfix).append(separator));
            return builder.toString().substring(0, builder.length() - separator.length());
        }
        return "";
    }

    /**
     * 取得Collection的第一个元素，如果collection为空返回null.
     */
    public static <T> T getFirst(Collection<T> collection) {
        if (isEmpty(collection)) {
            return null;
        }

        return collection.iterator().next();
    }

    /**
     * 获取Collection的最后一个元素 ，如果collection为空返回null.
     */
    public static <T> T getLast(Collection<T> collection) {
        if (isEmpty(collection)) {
            return null;
        }

        //当类型为List时，直接取得最后一个元素 。
        if (collection instanceof List) {
            List<T> list = (List<T>) collection;
            return list.get(list.size() - 1);
        }

        //其他类型通过iterator滚动到最后一个元素.
        Iterator<T> iterator = collection.iterator();
        while (true) {
            T current = iterator.next();
            if (!iterator.hasNext()) {
                return current;
            }
        }
    }

    /**
     * 返回a+b的新List.
     */
    public static <T> List<T> union(final Collection<T> a, final Collection<T> b) {
        List<T> result = new ArrayList<T>(a);
        result.addAll(b);
        return result;
    }

    /**
     * 返回a-b的新List.
     */
    public static <T> List<T> subtract(final Collection<T> a, final Collection<T> b) {
        List<T> list = new ArrayList<T>(a);
        for (T element : b) {
            list.remove(element);
        }

        return list;
    }

    /**
     * 返回a与b的交集的新List.
     */
    public static <T> List<T> intersection(Collection<T> a, Collection<T> b) {
        List<T> list = new ArrayList<T>();

        for (T element : a) {
            if (b.contains(element)) {
                list.add(element);
            }
        }
        return list;
    }

    /**
     * 拆分集合
     *
     * @param <T>
     * @param resList 要拆分的集合
     * @param count   每个集合的元素个数
     * @return 返回拆分后的各个集合
     */
    public static <T> List<List<T>> split(List<T> resList, int count) {

        if (resList == null || count < 1) {
            return CommonConstants.EMPTY_LIST;
        }
        List<List<T>> ret = new ArrayList<List<T>>();
        int size = resList.size();
        //数据量不足count指定的大小
        if (size <= count) {
            ret.add(resList);
        } else {
            int pre = size / count;
            int last = size % count;
            //前面pre个集合，每个大小都是count个元素
            for (int i = 0; i < pre; i++) {
                List<T> itemList = new ArrayList<T>();
                for (int j = 0; j < count; j++) {
                    itemList.add(resList.get(i * count + j));
                }
                ret.add(itemList);
            }
            //last的进行处理
            if (last > 0) {
                List<T> itemList = new ArrayList<T>();
                for (int i = 0; i < last; i++) {
                    itemList.add(resList.get(pre * count + i));
                }
                ret.add(itemList);
            }
        }
        return ret;

    }
}

