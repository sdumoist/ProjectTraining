package com.jxdinfo.doc.manager.topicmanager.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jxdinfo.doc.common.constant.DocConstant;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.common.util.XSSUtil;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.model.DocResourceLog;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import com.jxdinfo.doc.manager.topicmanager.model.SpecialTopic;
import com.jxdinfo.doc.manager.topicmanager.model.TopicFile;
import com.jxdinfo.doc.manager.topicmanager.service.ITopicDocManagerService;
import com.jxdinfo.hussar.bsp.permit.service.ISysIdtableService;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.support.HttpKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @author xb
 * @Description:
 * @Date: 2018/7/4 13:53
 */
@Controller
@RequestMapping("/topicDoc")
public class TopicDocManagerController {
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

    @RequestMapping("/topicDocAdd")
    public String specialTopicQuery(HttpServletRequest request, Model model) {
        String chooseFile = request.getParameter("chooseFile") == null ? "" : request.getParameter("chooseFile");
        String chooseFileType = request.getParameter("chooseFileType") == null ? "" : request.getParameter("chooseFileType");
        chooseFileType = XSSUtil.xss(chooseFileType);
        chooseFile = XSSUtil.xss(chooseFile);
        model.addAttribute("fsFiles", chooseFile);
        model.addAttribute("chooseFileType", chooseFileType);
        return "/doc/manager/topicmanager/topic_doc.html";
    }

    /**
     * 主題查詢下拉框
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/searchTopic")
    @ResponseBody
    public JSON searchTopic(HttpServletRequest request, HttpServletResponse response) {
        List list = iTopicDocManagerService.searchTopic();
        JSONObject json = new JSONObject();
        json.put("data", list);
        return json;
    }

    /**
     * 根据文件夹查询文档
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/getDocByFsFile")
    @ResponseBody
    public JSON getDocByFsFile(HttpServletRequest request, HttpServletResponse response) {
        String fsFiles = request.getParameter("fsFiles") == null ? "" : request.getParameter("fsFiles");
        String chooseFileType = request.getParameter("chooseFileType") == null ? "" : request.getParameter("chooseFileType");
        List<String> list = Arrays.asList(fsFiles.split(","));
        List docList = iTopicDocManagerService.getDocByFsFile(list);
        JSONObject json = new JSONObject();
        json.put("count", docList.size());
        json.put("data", docList);
        json.put("msg", "success");
        json.put("code", 0);
        return json;
    }

    /**
     * 保存主题下文档
     *
     * @param topicId
     * @param docData
     * @return
     */
    @RequestMapping("/saveTopicDoc")
    @ResponseBody
    public JSON saveTopicDoc(String topicId, String docData) {
        JSONObject json = new JSONObject();
        Integer adminFlag = CommonUtil.getAdminFlag();
        if (!DocConstant.ADMINFLAG.WKADMIN.getValue().equals(adminFlag)) {
            json.put("result", 3);
            return json;
        }
        net.sf.json.JSONArray jsonArray = net.sf.json.JSONArray.fromObject(docData);
        List<TopicFile> list = new ArrayList();
        for (int i = 0; i < jsonArray.size(); i++) {
            net.sf.json.JSONObject obj = (net.sf.json.JSONObject) jsonArray.get(i);
            String docId = obj.get("docId").toString();
            DocInfo docInfo= docInfoService.getDocDetail(docId);

            int sameNum = 0;
            if(docInfo!=null){
                sameNum = iTopicDocManagerService.checkIsSameNameExist(docId, topicId);
            }else {

                sameNum = iTopicDocManagerService.checkIsSameFolderNameExist(docId, topicId);
            }
            if (sameNum == 0) {
                Timestamp ts = new Timestamp(System.currentTimeMillis());
                TopicFile topicFile = new TopicFile();
                topicFile.setTopicFileId(UUID.randomUUID().toString().replaceAll("-", ""));
                topicFile.setDocId(docId);
                topicFile.setSpecialTopicId(topicId);
                topicFile.setCreateTime(ts);
                String currentCode = this.sysIdtableService.getCurrentCode("TOPIC_FILE_NUM", "doc_special_topic_files");
                int bigNum = Integer.parseInt(currentCode);
                topicFile.setShowOrder(bigNum);
                list.add(topicFile);
                //拼装操作历史记录
                List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
                DocResourceLog docResourceLog = new DocResourceLog();
                String id = UUID.randomUUID().toString().replace("-", "");
                docResourceLog.setId(id);
                docResourceLog.setResourceId(docId);
                docResourceLog.setOperateTime(ts);
                docResourceLog.setResourceType(2);
                docResourceLog.setUserId(ShiroKit.getUser().getId());
                docResourceLog.setOperateType(15);
                docResourceLog.setAddressIp(HttpKit.getIp());
                docResourceLog.setOrigin("pc");
                resInfoList.add(docResourceLog);

                iTopicDocManagerService.insertResourceLog(resInfoList);
            }
        }
        if (list != null && list.size() > 0) {
              iTopicDocManagerService.saveTopicDoc(topicId, list);
        }
        json.put("result", 1);
        return json;
    }

    @RequestMapping("/topicFrontView")
    public String topicFrontView(HttpServletRequest request, Model model, int page, int size) {
        String topicId = request.getParameter("topicId") == null ? "" : request.getParameter("topicId");
        //根据TopicIdid查询专题信息
        SpecialTopic specialTopic = iTopicDocManagerService.getTopicDetail(topicId);
        //根据主题信息查询文件信息
        int startNum = page * size - size;
//        List<DocDetail> list = iTopicDocManagerService.getTopicDoclist(topicId,startNum,size);
//        model.addAttribute("specialTopic", specialTopic);
//        model.addAttribute("list", list);
        return "/docbase/admin/generalmanage/topic_front_view.jsp";
    }

    @RequestMapping("/topicFrontList")
    public String topicFrontList(Model model) {
        //查询所有专题信息
        List<SpecialTopic> list = iTopicDocManagerService.getTopicList();
        model.addAttribute("list", list);
        return "/docbase/admin/generalmanage/topic_front_list.jsp";
    }


    /**
     * 根据ID查询文档列表
     *
     * @param page    页数
     * @param limit   每页数据条数
     * @param topicId 专题ID
     * @return layui table返回值
     */
    @RequestMapping("/getDocListByIds")
    @ResponseBody
    public JSON getDocListByIds(String topicId, int page, int limit) {
        int startNum = page * limit - limit;
        List docList = iTopicDocManagerService.getTopicDoclist(topicId, startNum, limit);
        JSONObject json = new JSONObject();
        json.put("count", docList.size());
        json.put("data", docList);
        json.put("msg", "success");
        json.put("code", 0);
        return json;
    }
}
