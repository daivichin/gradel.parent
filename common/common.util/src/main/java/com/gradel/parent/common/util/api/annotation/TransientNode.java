package com.gradel.parent.common.util.api.annotation;


import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 是否解释成sql字段
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface TransientNode {
}