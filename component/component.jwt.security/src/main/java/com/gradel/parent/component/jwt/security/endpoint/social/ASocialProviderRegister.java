package com.gradel.parent.component.jwt.security.endpoint.social;

import com.gradel.parent.component.jwt.security.social.ASocialProvider;
import com.gradel.parent.component.jwt.security.social.ASocialProviderHolder;

import javax.annotation.PostConstruct;

/**
 * ASocialProviderRegister
 *
 * @Date 2019/7/20 下午5:38
 * @Author sdeven
 */
public abstract class ASocialProviderRegister implements ASocialProvider {
    /**
     * 对公提供者注册器
     */
    @PostConstruct
    public void register() {
        ASocialProviderHolder.add(this);
    }
}
