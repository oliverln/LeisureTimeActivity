package com.jd.crawler.timeutils.job;
import com.jd.crawler.leveldb.impl.levelDBImpl;
import com.jd.crawler.spider.impl.CrawlStatImpl;
import com.jd.crawler.spider.MyCrawler;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
/**
 * Created by lunan on 17-11-21.
 */
public class CrawlerController implements Job  {
    private static String crawlStorageFolder;// 定义爬虫数据存储位置
    private static String storageFolder;
    private static String crawlDomainlist;
    private static int numberOfCrawlers;
    private static int maxDepthOfCrawling;
    private static int maxPagesToFetch;
    private static String proxyHost;
    private static int proxyPort;
    private static String proxyPassword;
    private static String proxyUsername;
    private static String urlprefix;
    private static boolean followRedirect;


    public String getUrlprefix() {
        return urlprefix;
    }

    public void setUrlprefix(String urlprefix) {
        this.urlprefix = urlprefix;
    }

    public boolean getFollowRedirect() {
        return followRedirect;
    }

    public int getMaxDepthOfCrawling() {
        return maxDepthOfCrawling;
    }

    public int getMaxPagesToFetch() {
        return maxPagesToFetch;
    }

    public int getNumberOfCrawlers() {
        return numberOfCrawlers;
    }

    public String getCrawlDomainlist() {
        return crawlDomainlist;
    }

    public String getCrawlStorageFolder() {
        return crawlStorageFolder;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public String getProxyPassword() {
        return proxyPassword;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public String getProxyUsername() {
        return proxyUsername;
    }

    public String getStorageFolder() {
        return storageFolder;
    }

    public void setCrawlDomainlist(String crawlDomainlist) {
        this.crawlDomainlist = crawlDomainlist;
    }

    public void setCrawlStorageFolder(String crawlStorageFolder) {
        this.crawlStorageFolder = crawlStorageFolder;
    }

    public void setFollowRedirect(String followRedirect) {
        if(followRedirect=="true"||followRedirect=="True"||followRedirect=="TRUE") {
            this.followRedirect = true;
        }else if(followRedirect=="false"||followRedirect=="False"||followRedirect=="FALSE"){
            this.followRedirect=false;
        }
    }

    public void setMaxDepthOfCrawling(int maxDepthOfCrawling) {
        this.maxDepthOfCrawling = maxDepthOfCrawling;
    }

    public void setMaxPagesToFetch(int maxPagesToFetch) {
        this.maxPagesToFetch = maxPagesToFetch;
    }

    public void setNumberOfCrawlers(int numberOfCrawlers) {
        this.numberOfCrawlers = numberOfCrawlers;
    }

    public void setProxyHost(String proxyHost) {
        if(proxyHost=="null"||proxyHost=="NULL"){
            this.proxyHost=null;
        }else {
            this.proxyHost = proxyHost;
        }
    }

    public void setProxyPassword(String proxyPassword) {
        if(proxyPassword=="null"||proxyPassword=="NULL"){
            this.proxyPassword=null;
        }else{
            this.proxyPassword = proxyPassword;
        }
    }

    public void setProxyPort(String proxyPort) {
        this.proxyPort =Integer.parseInt(proxyPort);
    }

    public void setProxyUsername(String proxyUsername) {
        if(proxyUsername=="null"||proxyUsername=="NULL"){
            this.proxyUsername=null;
        }else {
            this.proxyUsername = proxyUsername;
        }
    }

    public void setStorageFolder(String storageFolder) {
        this.storageFolder = storageFolder;
    }


    //@Override
    public synchronized void execute(JobExecutionContext arg0) throws JobExecutionException {
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
        Date Datetime=new Date();
        String unixtime=String.valueOf(Datetime.getTime());
        System.out.println("Spider Begin: "+ formatter.format(Datetime));
        System.out.println("Date:"+unixtime);
        System.out.println("crawlStorageFolder:"+crawlStorageFolder+"\n  storageFolder:"+storageFolder
        +"\n crawlDomainlist:"+crawlDomainlist+"\n maxDepthOfCrawling:"+maxDepthOfCrawling);
        //针对；分割的多个网站进行
        String[] crawlDomainlists =crawlDomainlist.split(";");
        for(String crawlDomain:crawlDomainlists) {
            System.out.println("crawlDomain:"+crawlDomain);
            controller(unixtime,crawlDomain);
        }
    }

    public void controller(String unixtime,String crawlDomain){
        String[] crawlDomains =crawlDomain.split(",");
        //int numberOfCrawlers = Integer.parseInt(args[4]); // 定义2个爬虫，也就是2个线程
        CrawlConfig config = new CrawlConfig(); // 定义爬虫配置
        config.setCrawlStorageFolder(crawlStorageFolder); // 设置爬虫文件存储位置
        config.setMaxDepthOfCrawling(maxDepthOfCrawling);//深度，即从入口URL开始算，URL是第几层。如入口A是1，从A中找到了B，B中又有C，则B是2，C是3
        config.setMaxPagesToFetch(maxPagesToFetch);//最多爬取多少个页面
        config.setProxyHost(proxyHost);//代理的ip
        config.setProxyPort(proxyPort);
        config.setProxyPassword(proxyPassword);
        config.setProxyUsername(proxyUsername);
        config.setFollowRedirects(followRedirect);//是否抓取重定向的网站
        config.setResumableCrawling(true);
        config.setPolitenessDelay(2000);//2000毫秒
        //创建数据库
        levelDBImpl db=new levelDBImpl();
        db.setUnixtime(unixtime);
        db.createAllDB(crawlStorageFolder);
        /*
         * 实例化爬虫控制器
         */
        PageFetcher pageFetcher = new PageFetcher(config); // 实例化页面获取器
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig(); //  实例化爬虫机器人配置 比如可以设置 user-agent
        // 实例化爬虫机器人对目标服务器的配置，每个网站都有一个robots.txt文件 规定了该网站哪些页面可以爬，哪些页面禁止爬，
        // 该类是对robots.txt规范的实现
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        try {
            // 实例化爬虫控制器
            CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
            /**
             * 配置爬虫种子页面，就是规定的从哪里开始爬，可以配置多个种子页面
             */
            for (String domain : crawlDomains) {
                controller.addSeed(domain);
            }

            MyCrawler.configure(crawlDomains, storageFolder,urlprefix);
            /**
             * 启动爬虫，爬虫从此刻开始执行爬虫任务，根据以上配置
             */
            //controller.start(MyCrawler.class, numberOfCrawlers);
            controller.startNonBlocking(MyCrawler.class, numberOfCrawlers);
            final List<Object> crawlersLocalData = controller.getCrawlersLocalData();
            long totalLinks = 0;
            long totalTextSize = 0;
            int totalProcessedPages = 0;
            for (final Object localData : crawlersLocalData) {
                final CrawlStatImpl stat = (CrawlStatImpl) localData;
                totalLinks += stat.getTotalLinks();
                totalTextSize += stat.getTotalTextSize();
                totalProcessedPages += stat.getTotalProcessedPages();
            }

            System.out.println("Aggregated Statistics:");
            System.out.println("\tProcessed Pages: {}" + totalProcessedPages);
            System.out.println("\tTotal Links found: {}" + totalLinks);
            System.out.println("\tTotal Text Size: {}" + totalTextSize);
            // Wait for 30 seconds
            Thread.sleep(30 * 1000);
            // Send the shutdown request and then wait for finishing
            if (controller.isFinished()) {
                controller.shutdown();
            }
            controller.waitUntilFinish();
            db.closeAllDB();
        }catch (final Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}
