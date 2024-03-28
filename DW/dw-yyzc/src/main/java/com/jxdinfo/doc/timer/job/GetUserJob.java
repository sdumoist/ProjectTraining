package com.jxdinfo.doc.timer.job;

import com.jxdinfo.doc.common.util.SpringContextUtil;
import com.jxdinfo.doc.interfaces.system.model.YYZCUser;
import com.jxdinfo.doc.interfaces.system.service.YYZCUserService;
import com.jxdinfo.doc.timer.client.ApiClient;
import com.jxdinfo.hussar.quartz.job.BaseJob;
import net.sf.json.JSONArray;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 类的用途：获取人员定时任务
 * 创建日期：2018年7月6日 <br>
 * 修改历史：<br>
 * 修改日期：2018年7月6日 <br>
 * 修改作者：XuXinYing <br>
 * 修改内容：修改内容 <br>
 * @author XuXinYing
 * @version 1.0
 */
public class GetUserJob implements BaseJob {

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
         getNewUserInfo();
    }

    /**
     * 更新员工信息
     * @Title: getNewUserInfo 
     * @author: XuXinYing
     */
    public void getNewUserInfo() {
        System.out.println("定时同步用户开始");
        final ApiClient client = new ApiClient();
        final String userList = client.userList();
        List<YYZCUser> userInfoList = new ArrayList<YYZCUser>();
        final JSONArray userJson = JSONArray.fromObject(userList);
        userInfoList = JSONArray.toList(userJson, YYZCUser.class);
        YYZCUserService.insertOrUpdateYyzcUser(userInfoList);
        System.out.println("定时同步用户结束");
    }

}
