package com.jd.crawler.leveldb;

import com.google.common.base.Charsets;
import org.iq80.leveldb.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.fusesource.leveldbjni.JniDBFactory.*;
import static org.fusesource.leveldbjni.JniDBFactory.factory;

/**
 * Created by lunan on 17-11-17.
 */
public class LevelDB {
    private DB dbData;
    private String dbname;
    public LevelDB(String dbName){
        try {
            Options options = new Options();
            options.createIfMissing(true);
            dbname=dbName;
            dbData = factory.open(new File(dbname), options);
            System.out.println("LevelDB Create Success:"+dbName);
        }catch(IOException e){
            System.out.println("Init LevelDB Error!"+e);
        }
    }

    public  void distoryDb(){
        try {
            factory.destroy(new File(dbname), null);
        }catch(IOException e){
            System.out.println("Destroy DB Error !"+e);
        }
    }
    public void closeDb(){
        try {
            dbData.close();
        }catch(IOException e){
            System.out.println("levelDBImpl Close Failed !"+e);
        }
    }

    public void putData(String key, String value){
        byte[]  bValue = value.getBytes(Charsets.UTF_8);
        byte[]  bKey = key.getBytes(Charsets.UTF_8);
        dbData.put(bKey, bValue);
    }

    public void putData( List<String> key , List<String> value) {
        try {
            WriteBatch dbDataBatch = dbData.createWriteBatch();
            for (int i = 0; i < key.size(); i++) {
                byte[] bKey = key.get(i).getBytes(Charsets.UTF_8);
                byte[] bValue = value.get(i).getBytes(Charsets.UTF_8);
                dbDataBatch.put(bKey, bValue);
            }
            dbData.write(dbDataBatch, new WriteOptions().sync(true));
            dbDataBatch.close();
        }catch(IOException e){
            System.out.println("putDataBatch Error!"+e);
        }
    }

    public void deleteData(String Key){
        byte[]  bKey = Key.getBytes(Charsets.UTF_8);
        dbData.delete(bKey);
    }

    public void deleteData(List<String> Key){
        try {
            WriteBatch dbDataBatch = dbData.createWriteBatch();
            for (int i = 0; i < Key.size(); i++) {
                byte[] bKey = Key.get(i).getBytes(Charsets.UTF_8);
                dbDataBatch.delete(bKey);
            }
            dbData.write(dbDataBatch, new WriteOptions().sync(true));
            dbDataBatch.close();
        }catch(IOException e){
            System.out.println("delDataBatch Error!"+e);
        }
    }

    public String getData(String Key){
        String value;
        byte[] bv = dbData.get(Key.getBytes(Charsets.UTF_8));
        if(bv != null && bv.length > 0) {
            value = new String(bv,Charsets.UTF_8);
            return value;
        }
        return null;
    }

    public void getSnapshot(){
        try {
            //iterator，遍历，顺序读
            //读取当前snapshot，快照，读取期间数据的变更，不会反应出来
            Snapshot snapshot = dbData.getSnapshot();
            //读选项
            ReadOptions readOptions = new ReadOptions();
            readOptions.fillCache(false);//遍历中swap出来的数据，不应该保存在memtable中。
            readOptions.snapshot(snapshot);//默认snapshot为当前。
            DBIterator iterator = dbData.iterator(readOptions);
            while (iterator.hasNext()) {
                Map.Entry<byte[], byte[]> item = iterator.next();
                String key = new String(item.getKey(), Charsets.UTF_8);
                String value = new String(item.getValue(), Charsets.UTF_8);//null,check.
                System.out.println(key + ":" + value);

            }
            iterator.close();//must be

        }catch(IOException e){
            System.out.println("Iterator Error !");
        }
    }

}
