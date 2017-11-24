package com.jd.crawler.timeutils.impl;
import com.jd.crawler.timeutils.job.ScheduleJob;
import org.quartz.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.CronScheduleBuilder.cronSchedule;
/**
 * Created by lunan on 17-11-21.
 */
public class JobServiceImpl {
    private Scheduler scheduler;
    private String jobName;
    private String jobGroupName;
    private String triggerGroupName;
    private String time;
    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobGroupName() {
        return jobGroupName;
    }

    public void setJobGroupName(String jobGroupName) {
        this.jobGroupName = jobGroupName;
    }

    public String getTriggerGroupName() {
        return triggerGroupName;
    }

    public void setTriggerGroupName(String triggerGroupName) {
        this.triggerGroupName = triggerGroupName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
    //addJob
    public void addJob(Class cls, String time,Map<String, String> jobData){
        try{
            JobDetail jobDetail=JobBuilder.newJob(cls).withIdentity(jobName, jobGroupName).build();
            for (Map.Entry<String, String> entry : jobData.entrySet()) {
                System.out.println("Key: "+entry.getKey()+" Value:"+entry.getValue());
                jobDetail.getJobDataMap().put(entry.getKey(), entry.getValue());
            }
            CronTrigger trigger=newTrigger().withIdentity(jobName, triggerGroupName).
                    withSchedule(cronSchedule(time)).startNow().build();

            scheduler.scheduleJob(jobDetail,trigger);
            scheduler.start();
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
    //modifyJobName
    public void modifyJobTime(String jobName, String time,Map<String,String> jobData) throws SchedulerException, ClassNotFoundException{

            TriggerKey triggerKey = TriggerKey.triggerKey(jobName, triggerGroupName);
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            if (trigger == null) {
                return;
            }
            String oldTime = trigger.getCronExpression();
            if (!oldTime.equalsIgnoreCase(time)) {
                JobKey jobKey=JobKey.jobKey(jobName);
                JobDetail jobDetail = scheduler.getJobDetail(jobKey);
                Class objJobClass = jobDetail.getJobClass();
                removeJob(jobName);
                addJob(objJobClass, time,jobData);
            }
    }
    //removeJob
    public  void removeJob(String jobName) {
        try {
            TriggerKey triggerKey=TriggerKey.triggerKey(jobName,triggerGroupName);
            JobKey jobKey=JobKey.jobKey(jobName);
            scheduler.pauseTrigger(triggerKey);// 停止触发器
            scheduler.unscheduleJob(triggerKey);// 移除触发器
            scheduler.deleteJob(jobKey);// 删除任务
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    //start all jobs
    public  void startJobs() {
        try {
            scheduler.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    //stop all jobs
    public  void shutdownJobs() {
        try {
            if (!scheduler.isShutdown()) {
                scheduler.shutdown();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    //get all Running Jobs
    public void getRunningJobs()throws SchedulerException{
        List<JobExecutionContext> executingJobs=scheduler.getCurrentlyExecutingJobs();
        List<ScheduleJob> jobList = new ArrayList<ScheduleJob>(executingJobs.size());
        System.out.println(executingJobs);
        for (JobExecutionContext executingJob : executingJobs) {
            ScheduleJob job = new ScheduleJob();
            JobDetail jobDetail = executingJob.getJobDetail();
            JobKey jobKey = jobDetail.getKey();
            Trigger trigger = executingJob.getTrigger();
            job.setJobName(jobKey.getName());
            job.setJobGroup(jobKey.getGroup());
            job.setDescription("触发器:" + trigger.getKey());
            Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
            job.setJobStatus(triggerState.name());
            if (trigger instanceof CronTrigger) {
                CronTrigger cronTrigger = (CronTrigger) trigger;
                String cronExpression = cronTrigger.getCronExpression();
                job.setCronExpression(cronExpression);
            }
            System.out.println(job);
            jobList.add(job);
        }
    }
}
