package com.jxdinfo.doc.timer.job;

import com.jxdinfo.doc.common.util.SpringContextUtil;
import com.jxdinfo.doc.interfaces.system.service.YYZCUserService;
import com.jxdinfo.hussar.quartz.job.BaseJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;

/**
 * 
 * 类的用途：获取人员照片定时任务
 * 创建日期：2018年7月13日 <br>
 * 修改历史：<br>
 * 修改日期：2018年7月6日 <br>
 * 修改作者：XuXinYing <br>
 * 修改内容：修改内容 <br>
 * @author XuXinYing
 * @version 1.0
 */
public class GetUserPhoto implements BaseJob {

    /**
     * appCtx
     */
    private static ApplicationContext appCtx = SpringContextUtil.getApplicationContext();

    /**
     * YYZCUserEntityService
     */
    private static YYZCUserService YYZCUserService = appCtx.getBean(YYZCUserService.class);// 这样直接调用就好

    /**
     * 执行代码
     * @Title: execute
     * @param context
     * @throws JobExecutionException
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // YYZCUserEntityService.getUserPhotoInfo();
    }

}
