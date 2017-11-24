package com.jd.crawler.timeutils.job;
import java.text.ParseException;
import java.util.Date;
/**
 * Created by lunan on 17-11-21.
 */
public class ScheduleJob {
    public static final String STATUS_RUNNING = "1";
    public static final String STATUS_NOT_RUNNING = "0";

    private Long jobId;
    /**
     * 任务创建时间
     */
    private Date createTime;
    /**
     * 任务更新时间
     */
    private Date updateTime;
    /**
     * 上一次执行时间
     */
    private Date lastProcessTime;
    /**
     * 下次计划执行时间
     */
    private Date nextProcessTime;
    /**
     * 任务名称
     */
    private String jobName;
    /**
     * 任务分组
     */
    private String jobGroup;
    /**
     * 任务状态 是否启动任务
     */
    private String jobStatus;
    /**
     * cron表达式
     */
    private String cronExpression;
    /**
     * 描述
     */
    private String description;
    /**
     * 任务执行时调用哪个类的方法 包名+类名
     */
    private String beanClass;

  //  private List<JobParam> JobParamList;

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) throws ParseException {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(String beanClass) {
        this.beanClass = beanClass;
    }
/*
    public List<JobParam> getJobParamList() {
        return JobParamList;
    }

    public void setJobParamList(List<JobParam> jobParamList) {
        JobParamList = jobParamList;
    }
*/
    public Date getNextProcessTime() {
        return nextProcessTime;
    }

    public void setNextProcessTime(Date nextProcessTime) {
        this.nextProcessTime = nextProcessTime;
    }

    public Date getLastProcessTime() {
        return lastProcessTime;
    }

    public void setLastProcessTime(Date lastProcessTime) {
        this.lastProcessTime = lastProcessTime;
    }
}
