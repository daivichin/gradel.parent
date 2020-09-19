package com.gradel.parent.ali.ahas.autoconfigure;

import org.springframework.core.Ordered;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @date: 2018-04-10
 * @Description:
 */
public class SentinelCommonFilterMapping {

    private int order = Ordered.HIGHEST_PRECEDENCE;

    private String filterBeanName;

    private Set<String> urlPatterns = new LinkedHashSet<String>();

    protected SentinelCommonFilterMapping() {
    }

    public static SentinelCommonFilterMapping create() {
        return new SentinelCommonFilterMapping();
    }

    public SentinelCommonFilterMapping urlPatterns(Collection<String> urlPatterns) {
        Assert.notNull(urlPatterns, "UrlPatterns must not be null");
        this.urlPatterns = new LinkedHashSet<String>(urlPatterns);
        return this;
    }

    /**
     * Return a mutable collection of URL patterns that the filter will be registered
     * against.
     *
     * @return the URL patterns
     */
    public Collection<String> getUrlPatterns() {
        return this.urlPatterns;
    }

    /**
     * Add URL patterns that the filter will be registered against.
     *
     * @param urlPatterns the URL patterns
     */
    public SentinelCommonFilterMapping urlPatterns(String... urlPatterns) {
        Assert.notNull(urlPatterns, "UrlPatterns must not be null");
        Collections.addAll(this.urlPatterns, urlPatterns);
        return this;
    }

    /**
     * Set the order of the registration bean.
     *
     * @param order the order
     */
    public SentinelCommonFilterMapping order(int order) {
        this.order = order;
        return this;
    }

    /**
     * Get the order of the registration bean.
     *
     * @return the order
     */
    public int getOrder() {
        return this.order;
    }

    public String getFilterBeanName() {
        return filterBeanName;
    }

    public SentinelCommonFilterMapping filterBeanName(String filterBeanName) {
        this.filterBeanName = filterBeanName;
        return this;
    }
}
