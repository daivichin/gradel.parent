package com.gradel.parent.component.web.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ClassName: AtLeastOneNotNullTarget
 * Function:  AtLeastOneNotNullTarget
 * @date:      2019/6/19 下午3:43
 * Author     sdeven.chen.dongwei@gmail.com
 * Version    V1.0
 */

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AtLeastOneNotNullTarget {
    boolean blankable() default false;
}
