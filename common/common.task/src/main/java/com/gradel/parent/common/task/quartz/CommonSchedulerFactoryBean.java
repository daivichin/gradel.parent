package com.gradel.parent.common.task.quartz;

import com.gradel.parent.common.util.api.annotation.Scheduled;
import com.gradel.parent.common.util.util.ExceptionUtil;
import com.gradel.parent.common.util.util.StringUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @date: 2016/7/4
 * @Description:定时器
 */
@Slf4j
public class CommonSchedulerFactoryBean extends SchedulerFactoryBean {

    @Setter
    private Map<Object, Method> jobDestroyMethodMap = new HashMap<Object, Method>();

    /**
     * 控制是否扫描定时器
     */
    @Setter
    private boolean launch = true;

    private Trigger[] triggers;

    @Setter
    private Set<Object> jobs;

    /**
     * @throws Exception
     */
    public CommonSchedulerFactoryBean(){
        super();
    }

    /**
     * @param launch 是否运行
     * @param jobs
     * @throws Exception
     */
    public CommonSchedulerFactoryBean(boolean launch, Set<Object> jobs) throws Exception {
        super();
        this.launch = launch;
        this.jobs = jobs;
    }

    private Trigger[] scanAllTriggers(Set<Object> jobs) throws Exception {
        List<Trigger> tgList = new ArrayList();
        try {
            for (Object ossJob : jobs) {
                Method[] methods = ossJob.getClass().getMethods();
                for (Method method : methods) {
                    Scheduled sch = method.getAnnotation(Scheduled.class);
                    if (sch == null) {
                        continue;
                    }
                    if (method.getParameterTypes().length > 0) {
                        throw new SchedulerException(ossJob.getClass() + " method[" + method.getName() + "] could not have any Parameters");
                    }
                    if (!method.getReturnType().toString().equals("void")) {
                        throw new SchedulerException(ossJob.getClass() + " method[" + method.getName() + "] 'returnType must be void");
                    }

                    //查找destroy方法
                    String destroyMethodName = sch.destroyMethod();
                    if (StringUtil.isNotBlank(destroyMethodName)) {
                        Method destroy = null;
                        for (Method destroyMethod : methods) {
                            if (destroyMethod.getName().equals(destroyMethodName)) {
                                if (destroyMethod.getParameterTypes().length > 0) {
                                    throw new SchedulerException(ossJob.getClass() + " destroy method[" + destroyMethodName + "] could not have any Parameters");
                                }
                                if (!destroyMethod.getReturnType().toString().equals("void")) {
                                    throw new SchedulerException(ossJob.getClass() + " destroy method[" + destroyMethodName + "] 'returnType must be void");
                                }
                                destroy = destroyMethod;
                                break;
                            }
                        }
                        if (destroy == null) {
                            throw new SchedulerException(ossJob.getClass() + " destroy method[" + destroyMethodName + "] is not exist!");
                        }
                        jobDestroyMethodMap.put(ossJob, destroy);
                    }

                    MethodInvokingJobDetailFactoryBean jobDetail = createJobDetail(ossJob, method);
                    tgList.add(createTrigger(sch, jobDetail.getObject(), ossJob));
                    break;
                }
            }
        } catch (Exception ex) {
            log.error("定时器初始化错误:" + ex.getMessage());
            throw ex;
        }
        Trigger[] ts = new Trigger[tgList.size()];
        return tgList.toArray(ts);
    }

    private MethodInvokingJobDetailFactoryBean createJobDetail(Object obj, Method method) throws Exception {
        MethodInvokingJobDetailFactoryBean jobDetail = new MethodInvokingJobDetailFactoryBean();
        jobDetail.setTargetObject(obj);
        jobDetail.setTargetMethod(method.getName());
        String name = method.getName() + "JobDetail" + obj.getClass().getSimpleName();
        jobDetail.setName(name);
        jobDetail.afterPropertiesSet();
        return jobDetail;
    }

    private Trigger createTrigger(Scheduled scheduled, JobDetail jobDetail, Object ossJob) throws Exception {
        Trigger trigger = null;
        if (scheduled.cron().isEmpty()) {
            SimpleTriggerFactoryBean simpleTrigger = new SimpleTriggerFactoryBean();
            simpleTrigger.setName("trigger-" + ossJob.getClass().getSimpleName());
//            simpleTrigger.setJobName("job"+ossJob.getClass().getSimpleName());
            simpleTrigger.setStartDelay(scheduled.startDelay());
            simpleTrigger.setRepeatInterval(scheduled.repeatInterval());
            simpleTrigger.setJobDetail(jobDetail);
            simpleTrigger.afterPropertiesSet();
            trigger = simpleTrigger.getObject();
        } else {
            CronTriggerFactoryBean cronTrigger = new CronTriggerFactoryBean();
            cronTrigger.setName("trigger-" + ossJob.getClass().getSimpleName());
//            cronTrigger.setJobName("job"+ossJob.getClass().getSimpleName());
            cronTrigger.setStartDelay(scheduled.startDelay());
            cronTrigger.setCronExpression(scheduled.cron());
            cronTrigger.setJobDetail(jobDetail);
            cronTrigger.afterPropertiesSet();
            trigger = cronTrigger.getObject();
        }
        return trigger;
    }

    @Override
    public void setTriggers(Trigger triggers[]) {
        this.triggers = triggers;
    }

    protected void init(){
        if (jobs != null && !jobs.isEmpty()) {
            jobs.forEach(job -> jobDestroyMethodMap.put(job, null));
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
        if (launch && !jobDestroyMethodMap.isEmpty()) {
            Trigger[] aTriggers = scanAllTriggers(this.jobDestroyMethodMap.keySet());
            if (this.triggers != null) {
                aTriggers = ArrayUtils.addAll(aTriggers, triggers);
            }
            if (aTriggers != null && aTriggers.length > 0) {
                super.setTriggers(aTriggers);
            }

        }
        super.afterPropertiesSet();
    }

    @Override
    public void destroy() throws SchedulerException {
        super.destroy();

        //销毁资源
        if (!jobDestroyMethodMap.isEmpty()) {
            log.info("Starting to call job destroy method");
            jobDestroyMethodMap.entrySet().forEach(e -> {
                Method destroy = e.getValue();
                if (destroy != null) {
                    try {
                        destroy.invoke(e.getKey());
                    } catch (Throwable throwable) {
                        log.info("Call job destroy method[{}] Exception:{}", destroy, ExceptionUtil.getAsString(throwable));
                    }
                }
            });
            jobDestroyMethodMap.clear();
        }
    }
}
