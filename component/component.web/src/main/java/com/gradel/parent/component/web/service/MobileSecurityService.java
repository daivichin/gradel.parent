package com.gradel.parent.component.web.service;

import java.util.List;

/**
 * 移动端验证逻辑服务
 */
public interface MobileSecurityService {
    /**
     * 被拦截接口URL
     * @return
     */
    List<String> checkPathPatterns();

    /**
     * 排除拦截接口URL
     * @return
     */
    List<String> checkExcludePathPatterns();


}
