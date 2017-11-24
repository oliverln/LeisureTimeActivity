package com.jd.crawler.spider;

import com.jd.crawler.leveldb.impl.levelDBImpl;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

import com.jd.crawler.urlchartool.UrlCharTool;
/**
 * Created by lunan on 17-11-14.
 */
public class htmlParser {

    private String htmlstr;
    private Element body;
    private String urlPrefix;

    htmlParser(String str, String urlprefix) {
        htmlstr = str;
        urlPrefix = urlprefix;
        Document doc = Jsoup.parse(htmlstr, "utf-8");
        body = doc.body();
    }

    /**
     * 写文件到本地目录
     *
     * @param str
     * @param filePath
     * @param append
     */
    public void writeFile(String str, String filePath, boolean append) {

        String dirpath = filePath.substring(0, filePath.lastIndexOf("/") + 1);
        File dirFolder = new File(dirpath);
        if (!dirFolder.exists()) {
            dirFolder.mkdirs();
        }

        byte bt[] = new byte[1024];
        bt = str.getBytes();
        try {
            FileOutputStream in = new FileOutputStream(filePath, append);
            try {
                in.write(bt, 0, bt.length);
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从网络Url中下载文件
     *
     * @param urlStr
     * @param fileName,urlStr,savePath
     * @param savePath
     */
    private void downLoadFromUrl(String urlStr, String fileName, String savePath) throws IOException {
        URL url = new URL(urlStr);
        System.out.println("downLoadFromUrl..:"+url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        //设置超时间为10秒
        conn.setConnectTimeout(10 * 1000);
        //防止屏蔽程序抓取而返回403错误
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

        //得到输入流
        InputStream inputStream = conn.getInputStream();
        //获取自己数组
        byte[] getData = readInputStream(inputStream);

        //文件保存位置
        File saveDir = new File(savePath);
        if (!saveDir.exists()) {
            saveDir.mkdir();
        }
        File file = new File(saveDir + File.separator + fileName);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(getData);
        if (fos != null) {
            fos.close();
        }
        if (inputStream != null) {
            inputStream.close();
        }
        System.out.println("info:" + url + " download success");

    }


    /**
     * 从输入流中获取字节数组
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    private byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    /**
     * 解析table
     */
    private void parseTable() {
        Elements tables = body.select("table");
        if (tables.size() > 0) {
            for (Element table : tables) {
                //这里存在自定义table格式的数据，暂时没有获取到
                System.out.println("Table----------------------------");
                for (Element row : table.select("tr")) {
                    for (Element col : row.select("th")) {
                        System.out.print(col.text());
                        System.out.print(",");
                    }
                    for (Element col : row.select("td")) {
                        System.out.print(col.text());
                        System.out.print(",");
                    }
                    System.out.println("");
                }
                System.out.println("Table----------------------------end");
            }
        }
    }

    /**
     * 解析DOM
     * @param path
     */
    private void parseDom(String path) {
        Elements as = body.select("a");
        if (as.size() > 0) {
            for (Element a : as) {
                if (a.hasAttr("href") && a.hasAttr("title")) {
                    String href = a.attr("href");
                    String title = a.attr("title");
                    if (title != "") {
                        System.out.println("Title:" + title + " href:" + href);
                        //文件格式解析
                        downloadFile(href, path, title);//下载pdf,doc,docx

                    }
                }
            }
        }

    }

    /**
     * 遍历输出内容
     * @param a,savepath
     */
    private void loopElementW(Elements a,String savepath){
        if (a.size() > 0) {
            for (Element p : a) {
                System.out.println(p.text());
                if (p.text() != "") {
                    writeFile(p.text(), savepath, true);
                    writeFile("\n", savepath, true);
                }
            }
        }
    }
    /**
     * 解析shtml文件
     */
    private void parseTxt(String path, String title) {
        String savepath = path + "_shtml/" + title + ".txt";
        Elements is = body.select("i");
        loopElementW(is,savepath);
        Elements ps = body.select("p");
        loopElementW(ps,savepath);
    }

    /**
     * 解析file
     */
    private void downloadFile(String href, String path, String title) {
        Pattern filePatterns = Pattern.compile(".*(\\.(pdf|doc?))$");
        if (filePatterns.matcher(href).matches()) {
            String Urlpath = urlPrefix + href;
            if(href.contains(urlPrefix)||href.contains("http:")){
                Urlpath=href;
            }

            //仅对url的utf-8进行编码处理
            //if(UrlCharTool.isUtf8Url(Urlpath)){
            //    Urlpath=UrlCharTool.Utf8URLdecode(Urlpath);
            //}
            levelDBImpl db=new levelDBImpl();
            String unixtime=db.getUnixtime();
            String savepath = path + "/"+unixtime;
            try {
                File storageFolder = new File(savepath);
                if (!storageFolder.exists()) {
                    storageFolder.mkdirs();
                }
                String filename = title + "_" + href.substring(href.lastIndexOf("/") + 1, href.length());
                //仅对文件名的utf-8进行编码处理
                if(UrlCharTool.isUtf8Url(filename)){
                    filename=UrlCharTool.Utf8URLdecode(filename);
                }
                downLoadFromUrl(Urlpath, filename, savepath);
            } catch (Exception e) {
                System.out.println("download error" + e);
            }
        }
    }

    /**
     * 解析html文件，下载pdf,doc等文件
     *
     * @param path
     * @return
     * @throws IOException
     */
    public void parsehtml(String path, String htmltitle,boolean isshtml) {
       // parseTable();//解析表格
        parseDom(path);//解析Dom，并下载pdf,doc,docx
        /*
        if(isshtml) {
            parseTxt(path, htmltitle);//解析文档内容
        }
        */
    }
}
