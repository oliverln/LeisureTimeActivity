package com.jd.crawler.leveldb.impl;

import com.jd.crawler.leveldb.LevelDB;

/**
 * Created by lunan on 17-11-17.
 */
public class levelDBImpl {
    public static LevelDB urlDB;
    public static LevelDB linksDB;
    public static LevelDB parentDB;
    public static LevelDB htmlDB;
    public static LevelDB filesDB;
    public static LevelDB titleDB;
    public static String unixtime;

    public  String getUnixtime() {
        return unixtime;
    }

    public  void setUnixtime(String unixtime) {
        levelDBImpl.unixtime = unixtime;
    }

    public LevelDB getUrlDB() {
        return urlDB;
    }

    public void setUrlDB(String storageFolder,String unixtime) {
        String dbname=storageFolder+"/urlDB/"+unixtime;
        this.urlDB = new LevelDB(dbname);

    }

    public LevelDB getFilesDB() {
        return filesDB;
    }

    public void setFilesDB(String storageFolder,String unixtime) {
        String dbname=storageFolder+"/filesDB/"+unixtime;
        this.filesDB =new LevelDB(dbname);
    }

    public LevelDB getHtmlDB() {
        return htmlDB;
    }

    public void setHtmlDB(String storageFolder,String unixtime) {
        String dbname=storageFolder+"/htmlDB/"+unixtime;
        this.htmlDB = new LevelDB(dbname);
    }

    public LevelDB getLinksDB() {
        return linksDB;
    }

    public void setLinksDB(String storageFolder,String unixtime) {
        String dbname=storageFolder+"/linksDB/"+unixtime;
        this.linksDB = new LevelDB(dbname);
    }

    public LevelDB getParentDB() {
        return parentDB;
    }

    public void setParentDB(String storageFolder,String unixtime) {
        String dbname=storageFolder+"/parentDB/"+unixtime;
        this.parentDB = new LevelDB(dbname);
    }

    public LevelDB getTitleDB() {
        return titleDB;
    }

    public void setTitleDB(String storageFolder,String unixtime) {
        String dbname=storageFolder+"/titleDB/"+unixtime;
        this.titleDB = new LevelDB(dbname);
    }

    public void closeAllDB(){
        this.urlDB.closeDb();
        this.htmlDB.closeDb();
        this.parentDB.closeDb();
        this.filesDB.closeDb();
        this.titleDB.closeDb();
        this.linksDB.closeDb();
    }

    public void createAllDB(String Path){
        try {
            this.setUrlDB(Path,this.unixtime);
            this.setHtmlDB(Path,this.unixtime);
            this.setTitleDB(Path,this.unixtime);
            this.setLinksDB(Path,this.unixtime);
            this.setParentDB(Path,this.unixtime);
            this.setFilesDB(Path,this.unixtime);
        }catch (Exception e){
            System.out.println("Exception CreateDB!");
            System.exit(1);
        }
    }
}
