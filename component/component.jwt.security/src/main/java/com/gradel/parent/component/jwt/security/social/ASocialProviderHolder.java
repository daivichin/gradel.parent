package com.gradel.parent.component.jwt.security.social;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ASocialProviderHolder
 *
 * @Date 2019/7/21 下午8:04
 * @Author sdeven
 */
public class ASocialProviderHolder {
    private static final Map<Integer, ASocialProvider> map = new ConcurrentHashMap<>();

    public static ASocialProvider get(Integer type) {
        return map.get(type);
    }
    public static void add(ASocialProvider provider) {
        map.put(provider.socialType(), provider);
    }
}
