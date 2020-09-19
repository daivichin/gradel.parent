package com.gradel.parent.component.web.service;

import java.util.List;

/**
 * Web 站验证逻辑服务
 */
public interface WebSecurityService {
    /**
     * 被拦截接口URL
     * @return
     */
    List<String> checkPathPatterns();

    /**
     * 排除拦截接口URL Social
     * @return
     */
    List<String> checkExcludePathPatterns();

}
