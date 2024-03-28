package com.jxdinfo.doc.timer.job;

import com.jxdinfo.doc.front.docsharemanager.dao.ShareResourceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
public class SaticScheduleTask {

    @Autowired
    private ShareResourceMapper shareResourceMapper;

    //每天23点执行
    @Scheduled(cron = "0 0 23 * * ?")
    //@Scheduled(cron = "0 */1 * * * ?")
    //或直接指定时间间隔，例如：1小时
    //@Scheduled(fixedRate=1000*60*60*24)
    private void configureTasks() {
        shareResourceMapper.deletePreviewShareData();
        System.err.println("清除由预览生成的分享数据");
    }
}
