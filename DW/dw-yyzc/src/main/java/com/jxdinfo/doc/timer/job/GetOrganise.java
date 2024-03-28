package com.jxdinfo.doc.timer.job;


import com.jxdinfo.doc.common.util.SpringContextUtil;
import com.jxdinfo.doc.interfaces.system.model.YYZCOrganise;
import com.jxdinfo.doc.interfaces.system.service.YYZCOrganiseService;
import com.jxdinfo.doc.timer.client.ApiClient;
import com.jxdinfo.hussar.quartz.job.BaseJob;
import net.sf.json.JSONArray;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

public class GetOrganise implements BaseJob {
    private static ApplicationContext appCtx = SpringContextUtil.getApplicationContext();
    private static YYZCOrganiseService yyzcOrganiseService =
            (YYZCOrganiseService) appCtx.getBean(YYZCOrganiseService.class);
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        //getOrganise();
    }
    public void getOrganise(){
        System.out.println("定时同步组织机构开始");
        ApiClient client = new ApiClient();
        String OrganiseList = client.orgOrganise();
        List<YYZCOrganise> organisesInfoList = new ArrayList<YYZCOrganise>();
        JSONArray organiseJson = JSONArray.fromObject(OrganiseList);

        organisesInfoList=(List<YYZCOrganise>)JSONArray.toList(organiseJson, YYZCOrganise.class);

        yyzcOrganiseService.insertOrUpdateYYZCOrganise(organisesInfoList);
        System.out.println("定时同步组织机构结束");
    }
}
