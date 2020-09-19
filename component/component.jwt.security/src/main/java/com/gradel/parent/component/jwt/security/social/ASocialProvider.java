package com.gradel.parent.component.jwt.security.social;

import com.gradel.parent.component.jwt.security.endpoint.entity.ASocialDetail;

/**
 * ASocialProvider
 *
 * @Date 2019/7/20 下午5:38
 * @Author sdeven
 */
public interface ASocialProvider {
    Integer socialType();
    ASocialDetail querySocialDetail(String code);
}
