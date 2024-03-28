package com.jxdinfo.doc.manager.collectionmanager.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.common.util.UserInfoUtil;
import com.jxdinfo.doc.common.util.XSSUtil;
import com.jxdinfo.doc.manager.collectionmanager.dao.PersonalCollectionMapper;
import com.jxdinfo.doc.manager.collectionmanager.model.DocCollection;
import com.jxdinfo.doc.manager.collectionmanager.service.PersonalCollectionService;
import com.jxdinfo.doc.manager.docintegral.service.IntegralRecordService;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.model.DocResourceLog;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.foldermanager.model.DocFoldAuthority;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.foldermanager.service.IDocFoldAuthorityService;
import com.jxdinfo.doc.manager.groupmanager.service.DocGroupService;
import com.jxdinfo.doc.manager.personalcenter.service.PersonalOperateService;
import com.jxdinfo.hussar.core.base.controller.BaseController;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import com.jxdinfo.hussar.core.support.HttpKit;
import com.jxdinfo.hussar.core.util.ToolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.*;

/**
 * 个人收藏
 * @author yjs
 * @date 2018-11-13
 */
@Controller
@RequestMapping("/personalCollection")
public class PersonalCollectionController extends BaseController {

    /**
     * 个人操作服务
     */
    @Resource
    private PersonalOperateService operateService;


    @Autowired
    private IDocFoldAuthorityService docFoldAuthorityService;
    /**
     * 文档信息
     */
    @Autowired
    private DocInfoService docInfoService;
    /**
     * 我的收藏
     */
    @Autowired
    private PersonalCollectionService personalCollectionService;

    /** 积分系统服务类 */
    @Resource
    private IntegralRecordService integralRecordService;


    @Autowired
    private DocGroupService docGroupService;

    @Resource
    private PersonalCollectionMapper personalCollectionMapper;
    /**
     * 目录服务类
     */
    @Resource
    private BusinessService businessService;

    @PostMapping("/list")
    @ResponseBody
    public Map list(String name, String[] typeArr,@RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
                    @RequestParam(defaultValue = "60") int pageSize,String order,String parentFolderId){
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
        String levelCode = businessService.getLevelCodeByUserUpload(fsFolderParams);
        //获取我的操作记录，5对应收藏
        String orgId = docFoldAuthorityService.getDeptIds(ShiroKit.getUser().getDeptId());

        //获取当前用户收藏记录的总条数
        if (parentFolderId == null || "".equals(parentFolderId)) {
            parentFolderId = "abcde4a392934742915f89a586989292";
        }
        List<Map> list = personalCollectionService.getCollectionList(userId, beginIndex, pageSize, name,typeArr,order,levelCode,orgId,parentFolderId);
        int count = personalCollectionService.getMyCollectionCount(userId, parentFolderId, name);
        //封装传到前台的信息
        Map histories = new HashMap();
        histories.put("msg","success");
        histories.put("code",0);
        histories.put("adminFlag",adminFlag);
        histories.put("rows",list);
        histories.put("count",count);
        return histories;
    }

    /**
     * @author yjs
     * @date 2018-11-19
     * @description 获取个人收藏主页面
     * @return 个人收藏主页面地址
     */
    @GetMapping("")
    public String collectionList(){
        return "/doc/manager/personalcenter/collection-list.html";
    }


    /**
     *
     * @param model
     * @param openFileId
     * @param filePath
     * @return
     */
    @GetMapping("/collectionToFolderView")
    public String collectionToFolderView(Model model, String openFileId, String filePath,String docId) {
        openFileId = XSSUtil.xss(openFileId);
        docId = XSSUtil.xss(docId);
        String userId = UserInfoUtil.getUserInfo().get("ID").toString();
        if (personalCollectionMapper.selectByResourceId("abcde4a392934742915f89a586989292",userId,null).size()==0){
            DocCollection docCollection = new DocCollection();
            String collectionId = UUID.randomUUID().toString().replaceAll("-", "");
            Date date = new Date();
            Timestamp ts = new Timestamp(date.getTime());
            docCollection.setCollectionId(collectionId);
            docCollection.setResourceId("abcde4a392934742915f89a586989292");
            docCollection.setParentFolderId("root");
            docCollection.setResourceType("1");
            docCollection.setCreateTime(ts);
            docCollection.setCreateUserId(userId);
            docCollection.setLevelCode("001");
            docCollection.setResourceName("我的收藏");
            personalCollectionMapper.insertCollectionFolder(docCollection);
        }
        String userName = ShiroKit.getUser().getName();
        model.addAttribute("userName", userName);
        model.addAttribute("openFileId", openFileId);
        model.addAttribute("folderName", filePath);
        model.addAttribute("docId", docId);
        return "/doc/front/personalcenter/collection_list.html";
    }

