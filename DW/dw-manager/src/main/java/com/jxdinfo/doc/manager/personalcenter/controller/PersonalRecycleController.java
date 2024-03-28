package com.jxdinfo.doc.manager.personalcenter.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.common.util.UserInfoUtil;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.docmanager.service.FsFileService;
import com.jxdinfo.doc.manager.docrecycle.service.IDocRecycleService;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.foldermanager.service.IDocFoldAuthorityService;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import com.jxdinfo.doc.manager.groupmanager.service.DocGroupService;
import com.jxdinfo.doc.manager.statistics.service.DeptStatisticsService;
import com.jxdinfo.doc.manager.statistics.service.EmpStatisticsService;
import com.jxdinfo.hussar.core.base.controller.BaseController;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import com.jxdinfo.hussar.core.util.ToolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/personalRecycle")
public class PersonalRecycleController extends BaseController{
    @Autowired
    private FsFileService fsFileService;
    @Autowired
    private DocInfoService docInfoService;

    @Autowired
    private DocGroupService docGroupService;

    @Autowired
    private IFsFolderService fsFolderService;

    @Autowired
    private EmpStatisticsService empStatisticsService;

    /**
     * 目录服务类
     */
    @Resource
    private BusinessService businessService;
    /**
     * 回收站服务类
     */
    @Autowired
    private IDocRecycleService iDocRecycleService;
    @Autowired
    private IDocFoldAuthorityService docFoldAuthorityService;
    /**
     * 部门文件大小 Service接口
     */
    @Autowired
    private DeptStatisticsService deptStatisticsService;

    /**
     * @author luzhanzhao
     * @date 2018-11-16
     * @description 返回预览记录列表
     * @param name 关键字
     * @param pageNumber 当前页数
     * @param pageSize 每页长度
     * @return
     * @edit zhongguangrui
     */
    @PostMapping("/list")
    @ResponseBody
    public Map list(String name, @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber, @RequestParam(defaultValue = "60") int pageSize,String order){
        List<String> roleList = ShiroKit.getUser().getRolesList();
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        //获取当前登录人
        ShiroUser shiroUser = ShiroKit.getUser();
        String userId = shiroUser.getId();
        FsFolderParams fsFolderParams = new FsFolderParams();
        List<String> listGroup = docGroupService.getPremission(userId);
        fsFolderParams.setUserId(userId);
        fsFolderParams.setGroupList(listGroup);
        //获得目录管理权限层级码
        fsFolderParams.setType("2");
//        String levelCode = businessService.getLevelCodeByUserUpload(fsFolderParams);
        Map<String, Object> list = iDocRecycleService.getDocRecycleList(String.valueOf(pageNumber),String.valueOf(pageSize),name,order);
        int count =(Integer)list.get("count");
        Map histories = new HashMap();
        histories.put("msg","success");
        histories.put("code",0);
        histories.put("adminFlag",adminFlag);
        histories.put("rows",list);
        histories.put("count",count);
        return histories;
    }
    /**
     * 清空回收站
     *
     * @return boolean
     * @author: ZhongGuangrui
     */
    @PostMapping("/clear")
    @ResponseBody
    public boolean clear() {
        boolean flag = iDocRecycleService.clear();
        return flag;
    }
    /**
     * 还原回收站
     *
     * @return boolean
     * @author: ChenXin
     */
    @PostMapping("/restore")
    @ResponseBody
    public JSON restore() {
        String userId = ShiroKit.getUser().getId();
        List<String> listGroup = docGroupService.getPremission(userId);
        List<String> roleList = ShiroKit.getUser().getRolesList();
        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        JSONObject json = new JSONObject();
        String fileId = super.getPara("fileId");
        if (ToolUtil.isEmpty(fileId)) {
            fileId = "";
        }
        String folderId = super.getPara("folderId");
        String fileName = super.getPara("fileName");
        ShiroUser shiroUser= UserInfoUtil.getCurrentUser();
      /*  if (adminFlag != 1) {
            int isEdits = docFoldAuthorityService.findEdit(folderId, listGroup, userId);
            if (isEdits == 0) {
                json.put("result", "3");
                return json;
            }
        }*/
        DocInfo docInfo = docInfoService.getDocDetail(fileId);
        //判断是否为根目录（不能在根目录上传文件）
        boolean isRoot = (folderId.equals(fsFileService.getRoot().get(0).getFolderId()));
        if (!isRoot) {
            String fileNameStr = fileName + docInfo.getDocType();
            if (iDocRecycleService.checkDocExist(folderId, fileNameStr)) {
                json.put("result", "0");
            } else {
                if (docInfo != null) {
                    if (adminFlag != 1) {
                        double lackShare = empStatisticsService.getStatisticsDataByUserId(userId, docInfo.getFileSize());
                        if (lackShare < 0) {
                            json.put("result", "5");
                            return json;
                        }
                    }
                }


                if (iDocRecycleService.restore(fileId, folderId)) {
                    json.put("result", "1");
                } else {
                    json.put("result", "2");
                }
            }
        } else {
            json.put("result", "4");
        }

        return json;
    }

    /**
     * 还原回收站
     *
     * @return boolean
     * @author: ChenXin
     */
    @PostMapping("/restoreOldFolder")
    @ResponseBody
    public JSON restoreOldFolder() {
        String userId = ShiroKit.getUser().getId();
        List<String> listGroup = docGroupService.getPremission(userId);
        List<String> roleList = ShiroKit.getUser().getRolesList();
        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        JSONObject json = new JSONObject();
        String fileId = super.getPara("fileId");
        if (ToolUtil.isEmpty(fileId)) {
            fileId = "";
        }
        DocInfo docInfoOld =docInfoService.getById(fileId);
        String folderId = docInfoOld.getFoldId();
        FsFolder fsFolder = fsFolderService.getById(folderId);
        if(fsFolder == null){
            json.put("result", "6");
            return json;
        }
        String fileName = docInfoOld.getTitle();
        ShiroUser shiroUser= UserInfoUtil.getCurrentUser();
        if (adminFlag != 1) {
            int isEdits = docFoldAuthorityService.findEdit(folderId, listGroup, userId);
            if (isEdits == 0) {
                json.put("result", "3");
                return json;
            }
        }
        DocInfo docInfo = docInfoService.getDocDetail(fileId);
        //判断是否为根目录（不能在根目录上传文件）
        boolean isRoot = (folderId.equals(fsFileService.getRoot().get(0).getFolderId()));
        if (!isRoot) {
            String fileNameStr = fileName + docInfo.getDocType();
            if (iDocRecycleService.checkDocExist(folderId, fileNameStr)) {
                json.put("result", "0");
            } else {
                if (docInfo != null) {
                    if (adminFlag != 1) {
                        double lackShare = empStatisticsService.getStatisticsDataByUserId(userId, docInfo.getFileSize());
                        if (lackShare < 0) {
                            json.put("result", "5");
                            return json;
                        }
                    }
                }


                if (iDocRecycleService.restore(fileId, folderId)) {
                    json.put("result", "1");
                } else {
                    json.put("result", "2");
                }
            }
        } else {
            json.put("result", "4");
        }

        return json;
    }
}
