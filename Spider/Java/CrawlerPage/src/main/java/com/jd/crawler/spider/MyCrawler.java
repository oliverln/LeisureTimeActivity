package com.jd.crawler.spider;

import com.jd.crawler.urlchartool.UrlCharTool;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import com.jd.crawler.leveldb.impl.levelDBImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by lunan on 17-11-13.
 */
public class MyCrawler extends WebCrawler {
    private static final Logger logger = LoggerFactory.getLogger(WebCrawler.class);
    CrawlStat myCrawlStat;
    public MyCrawler(){
        myCrawlStat = new CrawlStat();
    }

    public void dumpMyData() {
        final int id = getMyId();
        // You can configure the log to output to file
        logger.info("Crawler {} > Processed Pages: {}", id, myCrawlStat.getTotalProcessedPages());
        logger.info("Crawler {} > Total Links Found: {}", id, myCrawlStat.getTotalLinks());
        logger.info("Crawler {} > Total Text Size: {}", id, myCrawlStat.getTotalTextSize());
    }

    @Override
    public Object getMyLocalData() {
        return myCrawlStat;
    }

    @Override
    public void onBeforeExit() {
        dumpMyData();
    }
    /**
     * 正则匹配指定的后缀文件
     */
    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg"
            + "|png|mp3|mp3|zip|gz))$");
    private static final Pattern needPatterns = Pattern.compile(".*(\\.(pdf|doc?))$");

    private static File storageFolder;
    private static String[] crawlDomains;
    private static String urlPrefix;

    public static void configure(String[] domain, String storageFolderName,String urlprefix) {
        crawlDomains = domain;
        urlPrefix=urlprefix;
        storageFolder = new File(storageFolderName);
        if (!storageFolder.exists()) {
            storageFolder.mkdirs();
        }
    }

    private String UrlParse(String url){
        String prefix=null;
        try {
            URL aURL = new URL(url);
            String protocol=aURL.getProtocol();
            String authority=aURL.getAuthority();
            String host=aURL.getHost();
            int port=aURL.getPort();
            if(port!=-1){
                prefix=protocol+"://"+host+":"+port;
            }else{
                prefix=protocol+"://"+host;
            }
        }catch(MalformedURLException e){
            System.out.println("UrlParse Exception:"+e);
        }
            return prefix;
        }
    /**
     * 这个方法主要是决定哪些url我们需要抓取，返回true表示是我们需要的，返回false表示不是我们需要的Url
     * 第一个参数referringPage封装了当前爬取的页面信息
     * 第二个参数url封装了当前爬取的页面url信息
     */
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();  //得到小写的url
        if (FILTERS.matcher(href).matches()) {
            return false;
        }

        if (needPatterns.matcher(href).matches()) {
            return true;
        }
        for (String domain : crawlDomains) {
            if (href.startsWith(domain)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 当我们爬到我们需要的页面，这个方法会被调用，我们可以尽情的处理这个页面
     * page参数封装了所有页面信息
     */
    @Override
    public void visit(Page page) {
        int docid = page.getWebURL().getDocid();
        String url = page.getWebURL().getURL();
        String domain = page.getWebURL().getDomain();
        String path = page.getWebURL().getPath();
        String subDomain = page.getWebURL().getSubDomain();
        String parentUrl = page.getWebURL().getParentUrl();
        String anchor = page.getWebURL().getAnchor();
        /*
        //仅对url的utf-8进行编码处理
        if(UrlCharTool.isUtf8Url(url)){
            url=UrlCharTool.Utf8URLdecode(url);
            System.out.println("爬取路径(deconde)：" + url);
        }
        */
        System.out.println("爬取路径：" + url);
        System.out.println("Docid: {}" + docid);
        System.out.println("Domain: '{}'" + domain);
        System.out.println("Sub-domain: '{}'" + subDomain);
        System.out.println("Path: '{}'" + path);
        System.out.println("Parent page: {}" + parentUrl);
        System.out.println("Anchor text: {}" + anchor);
        myCrawlStat.incProcessedPages();
        //获取数据库
        levelDBImpl db=new levelDBImpl();
        String status=db.getUrlDB().getData(url);
        if(status!=null){
            if(status=="success"){
                return;
            }
        }
        //获取并解析数据
        try {
            if (page.getParseData() instanceof HtmlParseData) {
                HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
                String text = htmlParseData.getText();
                String html = htmlParseData.getHtml();
                //解析urlPrefix
                urlPrefix=UrlParse(url);
                htmlParser hp = new htmlParser(html, urlPrefix);
                Set<WebURL> links = htmlParseData.getOutgoingUrls();
                String title = htmlParseData.getTitle();
                //set to string；StringUtils.join(list.toArray(), ",")
                String linkValue="";
                for (WebURL wu : links) {
                    linkValue+=wu.toString();
                }
                //数据存储
                db.getHtmlDB().putData(url, html);
                db.getTitleDB().putData(url,title);
                db.getLinksDB().putData(url,linkValue);


                boolean isshtml = false;
                /*
                **针对上交所，shtml格式为公告或说明,存在，则解析成文档
                **此处注释文档解析和表格解析;
                Pattern filePatterns = Pattern.compile(".*(\\.(shtml))$");
                if (filePatterns.matcher(url).matches()) {
                    isshtml = true;
                }
                */
                //此处注释文档解析和表格解析
                hp.parsehtml(storageFolder + "/" + url, title, isshtml);
                //统计
                myCrawlStat.incTotalTextSize(text.length());
                myCrawlStat.incTotalLinks(links.size());
            }
            //存入DB中
            db.getParentDB().putData(url, parentUrl);
            db.getUrlDB().putData(url, "success");
        }catch(Exception e){
            db.getUrlDB().putData(url, "exception");
            System.out.println("Error Visit!");
        }

    }
}
