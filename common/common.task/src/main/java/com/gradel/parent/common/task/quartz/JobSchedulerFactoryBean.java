package com.gradel.parent.common.task.quartz;

import com.gradel.parent.common.task.job.Jobable;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @date: 2016/7/4
 * @Description:定时器
 */
@Slf4j
public class JobSchedulerFactoryBean extends CommonSchedulerFactoryBean {

    private ObjectProvider<Jobable> jobablesPrivider;

    @Setter
    private Set<Jobable> jobables;

    public JobSchedulerFactoryBean() {
        super();
    }

    public JobSchedulerFactoryBean(ObjectProvider<Jobable> jobablesPrivider) {
        this.jobablesPrivider = jobablesPrivider;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Set<Object> jobs = new HashSet<>();
        if(jobablesPrivider != null){
            Iterator<Jobable> iterator = jobablesPrivider.iterator();
            if(iterator != null){
                while (iterator.hasNext()){
                    Jobable jobable = iterator.next();
                    jobs.add(jobable);
                }
            }
        }
        if(jobables != null){
            for(Jobable jobable:jobables){
                jobs.add(jobable);
            }
        }

        super.setJobs(jobs);
        super.afterPropertiesSet();
    }
}