    @PostMapping("/collectionToFolderList")
    @ResponseBody
    public Map collectionToFolderList(String parentFolderId,String docId){

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
        fsFolderParams.setRoleList(ShiroKit.getUser().getRolesList());
        fsFolderParams.setType("2");
        String levelCode = businessService.getLevelCodeByUserUpload(fsFolderParams);
        //获取我的操作记录，5对应收藏

        //获取当前用户收藏记录的总条数
        if (parentFolderId == null || "".equals(parentFolderId)||"undefined".equals(parentFolderId)) {
            parentFolderId = "abcde4a392934742915f89a586989292";
        }
        List<Map> list = personalCollectionService.getCollectionToFolderList(userId,levelCode,parentFolderId);
        List<DocCollection> docCollections = personalCollectionService.selectByResourceId(docId,userId,null);
        boolean isCollectionToParentFolder = false;
        for (DocCollection d:docCollections){
            if(d.getParentFolderId().equals(parentFolderId)){
                isCollectionToParentFolder = true;
            }
            for (Map m:list){
                if (m.get("resourceId").equals(d.getParentFolderId())){
                    m.put("isCollection",true);
                    break;
                }else {
                    m.put("isCollection",false);
                }
            }
        }
        //封装传到前台的信息
        Map histories = new HashMap();
        histories.put("msg","success");
        histories.put("code",0);
        histories.put("adminFlag",adminFlag);
        histories.put("rows",list);
        histories.put("parentFolderId",parentFolderId);
        histories.put("isCollectionToParentFolder",isCollectionToParentFolder);
        return histories;
    }
    /**
     * @author yjs
     * @date 2018-11-19
     * @description 取消收藏的方法
     * @param docIds 需要取消收藏对应的文档id
     * @return 删除信息
     */
    @PostMapping("cancelCollection")
    @ResponseBody
    public Object cancelCollection(String docIds,String parentFolderId){
        Map<String, Object> result = new HashMap<>(5);
        try {
            //获取当前登录用户
            ShiroUser shiroUser = ShiroKit.getUser();
            String userId = shiroUser.getId();
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            //获取uuid的方法
            String id = UUID.randomUUID().toString().replace("-", "");
            //封装操作记录信息
            List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
            //新建操作记录
            DocResourceLog docResourceLog = new DocResourceLog();
            //封装操作记录信息
            docResourceLog.setId(id);
            docResourceLog.setResourceId(docIds);
            docResourceLog.setOperateTime(ts);
            docResourceLog.setResourceType(0);
            docResourceLog.setUserId(userId);
            docResourceLog.setOperateType(9);
            docResourceLog.setValidFlag("1");
            docResourceLog.setAddressIp(HttpKit.getIp());
            resInfoList.add(docResourceLog);
            //将操作记录插入操作记录表中
            docInfoService.insertResourceLog(resInfoList);
            //根据文档id、当前登录用户id进行取消收藏操作
            personalCollectionService.cancelCollection(docIds,userId,null);
            result.put("success", "0");

        }catch (Exception e){
            e.printStackTrace();
            result.put("success","1");
        }
        return result;
    }

