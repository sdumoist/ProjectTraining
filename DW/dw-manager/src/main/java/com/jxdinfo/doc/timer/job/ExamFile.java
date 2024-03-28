package com.jxdinfo.doc.timer.job;

import com.jxdinfo.doc.common.docutil.service.ESService;
import com.jxdinfo.doc.common.util.ESUtil;
import com.jxdinfo.doc.common.util.SpringContextUtil;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.statistics.service.FileStatisticsService;
import com.jxdinfo.hussar.quartz.job.BaseJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;

/**
 * @author luzhanzhao
 * @date 2018-12-19
 * @description 审核文件定时任务
 */
public class ExamFile implements BaseJob {
    /**
     * appCtx
     */
    private static ApplicationContext appCtx = SpringContextUtil.getApplicationContext();



    private DocInfoService docInfoService = appCtx.getBean(DocInfoService.class);

    /**
     * 文件统计接口
     */
    private FileStatisticsService fileStatisticsService = appCtx.getBean(FileStatisticsService.class);


    /** es服务类  */
    private ESService esService = appCtx.getBean(ESService.class);


    private ESUtil esUtil = appCtx.getBean(ESUtil.class);





    /**
     * 执行代码
     *
     * @param context
     * @throws JobExecutionException
     * @Title: execute
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("===========定时器执行===========开始审核文件");
        exam();
        System.out.println("===========定时器执行===========审核文件完成");
    }

    public void exam(){

    }
}
