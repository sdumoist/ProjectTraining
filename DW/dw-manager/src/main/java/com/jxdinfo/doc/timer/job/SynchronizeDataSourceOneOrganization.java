package com.jxdinfo.doc.timer.job;

import com.jxdinfo.doc.Synchronous.service.SynchronousService;
import com.jxdinfo.doc.common.util.SpringContextUtil;
import com.jxdinfo.hussar.quartz.job.BaseJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;

/**
 * 同步第一数据源组织机构
 * @author lq
 * @date 2022-01-05
 */
public class SynchronizeDataSourceOneOrganization implements BaseJob {

    /**
     * appCtx
     */
    private static ApplicationContext appCtx = SpringContextUtil.getApplicationContext();

    private SynchronousService synchronousService = appCtx.getBean(SynchronousService.class);

    /**
     * 执行代码
     * @param jobExecutionContext
     * @throws JobExecutionException
     */
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //同步组织机构 此方法适合同步用平台的业务系统
        //synchronousService.synchronousOrgan("1");
    }
}
