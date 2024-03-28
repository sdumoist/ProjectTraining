package com.jxdinfo.doc.newupload.thread;

import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.util.ExceptionUtils;
import com.jxdinfo.hussar.core.util.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import java.io.File;

/**
 * @author xuguijie
 * @date 2020-6-23
 * @description 删除服务器本地文件的线程
 */
public class DeleteFileThread2 extends Thread {

    static final public Logger LOGGER = LoggerFactory.getLogger(DeleteFileThread2.class);

    private CacheToolService cacheToolService = SpringContextHolder.getBean(CacheToolService.class);
    private Environment environment = SpringContextHolder.getBean(Environment.class);

    @Override
    public void run() {
        if (cacheToolService.getFastDFSUsingFlag()) {
            // 获取目录地址
//            String fileDirPath = environment.getProperty("docbase.filedir");
//            String fileByKeyPath = environment.getProperty("docbase.fileByKey");
//            String pdfFilePath = environment.getProperty("docbase.pdfFile");
//            String pdfFileByKeyPath = environment.getProperty("docbase.pdfFileByKey");
            String downloadFilePath = environment.getProperty("docbase.downloadFile");
            String downloadPdfFilePath = environment.getProperty("docbase.downloadPdfFile");
            String downloadFileByKeyPath = environment.getProperty("docbase.downloadFileByKey");
            String downloadPdfFileByKeyPath = environment.getProperty("docbase.downloadPdfFileByKey");

//            deleteDirectory(fileDirPath);
//            deleteDirectory(fileByKeyPath);
//            deleteDirectory(pdfFilePath);
//            deleteDirectory(pdfFileByKeyPath);
            deleteDirectory(downloadFilePath);
            deleteDirectory(downloadPdfFilePath);
            deleteDirectory(downloadFileByKeyPath);
            deleteDirectory(downloadPdfFileByKeyPath);
        }
    }

    /**
     * 删除目录下的文件
     * @param path 目录地址
     */
    public void deleteDirectory(String path) {
        //如果path不以文件分隔符结尾，自动添加文件分隔符
        if (!path.endsWith(File.separator)) {
            path = path + File.separator;
        }
        File dirFile = new File(path);
        //如果dir对应的文件不存在，或者不是一个目录，则退出
        if (dirFile.exists() && dirFile.isDirectory()) {
            //删除文件夹下的所有文件(包括子目录)
            File[] files = dirFile.listFiles();
            long nowTime = System.currentTimeMillis();
            for (int i = 0; i < files.length; i++) {
                //删除子文件
                if (files[i].isFile()) {
                    // 获取修改时间，删除3小时之前的数据
                    long fileTime = files[i].lastModified();
                    long diff = nowTime - fileTime;
                    if (diff > 3 * 60 * 60 * 1000) {
                        deleteFile(files[i].getAbsolutePath());
                    }
                } else {
                    deleteDirectory(files[i].getAbsolutePath());
                }
            }
            //删除当前目录
//            dirFile.delete();
        }

    }

    /**
     * 删除文件
     * @param path 文件地址
     */
    public void deleteFile(String path) {
        File file = new File(path);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            try {
                file.delete();
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error("定时删除文件失败，文件路径：" + path + "\n" + ExceptionUtils.getErrorInfo(e));
            }
        }
    }

}