    /**
     * @author yjs
     * @date 2018-11-19
     * @param docIds 批量收藏的文档id
     * @return 是否收藏成功
     */
    @PostMapping("addCollection")
    @ResponseBody
    public Object addCollection(String docIds,String parentFolderId){
        Map<String, Object> result = new HashMap<>(5);
        try {
            List<DocCollection> docCollectionList = new ArrayList<>();
            //获取当前时间戳
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            //新建操作记录
            DocCollection docCollection = new DocCollection();
            //获取uuid的方法
            String id = UUID.randomUUID().toString().replace("-", "");
            List<String> docIdList = Arrays.asList(docIds.split(","));
            //获取文件信息
            List<DocInfo> docInfoList = docInfoService.getDocInfo(docIdList);
            //获取当前登录用户
            ShiroUser shiroUser = ShiroKit.getUser();
            String userId = shiroUser.getId();
            //封装操作记录信息
            List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
            //新建操作记录
            DocResourceLog docResourceLog = new DocResourceLog();
            //获取uuid的方法
            String resourceLogId = UUID.randomUUID().toString().replace("-", "");
            //封装操作记录信息
            docResourceLog.setId(resourceLogId);
            docResourceLog.setResourceId(docIds);
            docResourceLog.setOperateTime(ts);
            docResourceLog.setResourceType(0);
            docResourceLog.setUserId(userId);
            docResourceLog.setOperateType(5);
            docResourceLog.setValidFlag("1");
            docResourceLog.setAddressIp(HttpKit.getIp());
            resInfoList.add(docResourceLog);
            //将操作记录插入操作记录表中
            docInfoService.insertResourceLog(resInfoList);
            if (parentFolderId==null||"".equals(parentFolderId)){
                parentFolderId = "abcde4a392934742915f89a586989292";
            }
            for (DocInfo docInfo: docInfoList) {
                docCollection.setCollectionId(id);
                docCollection.setResourceId(docInfo.getDocId());
                docCollection.setParentFolderId(parentFolderId);
                docCollection.setResourceType("0");
                docCollection.setCreateTime(ts);
                docCollection.setCreateUserId(userId);
                docCollection.setResourceName(docInfo.getTitle());
                docCollectionList.add(docCollection);
            }
            //将数据插入我的收藏表中
            personalCollectionService.insertCollection(docCollectionList);
            result.put("success", "0");
        }catch (Exception e){
            e.printStackTrace();
            result.put("success","1");
        }
        //返回收藏结果
        return result;
    }

    /**
     * @author yjs
     * @date 2018-11-19
     * @description 批量删除收藏记录
     * @param ids
     * @return 删除状态
     */
    @PostMapping("deleteCollection")
    @ResponseBody
    public int deleteCollection(String[] ids,String fileType){
        String userId = ShiroKit.getUser().getId();
        return personalCollectionService.deleteCollection(ids,fileType,userId);
    }

    /**
     * 检测名字是否重复
     * @param name
     * @param parentFolderId
     * @param folderId
     * @return
     */
    @PostMapping(value = "/addCheck")
    @ResponseBody
    public String addCheck(String name, String parentFolderId,String folderId) {
        String userId = ShiroKit.getUser().getId();
        String type;
        if (parentFolderId == null || "".equals(parentFolderId)) {
            parentFolderId = "abcde4a392934742915f89a586989292";
        }
        if(folderId!=null) {
            type = "0";
        }else {
            type = "1";
        }
        String res = "true";
        List<DocCollection> list = personalCollectionService.addCheck(parentFolderId, name,folderId,userId,type);
        if (list.size() > 0) {
            return "false";
        }
        return res;
    }

    @PostMapping(value = "/add")
    @ResponseBody
    public Object add(DocCollection docCollection) {

        String userId = ShiroKit.getUser().getId();
            //新增一下主表
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        if (docCollection.getParentFolderId() == null || "".equals(docCollection.getParentFolderId())) {
            docCollection.setParentFolderId("abcde4a392934742915f89a586989292");
        }
        String collectionId = UUID.randomUUID().toString().replaceAll("-", "");
        String folderId = UUID.randomUUID().toString().replaceAll("-", "");
        DocCollection newDocCollection = new DocCollection();
        newDocCollection.setCollectionId(collectionId);
        newDocCollection.setResourceId(folderId);
        newDocCollection.setParentFolderId(docCollection.getParentFolderId());
        newDocCollection.setResourceType("1");
        newDocCollection.setCreateTime(ts);
        newDocCollection.setCreateUserId(userId);
        newDocCollection.setResourceName(docCollection.getResourceName());
        if (docCollection.getSynopsis()!=null){
            newDocCollection.setSynopsis(docCollection.getSynopsis());
        }
        //生成levelCode
        DocCollection parentDocCollection = new DocCollection();
        parentDocCollection = personalCollectionService.selectByResourceId(docCollection.getParentFolderId(),userId,null).get(0);
        String parentCode = parentDocCollection.getLevelCode();
        String currentCode = personalCollectionService.getCurrentLevelCode(parentCode, parentDocCollection.getResourceId());
        newDocCollection.setLevelCode(currentCode);
        //保存目录信息
        personalCollectionService.add(newDocCollection);
        return SUCCESS_TIP;
    }

