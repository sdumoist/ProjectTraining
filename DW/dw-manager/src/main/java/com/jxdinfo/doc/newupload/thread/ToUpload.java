package com.jxdinfo.doc.newupload.thread;

import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.newupload.service.UploadService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author luzhanzhao
 * @date 2019-1-9
 * @description 用于系统启动时，对上传文件相关功能的初始化
 */
@Component
public class ToUpload implements ApplicationListener<ContextRefreshedEvent> {
    private static ThreadPoolExecutor pdfThreadPoolExecutor = new ThreadPoolExecutor(5,
            100, 10000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

    private static ThreadPoolExecutor esThreadPoolExecutor = new ThreadPoolExecutor(5,
            100, 10000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

    static final public Logger LOGGER = LogManager.getLogger(ChangeToPdfThread.class);


    @Value("${fastdfs.using}")
    private boolean fastdfsUsingFlag;

    @Resource
    private CacheToolService cacheToolService;

    @Resource
    private UploadService uploadService;

    private static ScheduledThreadPoolExecutor threadPoolExecutor = new ScheduledThreadPoolExecutor(5);

    /**
     * 获取转换pdf的线程池
     *
     * @return
     */
    public static ThreadPoolExecutor getPdfThreadPoolExecutor() {
        return pdfThreadPoolExecutor;
    }

    /**
     * 获取转换es的线程池
     *
     * @return
     */
    public static ThreadPoolExecutor getEsThreadPoolExecutor() {
        return esThreadPoolExecutor;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent  event)  {
        System.out.println("初始化转换线程");

        //设置是否启动fastDFS
        cacheToolService.setFastDFSUsingFlag(fastdfsUsingFlag);
        //从数据库中获取上传状态的列表
        List<Map<String, String>> list = uploadService.getUploadState();

        // 循环待转换文件列表
        int i = 0;
        for (Map map : list) {
            String times = null;
            if (map.containsKey("times")) {
                times = map.get("times").toString();
            }
            String docId = "";
            if (map.containsKey("docId")) {
                docId = map.get("docId").toString();
            }
            if (null != map.get("state") && times!=null) {
                //获得每个文件信息的上传状态，并进行判断
                String state = map.get("state").toString();
                int time = Integer.parseInt(times);
                if (StringUtils.isNotEmpty(docId) && time < 4) {
                    try{
                        switch (state) {
                            case "1"://如果状态为1，则为已上传未转化pdf
                                /**
                                 * 这里是为了启动项目的时候 把没有转换成功的文件 转换成pdf
                                 * 但是这里有个问题 有些文件一直转换不成功 这些文件就会一直占用libreoffice线程  导致上传文件后无法转换
                                 * 所以把这里注释掉 在unstruct类添加一个接口来转换文件 （unstruct/transPdf）
                                 */
                                //pdfThreadPoolExecutor.execute(new ChangeToPdfThread(docId));
                                i ++;
                                break;
                            case "2"://如果状态为2，则为已转化pdf未创建es
                                esThreadPoolExecutor.execute(new CreateEsThread(docId));
                                break;
                            default:
                                break;
                        }
                    }catch (Exception ex){
                        LOGGER.info(docId+"开启转换线程异常");
                        ex.printStackTrace();
                    }
                }
            }
        }
        System.out.println("待转换文件个数: " + i);
        // 定时删除不用的文件
        if (fastdfsUsingFlag) {
            // 每天凌晨1点定时执行线程方法
            long oneDay = 24 * 60 * 60 * 1000;
            long initDelay  = getTimeMillis("01:00:00") - System.currentTimeMillis();
            initDelay = initDelay > 0 ? initDelay : oneDay + initDelay;
            threadPoolExecutor.scheduleAtFixedRate(new DeleteFileThread2(),initDelay,oneDay,TimeUnit.MILLISECONDS);
        }

    }

    private static long getTimeMillis(String time) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date curDate = dateFormat.parse(dayFormat.format(new Date()) + " " + time);
            return curDate.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}

