package com.jxdinfo.doc.front.personalmanager.controller;

import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.common.util.UserInfoUtil;
import com.jxdinfo.doc.common.util.XSSUtil;
import com.jxdinfo.doc.front.groupmanager.service.FrontDocGroupService;
import com.jxdinfo.doc.front.personalmanager.service.FrontUploadService;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.foldermanager.service.IDocFoldAuthorityService;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import com.jxdinfo.doc.manager.groupmanager.service.DocGroupService;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类的用途：跳转前台我的上传<p>
 * 创建日期：2018年12月6日 <br>
 * 作者：yjs <br>
 */
@Controller
@RequestMapping("/frontUpload")
public class FrontUploadController {

    @Autowired
    private DocGroupService docGroupService;

    @Resource
    private IFsFolderService fsFolderService;
    @Autowired
    private IDocFoldAuthorityService docFoldAuthorityService;


    /** 目录管理工具类 */
    @Resource
    private BusinessService businessService;


    /** 目录管理工具类 */
    @Resource
    private FrontUploadService frontUploadService;


    /** 文档群组服务类 */
    @Resource
    private FrontDocGroupService frontDocGroupService;

    @Value("${onlineEdit.using}")
    private boolean onlineEditUsing;

    @Value("${fileAudit.using}")
    private String using;

    @Value("${fileAudit.workflowUsing}")
    private boolean workflowUsing;

    @Value("${fileAudit.auditType}")
    private String auditType;

    @Value("${fileAudit.auditorRange}")
    private String auditorRange;

    @Value("${extendedFunctions.addAuth}")
    private String addAuth;

    @Value("${extendedFunctions.copyFold}")
    private String copyFold;

    @Value("${sameName.newVersion:false}")
    private boolean sameNameNewVersion;

    /**
     * 语义分析：生成标签-是否开启
     */
    @Value("${semanticAnalysis.analysisUsing}")
    private String analysisUsing;

    @GetMapping("/list")
    public String index(Model model) {
        String userId = UserInfoUtil.getUserInfo().get("ID").toString();
        String userName = ShiroKit.getUser().getName();
        model.addAttribute("userName", userName);
        model.addAttribute("auditUsing", using);
        model.addAttribute("onlineEditUsing", onlineEditUsing);

        return "/doc/front/personalcenter/upload.html";
    }