    @GetMapping(value = "/getTreeDataLazy")
    @ResponseBody
    public List getTreeDataLazy(String id, String type) {
        return personalCollectionService.getTreeDataLazy(id, type);
    }

    @PostMapping(value = "/checkChild")
    @ResponseBody
    public String checkChild(@RequestParam String fsFolderIds, @RequestParam String id) {
        String res = "success";
        String[] strArr = fsFolderIds.split(",");
        for (String s : strArr) {

            String[] childArr = personalCollectionService.getChildFsFolder(s);
            for (int i = 0; i < childArr.length; i++) {
                if (childArr[i].equals(id)) {
                    res = "have";
                }
            }
        }
        return res;
    }

    @PostMapping(value = "/update")
    @ResponseBody
    public String update(String ids, String parentFolderId, String folderName) {
        String res = "success";
        String[] id = ids.split(",");
        for (int i=0;i<id.length;i++) {
            String userId = ShiroKit.getUser().getId();
            DocCollection newDocCollection = new DocCollection();
            newDocCollection.setParentFolderId(parentFolderId);
            newDocCollection.setResourceType("1");
            //生成levelCode
            DocCollection parentDocCollection = new DocCollection();
            parentDocCollection = personalCollectionService.selectByResourceId(parentFolderId, userId, null).get(0);
            String parentCode = parentDocCollection.getLevelCode();
            String currentCode = personalCollectionService.getCurrentLevelCode(parentCode, parentDocCollection.getResourceId());
            newDocCollection.setLevelCode(currentCode);
            //保存目录信息
            personalCollectionService.updateFolder(id[i], parentFolderId, userId);
            personalCollectionService.addLevel(parentFolderId);
        }
        return res;
    }
    @PostMapping(value = "/move")
    @ResponseBody
    public JSON move(String ids, String parentFolderId) {
        JSONObject json = new JSONObject();
        String userId = ShiroKit.getUser().getId();
        String[] id = ids.split(",");
        List<String> docIdList = Arrays.asList(ids.split(","));
        //获取文件信息
        List<DocInfo> docInfoList = docInfoService.getDocInfo(docIdList);
        for (int i=0;i<id.length;i++) {
            String flag = "true";
            for (DocInfo d : docInfoList) {
                flag = addCheck(d.getTitle(), parentFolderId, id[i]);
            }
            if ("false".equals(flag)) {
                json.put("result", "0");
                return json;
            }
            //保存目录信息
        }
        for (int i=0;i<id.length;i++) {
            personalCollectionService.updateFile(id[i], parentFolderId, userId);
        }
        json.put("result", "1");
        return json;
    }

    @PostMapping(value = "/collectionToFolder")
    @ResponseBody
    public JSON collectionToFolder(String ids, String parentFolderId) {
        JSONObject json = new JSONObject();
        String userId = ShiroKit.getUser().getId();
        List<String> docIdList = Arrays.asList(ids.split(","));
        //获取文件信息
        List<DocInfo> docInfoList = docInfoService.getDocInfo(docIdList);
        List<DocCollection> docCollection = personalCollectionService.selectByResourceId(ids,userId,null);
        String flag = "true";
        for (DocInfo d : docInfoList){
            flag = addCheck(d.getTitle(),parentFolderId,ids);
        }
        if (docCollection.size()!=0||"false".equals(flag)){
            json.put("result", "0");
            return json;
        }
        addCollection(ids,parentFolderId);
        json.put("result", "1");
        return json;
    }
    @PostMapping(value = "/updateFolderName")
    @ResponseBody
    public int updateFolderName(String collectionId, String folderName,String synopsis) {
        return personalCollectionService.updateFolderName(collectionId,folderName,synopsis);
    }
}
