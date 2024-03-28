package com.jxdinfo.doc.manager.middlegroundConsulation.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.manager.componentmanager.service.ComponentApplyAttachmentService;
import com.jxdinfo.doc.manager.componentmanager.service.ComponentApplyService;
import com.jxdinfo.doc.manager.middlegroundConsulation.dao.ConsulationAttachmentMapper;
import com.jxdinfo.doc.manager.middlegroundConsulation.model.MiddlegroundConsulation;
import com.jxdinfo.doc.manager.middlegroundConsulation.model.MiddlegroundConsulationAttachment;
import com.jxdinfo.doc.manager.middlegroundConsulation.service.MiddlegroundConsulationService;
import com.jxdinfo.hussar.common.treemodel.JSTreeModel;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * describe:
 *
 * @author lixin
 * @date 2020/01/08
 */
@Controller
@RequestMapping("/middlegroundConsulation")
public class MiddlegroundConsulationController {
    @Resource
    private ComponentApplyService componentApplyService;

    @Resource
    private ComponentApplyAttachmentService componentApplyAttachmentService;

    @Resource
    private MiddlegroundConsulationService middlegroundConsulationService;
    @Resource
    private ConsulationAttachmentMapper consulationAttachmentMapper;
    @GetMapping("/toAddMeetingReord")
    public String toAddMeetingRecord(Model model){

        model.addAttribute("style", "");
        model.addAttribute("origin", "");
        model.addAttribute("state", "");
        model.addAttribute("dept", "");
        model.addAttribute("type", "");
        model.addAttribute("userName", ShiroKit.getUser().getName());
        model.addAttribute("consulationId", UUID.randomUUID().toString().replaceAll("-", ""));
        return "/doc/manager/middlegroundConsulation/middlegroundConsulation_add.html";
    }

    @GetMapping("/toEditMeetingReord")
    public String toEditMeetingRecord(Model model,String consulationId){

        MiddlegroundConsulation middlegroundConsulation = middlegroundConsulationService.selectById(consulationId);
        model.addAttribute("userName", ShiroKit.getUser().getName());
        model.addAttribute("consulationId", consulationId);
        model.addAttribute("middlegroundConsulation", middlegroundConsulation);
        List<MiddlegroundConsulationAttachment> list = consulationAttachmentMapper.getAttachmentList(consulationId);

        model.addAttribute("AttachmentList", list);
        return "/doc/manager/middlegroundConsulation/middlegroundConsulation_edit.html";

    }

    /**
     * 选择项目
     *
     * @return 项目选择页面
     */
    @GetMapping("/projectListView")
    public String projectListView() {
        return "/doc/manager/middlegroundConsulation/projectListView.html";
    }

    /**
     * 选择部门
     *
     * @return 部门选择页面
     */
    @GetMapping("/deptListView")
    public String deptListView(String type,Model model) {
        model.addAttribute("type", type);
        return "/doc/manager/middlegroundConsulation/deptTreeView.html";
    }

    @PostMapping("/deptTreeList")
    @ResponseBody
    public List<JSTreeModel> deptTreeList() {
        List<JSTreeModel> result = middlegroundConsulationService.getDeptTree();
        JSTreeModel jsTreeModel = new JSTreeModel();
        jsTreeModel.setId("11");
        jsTreeModel.setCode("11");
        jsTreeModel.setText("系统用户");
        jsTreeModel.setParent("#");
        jsTreeModel.setType("isRoot");
        result.add(jsTreeModel);
        List<JSTreeModel> result2= new ArrayList();
        for ( int i=0;i<result.size();i++){
            if(result.get(i).getId().equals("superadmin")||result.get(i).getId().equals("wkadmin")||
                    result.get(i).getId().equals("auditadmin")||result.get(i).getId().equals("reviewadmin")||result.get(i).getId().equals("systemadmin")){
                continue;
            }else{
                result2.add(result.get(i));
            }
        }
        return result2;
    }

    @PostMapping("/addMiddlegroundConsulation")
    @ResponseBody
    public JSONObject addRecord(String consulationId,String deptName, String deptId, String time, String projectName, String projectDesc,
                                String projectId, String participant, String content ,String idArr,String nameArr,String typeArr,String contentText) {
        JSONObject jsonObject = new JSONObject();
        String title = projectDesc;
        MiddlegroundConsulation middlegroundConsulation = new MiddlegroundConsulation();
        middlegroundConsulation.setConsulationId(consulationId);
        middlegroundConsulation.setConsulationContent(content);
//        time = time + " 00:00:00";
//        Timestamp consulationTime = Timestamp.valueOf(time);
        middlegroundConsulation.setConsulationTime(time);
        middlegroundConsulation.setConsulationTitle(title);
        middlegroundConsulation.setCreateUserName(ShiroKit.getUser().getName());
        middlegroundConsulation.setCreateUserId(ShiroKit.getUser().getId());
        middlegroundConsulation.setDeptId(deptId);
        middlegroundConsulation.setDeptName(deptName);
//        middlegroundConsulation.setProjectId(projectId);
//        middlegroundConsulation.setProjectName(projectName);
        middlegroundConsulation.setProjectDesc(projectDesc);
        middlegroundConsulation.setParticipant(participant);
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        middlegroundConsulation.setCreateTime(ts);
        middlegroundConsulation.setState("1");
        middlegroundConsulation.setContentText(contentText);
        if (!"".equals(idArr)&&idArr!=null) {
            String[] idArrStr = idArr.split(",");
            String[] nameArrStr = nameArr.split(",");
            String[] typeArrStr = typeArr.split(",");
            for (int i = 0; i < idArrStr.length; i++) {
                MiddlegroundConsulationAttachment middlegroundConsulationAttachment = new MiddlegroundConsulationAttachment();
                middlegroundConsulationAttachment.setAttachmentId(idArrStr[i]);
                middlegroundConsulationAttachment.setAttachmentName(nameArrStr[i]);
                middlegroundConsulationAttachment.setAttachmentType(typeArrStr[i]);
                middlegroundConsulationAttachment.setConsulationId(middlegroundConsulation.getConsulationId());
                consulationAttachmentMapper.insert(middlegroundConsulationAttachment);
            }

        }
        boolean result = middlegroundConsulationService.save(middlegroundConsulation);
        jsonObject.put("result",result);

        return jsonObject;
    }

