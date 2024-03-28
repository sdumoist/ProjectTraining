package com.jxdinfo.doc.manager.personalcenter.controller;

import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.foldermanager.service.IDocFoldAuthorityService;
import com.jxdinfo.doc.manager.groupmanager.service.DocGroupService;
import com.jxdinfo.doc.manager.personalcenter.service.PersonalOperateService;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/personalPreview")
public class PersonalPreviewController {
    @Resource
    private PersonalOperateService operateService;

    @Autowired
    private DocGroupService docGroupService;

    @Autowired
    private IDocFoldAuthorityService docFoldAuthorityService;
    /**
     * 目录服务类
     */
    @Resource
    private BusinessService businessService;

    /**
     * @author luzhanzhao
     * @date 2018-11-16
     * @description 返回预览记录列表
     * @param name 关键字
     * @param pageNumber 当前页数
     * @param pageSize 每页长度
     * @return
     */
    @PostMapping("/list")
    @ResponseBody
    public Map list(String name,String[] typeArr,@RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber, @RequestParam(defaultValue = "20") int pageSize,String order){
        int beginIndex = pageNumber * pageSize - pageSize;
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
        String levelCode = businessService.getLevelCodeByUserUpload(fsFolderParams);
        String orgId =docFoldAuthorityService.getDeptIds( ShiroKit.getUser().getDeptId());
        List<Map> list = operateService.getMyHistory(userId, "3", beginIndex, pageSize,name,typeArr,order,levelCode,orgId);
        int count = operateService.getMyHistoryCount(userId, "3",name);
        Map histories = new HashMap();
        histories.put("msg","success");
        histories.put("code",0);
        histories.put("adminFlag",adminFlag);
        histories.put("rows",list);
        histories.put("count",count);
        return histories;
    }

    /**
     * @author luzhanzhao
     * @date 2018-11-16
     * @description 返回预览记录页面
     * @return 预览记录页面地址
     */
    @GetMapping("/view")
    public String PreviewList(){
        return "/doc/manager/personalcenter/Preview-list.html";
    }

}
