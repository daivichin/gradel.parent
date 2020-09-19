package com.gradel.parent.common.autoimport;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @date 2019/01/16 下午9:48
 * @Description: 解决手动＠import 类加载顺序问题
 *
 * ＠see https://www.cnblogs.com/yszzu/p/10002281.html
 * ＠see https://gooroo.io/GoorooTHINK/Article/17466/Lessons-Learned-Writing-Spring-Boot-Auto-Configurations/29652#.W_ZwdFUzYdV%20%20https://blog.csdn.net/isea533/article/details/53975720
 */
@Target({METHOD, TYPE})
@Retention(RUNTIME)
@Inherited
@Configuration
@Import(CustomAutoConfigurationImportSelector.class)
public @interface CustomImportAutoConfiguration {
    Class<?>[] value();
}