package com.gradel.parent.common.util.api.annotation;


import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Scheduled {
	/**
	 * 为""时，startDelay、repeatInterval有效，否则startDelay、repeatInterval无效
	 * cronExpression
	 * 执行时间表达式
	 */
	String cron() default "";
	
	/**
	 * 启动多少毫秒后执行
	 *
	 */
	long startDelay() default -1;
	/**
	 *
	 * 每隔多少毫秒
	 * 
	 */
	long repeatInterval() default -1;

	/**
	 * 销毁方法名--destroy
     */
	String destroyMethod() default "";
}
