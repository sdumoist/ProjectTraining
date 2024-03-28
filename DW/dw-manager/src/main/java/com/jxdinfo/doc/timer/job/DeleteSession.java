package com.jxdinfo.doc.timer.job;

import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.common.util.SpringContextUtil;
import com.jxdinfo.doc.front.groupmanager.service.FrontDocGroupService;
import com.jxdinfo.doc.front.topicmanager.service.FrontTopicService;
import com.jxdinfo.doc.manager.statistics.service.FileStatisticsService;
import com.jxdinfo.hussar.quartz.job.BaseJob;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;


/**
 * @author yjs
 * @Description:
 * @Date: 2018/11/23 10:00
 */
public class DeleteSession implements BaseJob {

    /**
     * appCtx
     */
    private static ApplicationContext appCtx = SpringContextUtil.getApplicationContext();

    /**
     * 文件统计接口
     */

    private SessionDAO sessionDAO = appCtx.getBean(SessionDAO.class);

    private FileStatisticsService fileStatisticsService = appCtx.getBean(FileStatisticsService.class);

    private FrontTopicService frontTopicService = appCtx.getBean(FrontTopicService.class);

    private BusinessService businessService = appCtx.getBean(BusinessService.class);

    private FrontDocGroupService frontDocGroupService = appCtx.getBean(FrontDocGroupService.class);

    /**
     * 执行代码
     *
     * @param context
     * @throws JobExecutionException
     * @Title: execute
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        saveIndexCache();
       
    }

    public void saveIndexCache() {
        Collection<Session> collection = sessionDAO.getActiveSessions();
        Iterator<Session> it = collection.iterator();

        while (it.hasNext()) {
            Session session = it.next();
            Date lastAccessDate = session.getLastAccessTime();
            long timeout = session.getTimeout();
            if ((lastAccessDate.getTime() + timeout) <  System.currentTimeMillis()) {
                sessionDAO.delete(session);
            }
        }
    }

}
