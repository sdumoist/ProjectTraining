package com.jxdinfo.doc.front.personalmanager.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.common.util.UserInfoUtil;
import com.jxdinfo.doc.common.util.XSSUtil;
import com.jxdinfo.doc.front.personalmanager.model.DocVersion;
import com.jxdinfo.doc.front.personalmanager.service.DocVersionService;
import com.jxdinfo.doc.manager.docintegral.service.IntegralRecordService;
import com.jxdinfo.doc.manager.docmanager.service.FsFileService;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.foldermanager.service.IDocFoldAuthorityService;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import com.jxdinfo.doc.manager.groupmanager.service.DocGroupService;
import com.jxdinfo.doc.manager.personalcenter.service.PersonalOperateService;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类的用途：跳转个人中心<p>
 * 创建日期：2018年12月6日 <br>
 * 作者：yjs <br>
 */
@Controller
@RequestMapping("/personalcenter")
public class PersonalCenterController {


    @Autowired
    private IntegralRecordService integralRecordService;

    /*文件服务*/
    @Autowired
    private IFsFolderService fsFolderService;

    /**
     * 目录服务类
     */
    @Resource
    private BusinessService businessService;

    @Resource
    private PersonalOperateService operateService;

    @Autowired
    private DocGroupService docGroupService;

    @Autowired
    private FsFileService fsFileService;
    @Autowired
    private IDocFoldAuthorityService docFoldAuthorityService;
    /**
     * 版本管理 服务
     */
    @Autowired
    private DocVersionService docVersionService;
    @Value("${isProject.using}")
    private boolean projectFalg;

    @Value("${fileAudit.using}")
    private String using;

   /* @Value("${fileAudit.workflowUsing}")
    private boolean workflowUsing;

    @Value("${fileAudit.auditRole}")
    private String auditRole;*/

    @Value("${personalGroup.maintain:false}")
    private boolean personalGroupMaintainFlag;

    @Value("${entryAudit}")
    private String entryAudit;

    /**
     * 跳转个人中心
     *
     * @param model model类
     * @return string 返回路径
     */
    @GetMapping("")
    public String index(Model model, @RequestParam(defaultValue = "false") boolean toUpload,String menu,String folderId,String folderName) {
        menu = XSSUtil.xss(menu);
        folderId = XSSUtil.xss(folderId);
        folderName = XSSUtil.xss(folderName);

        String userId = UserInfoUtil.getUserInfo().get("ID").toString();
        String userName = ShiroKit.getUser().getName();
        model.addAttribute("userName", userName);
        Integer totalIntegral =integralRecordService.showIntegral(userId);
        List<String> roleList = ShiroKit.getUser().getRolesList();
        boolean hasMiddleGroundAuthority = false;
        // TODO 查询是否具有中台咨询委员会的角色
        Integer ztFlag = CommonUtil.getZTFlag(roleList);
        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        hasMiddleGroundAuthority = ztFlag == 7;
        if(userId.equals("wkadmin")||userId.equals("superadmin")){
            model.addAttribute("adminFlag",1);
        }else{
            model.addAttribute("adminFlag",2);
        }
        model.addAttribute("totalIntegral", totalIntegral);
        int uploadNum = fsFolderService.getPersonUploadNum(userId,null);
        model.addAttribute("uploadNum", uploadNum);
        int downloadNum = operateService.getMyHistoryCount(userId, "4", "");
        model.addAttribute("downloadNum",downloadNum);
        model.addAttribute("toUpload",toUpload);
        model.addAttribute("menu",menu);
        model.addAttribute("folderId",folderId);
        System.out.println(folderName);
        try {
        if(folderName!=null){
            model.addAttribute("folderName", URLEncoder.encode(folderName,"utf-8"));
        }else {
            model.addAttribute("folderName",folderName);
        }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        // 是否有词条审核角色
        Boolean entryAuditor = false;
        if (CommonUtil.getAdminFlag(roleList) == 1) {
            entryAuditor = true;
        } else if (roleList != null && roleList.size() > 0) {
            if (roleList.contains(entryAudit)) {
                entryAuditor = true;
            }
        }

        model.addAttribute("entryAudit",entryAuditor);
        model.addAttribute("isPersonCenter",true);
        model.addAttribute("projectFlag",projectFalg);
        model.addAttribute("hasMiddleGroundAuthority", hasMiddleGroundAuthority);
        model.addAttribute("auditUsing",using);
        model.addAttribute("personalGroupMaintainFlag",personalGroupMaintainFlag);
        return "/doc/front/personalcenter/member_center.html";
    }

    @PostMapping(value = "/getInfo")
    @ResponseBody
    public Object getInfo(@RequestParam String ids) {
        String[] strArr = ids.split(",");
        List<String> idList = new ArrayList<String>();
        for (String element : strArr) {
            idList.add(element);
        }
        String userId = UserInfoUtil.getCurrentUser().getId();
        Map<String, Object> result = new HashMap<>(5);
        List<String> listGroup = docGroupService.getPremission(userId);
        FsFolderParams fsFolderParams = new FsFolderParams();
        fsFolderParams.setGroupList(listGroup);
        fsFolderParams.setUserId(userId);
        fsFolderParams.setType("2");
//        fsFolderParams.setLevelCodeString(folder.getLevelCode());
//        fsFolderParams.setId(id);
        String levelCode = businessService.getLevelCodeByUserUpload(fsFolderParams);
        String orgId = docFoldAuthorityService.getDeptIds(ShiroKit.getUser().getDeptId());
        List<Map> list = fsFileService.getInfo(idList,userId,listGroup,levelCode,orgId,ShiroKit.getUser().getRolesList());
        List<String> roleList = ShiroKit.getUser().getRolesList();
        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        int fileState = 0;
        if(list!=null&&list.size()!=0){
            Integer validFlag = Integer.parseInt (list.get(0).get("validFlag").toString());
            if(validFlag == 0){
                if (docVersionService.count(new QueryWrapper<DocVersion>()
                        .eq("doc_id",list.get(0).get("fileId").toString())) == 0){
                    result.put("result","1");
                }else {
                    result.put("result","5");// 已被覆盖的文档版本
                }
            }else{
                if(adminFlag==1){
                    result.put("result","4");
                    return result;
                }
                if(list.get(0).get("authority")==null){
                    result.put("result","2");
                }else{
                    Integer power =Integer.parseInt (list.get(0).get("authority").toString()) ;
                    if(power<1){
                        result.put("result","3");
                    }
                }
            }

        }
        return result;
    }
}
