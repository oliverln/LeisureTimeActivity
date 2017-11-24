package com.jd.crawler;
import com.jd.crawler.timeutils.impl.JobServiceImpl;
import com.jd.crawler.timeutils.job.CrawlerController;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lunan on 17-11-21.
 */
public class JobScheduler {
    public static void main(String[] para) {
        try {
            if(para.length<12){
                System.out.println("需要填写以下参数：");
                System.out.println("1.爬虫数据存储位置");
                System.out.println("2.数据本地存储位置");
                System.out.println("3.需要爬取的网站,逗号隔开");
                System.out.println("4.爬虫线程数");
                System.out.println("5.爬虫页面深度");
                System.out.println("6.爬虫页面数");
                System.out.println("7.url前缀");
                System.out.println("8.爬虫是否重定向");
                System.out.println("9.定时调度时间");
                System.out.println("10.调度任务名JobName");
                System.out.println("11.调度任务组名");
                System.out.println("12.调度触发器组名");
                System.out.println("13.爬虫代理host");
                System.out.println("14.爬虫代理port");
                System.out.println("15.爬虫代理密码");
                System.out.println("16.爬虫代理用户名");
                System.exit(0);
            }

            Map<String, String> map = new HashMap<String,String>();
            map.put("crawlStorageFolder",para[0]);
            map.put("storageFolder",para[1]);
            map.put("crawlDomainlist",para[2]);
            map.put("numberOfCrawlers",para[3]);
            map.put("maxDepthOfCrawling",para[4]);
            map.put("maxPagesToFetch",para[5]);
            map.put("urlprefix",para[6]);
            map.put("followRedirect",para[7]);
            String time=para[8];
            String jobname=para[9];
            String jobgroupname=para[10];
            String triggergourpname=para[11];
            if(para.length>12) {
                map.put("proxyHost", para[12]);
                map.put("proxyPort", para[13]);
                map.put("proxyPassword", para[14]);
                map.put("proxyUsername", para[15]);
            }

            SchedulerFactory sf = new StdSchedulerFactory();
            Scheduler scheduler = sf.getScheduler();

            JobServiceImpl jobService = new JobServiceImpl();
            jobService.setScheduler(scheduler);

            //addjob
            jobService.setJobName(jobname);
            jobService.setJobGroupName(jobgroupname);
            jobService.setTriggerGroupName(triggergourpname);
            jobService.addJob(CrawlerController.class,time,map);
        } catch (SchedulerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
