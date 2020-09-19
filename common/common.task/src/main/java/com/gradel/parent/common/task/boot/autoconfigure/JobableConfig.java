package com.gradel.parent.common.task.boot.autoconfigure;

import com.gradel.parent.common.task.job.Jobable;
import com.gradel.parent.common.task.quartz.JobSchedulerFactoryBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobableConfig {

    @Bean
    public JobSchedulerFactoryBean jobableScheduler(ObjectProvider<Jobable> jobablesPrivider){
        return new JobSchedulerFactoryBean(jobablesPrivider);
    }
}
