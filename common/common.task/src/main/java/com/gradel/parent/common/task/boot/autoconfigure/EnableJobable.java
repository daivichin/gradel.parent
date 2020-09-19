package com.gradel.parent.common.task.boot.autoconfigure;

import com.gradel.parent.common.autoimport.CustomImportAutoConfiguration;
import com.gradel.parent.common.task.quartz.JobSchedulerFactoryBean;

import java.lang.annotation.*;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @date: 2018-04-10
 * @see JobSchedulerFactoryBean
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@CustomImportAutoConfiguration(JobableConfig.class)
public @interface EnableJobable {
}