    @GetMapping("/upload")
    public String upload(Model model,String openFileId,String folderName,String returnDocId) {
        openFileId = XSSUtil.xss(openFileId);
        folderName = XSSUtil.xss(folderName);
        if(folderName!=null){
        folderName=   folderName.replaceAll("alert","");
        }
        returnDocId = XSSUtil.xss(returnDocId);
        try {
            String filePath = null;
            if (folderName != null) {
                filePath = URLDecoder.decode(folderName, "UTF-8");
            } else {
                filePath = folderName;
            }
            String userId = UserInfoUtil.getUserInfo().get("ID").toString();
            String userName = ShiroKit.getUser().getName();
            model.addAttribute("userName", userName);
            model.addAttribute("openFileId", openFileId);
            model.addAttribute("folderName", filePath);
            model.addAttribute("returnDocId",(returnDocId == null ? "" : returnDocId));
            List<String> roleList = ShiroKit.getUser().getRolesList();
            Integer adminFlag = CommonUtil.getAdminFlag(roleList);

                model.addAttribute("onlineEditUsing",onlineEditUsing);

            // 目录审核信息能否修改
            boolean auditUsing = true;
            if("false".equals(using)){
                auditUsing = false;
            } else {
                if("false".equals(workflowUsing)){
                    if("1".equals(auditType) && ("1".equals(auditorRange) || "2".equals(auditorRange))){
                        auditUsing = false;
                    }
                }
            }
            model.addAttribute("auditUsing",auditUsing);
            model.addAttribute("workflowUsing",workflowUsing);
            model.addAttribute("addAuth", addAuth);
            model.addAttribute("copyFold", copyFold);
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "/doc/front/personalcenter/uploadList.html";
    }

    @GetMapping("/uploadFile")
    public String uploadFile(Model model,String openFileId,String path) {
        openFileId = XSSUtil.xss(openFileId);
      /*  path = XSSUtil.xss(path);*/
        if(path!=null) {
            path = path.replaceAll("alert", "");
        }
        try {
            String filePath = null;
            if( path != null ){
                filePath = URLDecoder.decode(path, "UTF-8");
            }else{
                filePath = path;
            }
            String userId = UserInfoUtil.getUserInfo().get("ID").toString();
            String userName = ShiroKit.getUser().getName();
            List<String> roleList = ShiroKit.getUser().getRolesList();
            Integer adminFlag = CommonUtil.getAdminFlag(roleList);
            model.addAttribute("adminFlag", adminFlag);
            model.addAttribute("userName", userName);
            model.addAttribute("userId", userId);
            model.addAttribute("openFileId",openFileId);
            model.addAttribute("path",filePath);
            model.addAttribute("analysisUsing",analysisUsing);
            model.addAttribute("sameNameNewVersion",sameNameNewVersion);
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "/doc/front/personalcenter/uploadFile.html";
    }

    @PostMapping("/folderList")
     @ResponseBody
    public Object folderList(String id, String type) {
        String userId = UserInfoUtil.getUserInfo().get("ID").toString();
        String userName = ShiroKit.getUser().getName();
        FsFolderParams fsFolderParams = new FsFolderParams();
        List resultList = new ArrayList();
        FsFolder folder;
        if ("#".equals(id)) {
            String fid="2bb61cdb2b3c11e8aacf429ff4208431";
            folder=fsFolderService.getById(fid);
        }else{
            folder=fsFolderService.getById(id);
        }
        List<String> roleList = ShiroKit.getUser().getRolesList();
        //所属群组id

        List<String> listGroup = frontDocGroupService.getPremission(userId);
        //超级管理员：1 文库管理员：2
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        fsFolderParams.setAdminFlag(adminFlag);
        fsFolderParams.setGroupList(listGroup);
        fsFolderParams.setUserId(userId);
        fsFolderParams.setType(type);
        fsFolderParams.setRoleList(ShiroKit.getUser().getRolesList());
        fsFolderParams.setLevelCodeString(folder.getLevelCode());
        String levelCodeString = businessService.getUploadLevelCodeFront(fsFolderParams);
        fsFolderParams.setLevelCodeString(levelCodeString);
        List<FsFolder> list=new ArrayList<>();
        if ("#".equals(id)) {
            //首次访问
            String idParam = "root";
            //获取根节点
            fsFolderParams.setId(idParam);
            fsFolderParams.setType("0");
            fsFolderParams.setRoleList(ShiroKit.getUser().getRolesList());
          list = frontUploadService.getTreeDataLazy(fsFolderParams);
            List<String> firstList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                FsFolder fsFolder = list.get(i);
                firstList.add(fsFolder.getFolderId());
            }
            //获取第一级
            List<FsFolder> childList =frontUploadService.getChildList(firstList, listGroup, userId, adminFlag, type,levelCodeString);
            List<String> secondList = new ArrayList<>();
            //将文件id拼接
            for (int i = 0; i < childList.size(); i++) {
                FsFolder fsFolder = childList.get(i);
                secondList.add(fsFolder.getFolderId());
            }
            //获取第一级是否有下级
            List<Map> childCountList = frontUploadService.getChildCountList(secondList, listGroup, userId, adminFlag, type,levelCodeString);
            List<Map> childResultList = frontUploadService.checkChildCount(childList,childCountList);

            for (int i = 0; i < list.size(); i++) {
                Map parentMap = new HashMap();
                FsFolder fsFolder = list.get(i);
                parentMap.put("id", fsFolder.getFolderId());
                parentMap.put("text", fsFolder.getFolderName());
                List childMapList = new ArrayList();
                for (int j = 0; j < childResultList.size(); j++) {
                    Map map = childResultList.get(j);
                    if (fsFolder.getFolderId().equals(map.get("pid"))) {
                        childMapList.add(map);
                    }
                }
                parentMap.put("children", childMapList);
                parentMap.put("opened", true);
                resultList.add(parentMap);
            }

        } else {
            fsFolderParams.setId(id);
            fsFolderParams.setType(type);
            fsFolderParams.setRoleList(ShiroKit.getUser().getRolesList());
            //非首次访问， 获取点击节点的下级
            list =frontUploadService.getTreeDataLazy(fsFolderParams);
            List<String> firstList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                FsFolder fsFolder = list.get(i);
                firstList.add(fsFolder.getFolderId());
            }
            //获取是否有下级
            List<Map> childCountList = frontUploadService.getChildCountList(firstList, listGroup, userId, adminFlag, type,levelCodeString);
            resultList =frontUploadService.checkChildCount(list,childCountList);

        }
        return resultList;
    }

    @PostMapping("/changeFolder")
    @ResponseBody
    public Object changeFolder(String id) {
        String userId = ShiroKit.getUser().getId();
        List<String> listGroup = docGroupService.getPremission(userId);
        List<String> roleList = ShiroKit.getUser().getRolesList();
        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        Map<String, Object> result = new HashMap<>(5);
        if (adminFlag != 1) {
            int isEdits = docFoldAuthorityService.findEditByUpload(id, listGroup, userId);
            result.put("noChildPower", isEdits);

        }else{
            result.put("noChildPower",2);
        }

        return  result;
    }

}

