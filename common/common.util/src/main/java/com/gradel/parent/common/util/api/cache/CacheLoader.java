package com.gradel.parent.common.util.api.cache;

/**
* User: sdeven.chen.dongwei@gmail.com
 * @date: 2016/5/30
 * @Description:
 */
@FunctionalInterface
public interface CacheLoader<K, V> {

    V load(K key);

}
