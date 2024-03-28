package com.jxdinfo.doc.timer.job;

import com.jxdinfo.doc.common.docutil.service.impl.PdfServiceImpl;
import com.jxdinfo.doc.common.util.SpringContextUtil;
import com.jxdinfo.doc.manager.componentmanager.model.YYZCProject;
import com.jxdinfo.doc.manager.componentmanager.service.MultiplexProjectService;
import com.jxdinfo.doc.manager.componentmanager.service.YYZCProjectService;
import com.jxdinfo.doc.timer.client.ApiClient;
import com.jxdinfo.doc.timer.constants.ApiURL;
import com.jxdinfo.hussar.quartz.job.BaseJob;
import net.sf.json.JSONArray;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

public class SaveYYZXProject implements BaseJob {

    private static ApplicationContext appCtx = SpringContextUtil.getApplicationContext();

    static final public Logger LOGGER = LoggerFactory.getLogger(PdfServiceImpl.class);

    private MultiplexProjectService multiplexProjectService = appCtx.getBean(MultiplexProjectService.class);
    private YYZCProjectService yyzcProjectService = appCtx.getBean(YYZCProjectService.class);
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        SaveYYZXProject();
    }

    private void SaveYYZXProject() {
        final ApiClient client = new ApiClient();
        List<YYZCProject> list = multiplexProjectService.projectList(null, null, 0, 1);
        String time = "";
        if (list != null && list.size() != 0) {
            time =
                    list.get(0).getCREATEDATE();
        }
        String url = ApiURL.PROJECTLIST.getUrl() + "?currentTime=" + time;
        final String userString = client.messageList(url.replaceAll(" ", "%20"));
        List<YYZCProject> messageList = new ArrayList<YYZCProject>();
        if(!userString.equals("")) {


            final JSONArray messageJson = JSONArray.fromObject(userString);
            messageList = JSONArray.toList(messageJson, YYZCProject.class);
//        messageService.deleteMessage();
            if (messageList.size() != 0) {
                yyzcProjectService.saveOrUpdateBatch(messageList);
            }
        }
    }
    }

