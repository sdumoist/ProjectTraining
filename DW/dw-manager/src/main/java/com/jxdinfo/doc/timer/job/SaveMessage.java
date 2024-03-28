package com.jxdinfo.doc.timer.job;

import com.jxdinfo.doc.common.docutil.service.impl.PdfServiceImpl;
import com.jxdinfo.doc.common.util.SpringContextUtil;
import com.jxdinfo.doc.manager.topicmanager.model.Message;
import com.jxdinfo.doc.manager.topicmanager.service.MessageService;
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

public class SaveMessage implements BaseJob {

    private static ApplicationContext appCtx = SpringContextUtil.getApplicationContext();

    static final public Logger LOGGER = LoggerFactory.getLogger(PdfServiceImpl.class);
    private MessageService messageService = appCtx.getBean(MessageService.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        saveMessage();
    }

    private void saveMessage() {
        final ApiClient client = new ApiClient();
        List<Message> list=  messageService.getList(null,null,null,0,1);
        String time="";
        if(list!=null&&list.size()!=0){
             time = list.get(0).getADDTIME();
        }
        String url = ApiURL.MESSAGELIST.getUrl() + "?currentTime=" + time;
        final String userString = client.messageList(url.replaceAll(" ","%20"));
        List<Message> messageList = new ArrayList<Message>();
        final JSONArray messageJson = JSONArray.fromObject(userString);
        messageList = JSONArray.toList(messageJson, Message.class);
//        messageService.deleteMessage();
        if(messageList.size()!=0){
            messageService.saveOrUpdateBatch(messageList);
        }
    }
}

