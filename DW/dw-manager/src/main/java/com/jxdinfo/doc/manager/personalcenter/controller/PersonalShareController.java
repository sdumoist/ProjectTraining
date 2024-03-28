package com.jxdinfo.doc.manager.personalcenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.docmanager.service.FsFileService;
import com.jxdinfo.doc.manager.docrecycle.service.IDocRecycleService;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.foldermanager.service.IDocFoldAuthorityService;
import com.jxdinfo.doc.manager.groupmanager.service.DocGroupService;
import com.jxdinfo.doc.manager.sharemanager.service.IPersonalShareService;
import com.jxdinfo.doc.manager.statistics.service.DeptStatisticsService;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: PersonalShareController
 * @Description:TODO
 * @Author: lishilin
 * @Date: 2019/10/30
 * @Version: 1.0
 */
@Controller
@RequestMapping("/personalShare")
public class PersonalShareController {
    @Autowired
    private FsFileService fsFileService;
    @Autowired
    private DocInfoService docInfoService;

    @Autowired
    private DocGroupService docGroupService;

    @Resource
    private CacheToolService cacheToolService;

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

    /**
     * 分享记录服务类
     */
    @Autowired
    private IPersonalShareService iPersonalShareService;
    @Autowired
    private IDocFoldAuthorityService docFoldAuthorityService;
    /**
     * 部门文件大小 Service接口
     */
    @Autowired
    private DeptStatisticsService deptStatisticsService;
    @PostMapping("/list")
    @ResponseBody
    public Map list(String name, @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber, @RequestParam(defaultValue = "60") int pageSize, String order){
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
        fsFolderParams.setRoleList(ShiroKit.getUser().getRolesList());
        String orgId = docFoldAuthorityService.getDeptIds(ShiroKit.getUser().getDeptId());
        String levelCode = businessService.getLevelCodeByUserUpload(fsFolderParams);
        Map<String,Object> list = iPersonalShareService.getMyShareHistory(userId,name,order,beginIndex,pageSize*pageNumber,null,levelCode,orgId,roleList);
        int count = (Integer)list.get("count");
        Map histories = new HashMap();
        histories.put("msg","success");
        histories.put("code",0);
        histories.put("adminFlag",adminFlag);
        histories.put("rows",list);
        histories.put("count",count);
        return histories;
    }
    @PostMapping("/cancelShare")
    @ResponseBody
    public Map cancelShare(String docId){
        Map <String,Object> map = new HashMap<>();
        QueryWrapper wrapper = new QueryWrapper();
        boolean  isSuccess = iPersonalShareService.removeById(docId);
        map.put("isSuccess",isSuccess);
        return  map;
    }
    @PostMapping("/getServerAddress")
    @ResponseBody
    public String getServerAddress(HttpServletRequest request){
        String mappingUrl = "";
        //读取缓存的服务器地址
        Map serverAddress = cacheToolService.getServerAddress();
        if (serverAddress == null ||serverAddress.get("addressValid") == null || "0".equals(serverAddress.get("addressValid").toString())){
            mappingUrl = "http://" + request.getLocalAddr() +  ":" + request.getLocalPort() + "/s/";
        } else {
            mappingUrl = "http://" + serverAddress.get("address").toString() + "/s/";
        }
        return  mappingUrl;
    }
}
