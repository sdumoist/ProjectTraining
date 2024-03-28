package com.jxdinfo.doc.mobileapi.topicmanager.controller;

import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import com.jxdinfo.doc.manager.topicmanager.model.TopicFile;
import com.jxdinfo.doc.manager.topicmanager.service.ITopicDocManagerService;
import com.jxdinfo.hussar.bsp.permit.service.ISysIdtableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.Timestamp;
import java.util.*;

/**
 * @author luzhanzhao
 * @date 2018-12-10
 * @description 分享相关的控制层
 */
@CrossOrigin
@Controller
@RequestMapping("/mobile/topic")
public class MobileTopicController {

    @Autowired
    private ITopicDocManagerService iTopicDocManagerService;

    @Autowired
    private DocInfoService docInfoService;

    @Autowired
    private IFsFolderService fsFolderService;
    /**
     * 获取编号公共方法
     */
    @Autowired
    private ISysIdtableService sysIdtableService;

    /**
     * 保存主题下文档
     *
     * @param topicId
     * @return
     */
    @RequestMapping("/saveTopicDoc")
    @ResponseBody
    public   Map<String,Object> saveTopicDoc(String topicId, String docIds) {
        List<TopicFile> list = new ArrayList();

        Map<String,Object> json = new HashMap();
        String[] docIdStr = docIds.split(",");
        for (int i = 0; i < docIdStr.length; i++) {
            String docId = docIdStr[i];
            DocInfo docInfo= docInfoService.getDocDetail(docId);
            int sameNum = 0;
            if(docInfo!=null){
                String docName = docInfo.getTitle();
                sameNum = iTopicDocManagerService.checkIsSameNameExist(docName, topicId);
            }else {

                String docName =  fsFolderService.getById(docId).getFolderName();
                sameNum = iTopicDocManagerService.checkIsSameFolderNameExist(docName, topicId);
            }
            if (sameNum != 0) {
                json.put("result", 2);
                return json;
            }
        }
        for (int i = 0; i < docIdStr.length; i++){
            String docId = docIdStr[i];
            //新增前先校验该专题下是否已经存在此文档
            int num = iTopicDocManagerService.checkIsExist(docId, topicId);
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            if (num == 0) {
                TopicFile topicFile = new TopicFile();
                topicFile.setTopicFileId(UUID.randomUUID().toString().replaceAll("-", ""));
                topicFile.setDocId(docId);
                topicFile.setSpecialTopicId(topicId);
                topicFile.setCreateTime(ts);
                String currentCode = this.sysIdtableService.getCurrentCode("TOPIC_FILE_NUM", "doc_special_topic_files");
                int bigNum = Integer.parseInt(currentCode);
                topicFile.setShowOrder(bigNum);
                list.add(topicFile);
            }
        }
        if (list != null && list.size() > 0) {
            iTopicDocManagerService.saveTopicDoc(topicId, list);
        }
        json.put("result", 1);
        return json;
    }

}