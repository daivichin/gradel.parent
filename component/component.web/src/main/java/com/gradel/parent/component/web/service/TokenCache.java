package com.gradel.parent.component.web.service;

import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.CreateCache;

import java.util.concurrent.TimeUnit;

public class TokenCache {
    @CreateCache(timeUnit = TimeUnit.MINUTES, expire=5, cacheType = CacheType.LOCAL)
    private Cache<String, String> accessToken;

    @CreateCache(timeUnit = TimeUnit.DAYS, expire=15, cacheType = CacheType.LOCAL)
    private Cache<String, String> refreshToken;
}