    @PostMapping("/editMiddlegroundConsulation")
    @ResponseBody
    public JSONObject editRecord(String consulationId,String deptName, String deptId, String time, String projectName, String projectDesc,
                                String projectId, String participant, String content ,String idArr,String nameArr,String typeArr,String contentText) {
        JSONObject jsonObject = new JSONObject();
        String title = projectDesc;
        MiddlegroundConsulation middlegroundConsulation = new MiddlegroundConsulation();
        middlegroundConsulation.setConsulationId(consulationId);
        middlegroundConsulation.setConsulationContent(content);
        middlegroundConsulation.setConsulationTime(time);
        middlegroundConsulation.setConsulationTitle(title);
        middlegroundConsulation.setCreateUserName(ShiroKit.getUser().getName());
        middlegroundConsulation.setCreateUserId(ShiroKit.getUser().getId());
        middlegroundConsulation.setDeptId(deptId);
        middlegroundConsulation.setDeptName(deptName);
        middlegroundConsulation.setProjectDesc(projectDesc);
        middlegroundConsulation.setParticipant(participant);
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        middlegroundConsulation.setUpdateTime(ts);
        middlegroundConsulation.setState("1");
        middlegroundConsulation.setContentText(contentText);
        List<MiddlegroundConsulationAttachment> attachment = consulationAttachmentMapper.getAttachmentList(consulationId);
        if (attachment.size()==0){
            if (!"".equals(idArr)&&idArr!=null) {
                String[] idArrStr = idArr.split(",");
                String[] nameArrStr = nameArr.split(",");
                String[] typeArrStr = typeArr.split(",");
                for (int i = 0; i < idArrStr.length; i++) {
                    MiddlegroundConsulationAttachment middlegroundConsulationAttachment = new MiddlegroundConsulationAttachment();
                    middlegroundConsulationAttachment.setAttachmentId(idArrStr[i]);
                    middlegroundConsulationAttachment.setAttachmentName(nameArrStr[i]);
                    middlegroundConsulationAttachment.setAttachmentType(typeArrStr[i]);
                    middlegroundConsulationAttachment.setConsulationId(middlegroundConsulation.getConsulationId());
                    consulationAttachmentMapper.insert(middlegroundConsulationAttachment);
                }

            }
        }else {
            consulationAttachmentMapper.deleteByConsulationId(consulationId);
            if (!"".equals(idArr)&&idArr!=null) {
                String[] idArrStr = idArr.split(",");
                String[] nameArrStr = nameArr.split(",");
                String[] typeArrStr = typeArr.split(",");
                for (int i = 0; i < idArrStr.length; i++) {
                    MiddlegroundConsulationAttachment middlegroundConsulationAttachment = new MiddlegroundConsulationAttachment();
                    middlegroundConsulationAttachment.setAttachmentId(idArrStr[i]);
                    middlegroundConsulationAttachment.setAttachmentName(nameArrStr[i]);
                    middlegroundConsulationAttachment.setAttachmentType(typeArrStr[i]);
                    middlegroundConsulationAttachment.setConsulationId(middlegroundConsulation.getConsulationId());
                    consulationAttachmentMapper.insert(middlegroundConsulationAttachment);
                }
            }
        }
        boolean result = middlegroundConsulationService.updateById(middlegroundConsulation);
        jsonObject.put("result",result);

        return jsonObject;
    }

    @PostMapping("/getMiddlegroundConsulationList")
    @ResponseBody
    public JSON getMiddlegroundConsulationList(boolean isMy, String deptId, @RequestParam(value = "page", defaultValue = "1")
            int page, @RequestParam(value = "limit", defaultValue = "10") int limit) {
        JSONObject jsonObject = new JSONObject();
        List<String> roleList = ShiroKit.getUser().getRolesList();
        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        Integer adminFlag = CommonUtil.getWYHFlag(roleList);
        String userName = null;
        if (isMy){
            userName = ShiroKit.getUser().getName();
        }
        int beginIndex = page * limit - limit;
        List<MiddlegroundConsulation> middlegroundConsulationList = new ArrayList<MiddlegroundConsulation>();
        middlegroundConsulationList = middlegroundConsulationService.getMiddlegroundList(userName, deptId, beginIndex,limit);
        int middlegroundConsulationCount = middlegroundConsulationService.getMiddlegroundCount(userName, deptId);

        jsonObject.put("count", middlegroundConsulationCount);
        jsonObject.put("data", middlegroundConsulationList);
        jsonObject.put("adminFlag", adminFlag);
        jsonObject.put("msg", "success");
        jsonObject.put("code", 0);

        return jsonObject;
    }
    @PostMapping("/deleteMiddlegroundConsulation")
    @ResponseBody
    public int deleteMiddlegroundConsulation(String middleGroundConsulationId) {

        int middlegroundConsulationList = middlegroundConsulationService.deleteMiddlegroundConsulation(middleGroundConsulationId);


        return middlegroundConsulationList;
    }
}
