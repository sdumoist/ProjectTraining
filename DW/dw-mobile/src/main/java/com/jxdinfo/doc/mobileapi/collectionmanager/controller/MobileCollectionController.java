package com.jxdinfo.doc.mobileapi.collectionmanager.controller;

import com.alibaba.fastjson.JSONObject;
import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.common.jwt.util.JWTUtil;
import com.jxdinfo.doc.common.jwt.util.RemoteIpMobileUtil;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.manager.collectionmanager.dao.PersonalCollectionMapper;
import com.jxdinfo.doc.manager.collectionmanager.model.DocCollection;
import com.jxdinfo.doc.manager.collectionmanager.service.PersonalCollectionService;
import com.jxdinfo.doc.manager.docintegral.service.IntegralRecordService;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.model.DocResourceLog;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.folderextranetauth.service.IFolderExtranetAuthService;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.groupmanager.service.DocGroupService;
import com.jxdinfo.doc.manager.personalcenter.service.PersonalOperateService;
import com.jxdinfo.doc.mobileapi.foldermanager.service.IMobileFsFolderService;
import com.jxdinfo.hussar.bsp.organ.dao.SysStruMapper;
import com.jxdinfo.hussar.bsp.organ.model.SysStru;
import com.jxdinfo.hussar.bsp.permit.service.ISysUserRoleService;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
import com.jxdinfo.hussar.config.front.common.response.ApiResponse;
import com.jxdinfo.hussar.core.base.controller.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.*;

/**
 * 个人收藏
 * @author yjs
 * @date 2018-11-13
 */
@CrossOrigin
@Controller
@RequestMapping("/mobile/collection")
public class MobileCollectionController extends BaseController {

    @Resource
    private JWTUtil jwtUtil;
    /**
     * 个人操作服务
     */
    @Resource
    private PersonalOperateService operateService;

    @Resource
    private SysStruMapper sysStruMapper;

    @Autowired
    private ISysUsersService iSysUsersService;

    @Autowired
    private ISysUserRoleService sysUserRoleService;
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

    @Autowired
    private IMobileFsFolderService fsMobileFolderService;

    @Autowired
    private IFolderExtranetAuthService iFolderExtranetAuthService;

    @Value("${openExtranetLimit}")
    private String openExtranetLimit;

    @RequestMapping("/list")
    @ResponseBody
    public ApiResponse list(String name, String[] typeArr, @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
                            @RequestParam(defaultValue = "60") int pageSize, String order, String parentFolderId, HttpServletRequest request){
        int beginIndex = pageNumber * pageSize - pageSize;
        String userId = jwtUtil.getSysUsers().getUserId();
        List<String> roleList = sysUserRoleService.getRolesByUserId(userId);
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        //创建我的收藏夹及默认收藏夹
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
        if (personalCollectionMapper.selectByResourceId("abcde4a392934742915f89a5869abcde",userId,null).size()==0){
            DocCollection docCollection = new DocCollection();
            String collectionId = UUID.randomUUID().toString().replaceAll("-", "");
            Date date = new Date();
            Timestamp ts = new Timestamp(date.getTime());
            docCollection.setCollectionId(collectionId);
            docCollection.setResourceId("abcde4a392934742915f89a5869abcde");
            docCollection.setParentFolderId("abcde4a392934742915f89a586989292");
            docCollection.setResourceType("1");
            docCollection.setCreateTime(ts);
            docCollection.setCreateUserId(userId);
            docCollection.setLevelCode("001001");
            docCollection.setResourceName("默认收藏夹");
            personalCollectionMapper.insertCollectionFolder(docCollection);
        }
        FsFolderParams fsFolderParams = new FsFolderParams();
        List<String> listGroup = docGroupService.getPremission(userId);
        fsFolderParams.setUserId(userId);
        fsFolderParams.setGroupList(listGroup);
        //获得目录管理权限层级码
        fsFolderParams.setType("2");
        String levelCode = businessService.getLevelCodeByUserUploadMobile(userId,fsFolderParams);
        //获取我的操作记录，5对应收藏
        String deptId = iSysUsersService.getById(userId).getDepartmentId();
        String orgId = "";
        SysStru stru = sysStruMapper.selectById(deptId);
        if(stru!=null){
            orgId = stru.getOrganAlias();
        }
        Map histories = new HashMap();
        List<String> folderExtranetIds = null;
        // 开启了外网访问限制
        if (adminFlag != 1 && StringUtils.equals(openExtranetLimit, "true")) {
            // 用户是从外网访问的系统
            if (adminFlag!=1 && RemoteIpMobileUtil.isExtranetVisit(request)) {
                folderExtranetIds = iFolderExtranetAuthService.getFolderExtranetListMobile();
                if (folderExtranetIds == null || folderExtranetIds.size() == 0) { // 没有外网可以访问的目录
                    System.out.println("===================没有配置外网可以访问的目录=====================");
                    histories.put("msg","success");
                    histories.put("code",0);
                    histories.put("rows", null);
                    histories.put("adminFlag",adminFlag);
                    return ApiResponse.data(200,histories,"");
                }

            }
        }
        //获取当前用户收藏记录的总条数
        if (parentFolderId == null || "".equals(parentFolderId)) {
            parentFolderId = "abcde4a392934742915f89a586989292";
        }
        List<Map> list = personalCollectionService.getCollectionListMobile(userId, beginIndex, pageSize, name,typeArr,order,levelCode,orgId,parentFolderId,folderExtranetIds);
        int count = personalCollectionService.getMyCollectionCountMobile(userId, parentFolderId, name,folderExtranetIds);
        //封装传到前台的信息

        histories.put("msg","success");
        histories.put("code",0);
        histories.put("adminFlag",adminFlag);
        histories.put("rows",list);
        histories.put("count",count);
        return ApiResponse.data(200,histories,"");
    }

    /**
     * @author yjs
     * @date 2018-11-19
     * @description 取消收藏的方法
     * @param docIds 需要取消收藏对应的文档id
     * @return 删除信息
     */
    @RequestMapping("/cancelCollection")
    @ResponseBody
    public ApiResponse cancelCollection(String docIds,String parentFolderId){
        Map<String, Object> result = new HashMap<>(5);
        try {
            //获取当前登录用户
            String userId = jwtUtil.getSysUsers().getUserId();
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
        return ApiResponse.data(200,result,"");
    }

    @RequestMapping(value = "/updateFolderName")
    @ResponseBody
    public int updateFolderName(String collectionId, String folderName,String synopsis) {
        return personalCollectionService.updateFolderName(collectionId,folderName,synopsis);
    }

    /**
     * @author yjs
     * @date 2018-11-19
     * @description 批量删除收藏记录
     * @param ids
     * @return 删除状态
     */
    @RequestMapping("deleteCollection")
    @ResponseBody
    public ApiResponse deleteCollection(String[] ids,String fileType){
        String userId =jwtUtil.getSysUsers().getUserId();
        int num = personalCollectionService.deleteCollection(ids,fileType,userId);
        return ApiResponse.data(200,num,"");
    }

    @RequestMapping("/collectionToFolderList")
    @ResponseBody
    public ApiResponse collectionToFolderList(String parentFolderId,String docId){
        String userId = jwtUtil.getSysUsers().getUserId();
        List<String> roleList = sysUserRoleService.getRolesByUserId(userId);
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        //创建我的收藏夹及默认收藏夹
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
        if (personalCollectionMapper.selectByResourceId("abcde4a392934742915f89a5869abcde",userId,null).size()==0){
            DocCollection docCollection = new DocCollection();
            String collectionId = UUID.randomUUID().toString().replaceAll("-", "");
            Date date = new Date();
            Timestamp ts = new Timestamp(date.getTime());
            docCollection.setCollectionId(collectionId);
            docCollection.setResourceId("abcde4a392934742915f89a5869abcde");
            docCollection.setParentFolderId("abcde4a392934742915f89a586989292");
            docCollection.setResourceType("1");
            docCollection.setCreateTime(ts);
            docCollection.setCreateUserId(userId);
            docCollection.setLevelCode("001001");
            docCollection.setResourceName("默认收藏夹");
            personalCollectionMapper.insertCollectionFolder(docCollection);
        }
        FsFolderParams fsFolderParams = new FsFolderParams();
        List<String> listGroup = docGroupService.getPremission(userId);
        fsFolderParams.setUserId(userId);
        fsFolderParams.setGroupList(listGroup);
        //获得目录管理权限层级码
        fsFolderParams.setType("2");
        String levelCode = businessService.getLevelCodeByUserUploadMobile(userId,fsFolderParams);
        //获取当前用户收藏记录的总条数
        if (parentFolderId == null || "".equals(parentFolderId)||"undefined".equals(parentFolderId)) {
            parentFolderId = "abcde4a392934742915f89a586989292";
        }
        List<Map> list = personalCollectionService.getCollectionToFolderList(userId,levelCode,parentFolderId);
        for (Map m:list){
            m.put("isCollection",false);
            m.put("count",personalCollectionService.getChildFileCount(userId,m.get("resourceId").toString()));
        }
        //封装传到前台的信息
        Map histories = new HashMap();
        histories.put("msg","success");
        histories.put("code",0);
        histories.put("adminFlag",adminFlag);
        histories.put("rows",list);
        histories.put("parentFolderId",parentFolderId);
        return ApiResponse.data(200,histories,"");
    }

    @RequestMapping(value = "/addCheck")
    @ResponseBody
    public String addCheck(String name, String parentFolderId,String folderId) {
        String userId = jwtUtil.getSysUsers().getUserId();
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

    @RequestMapping(value = "/add")
    @ResponseBody
    public ApiResponse add(DocCollection docCollection) {

        String userId = jwtUtil.getSysUsers().getUserId();
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
        //生成levelCode
        DocCollection parentDocCollection = new DocCollection();
        parentDocCollection = personalCollectionService.selectByResourceId(docCollection.getParentFolderId(),userId,null).get(0);
        String parentCode = parentDocCollection.getLevelCode();
        String currentCode = personalCollectionService.getCurrentLevelCode(parentCode, parentDocCollection.getResourceId());
        newDocCollection.setLevelCode(currentCode);
        //保存目录信息
        personalCollectionService.add(newDocCollection);
        return ApiResponse.data(200,SUCCESS_TIP,"");
    }

    @RequestMapping(value = "/collectionToFolder")
    @ResponseBody
    public ApiResponse collectionToFolder(String ids, String parentFolderId) {
        JSONObject json = new JSONObject();
        String userId = jwtUtil.getSysUsers().getUserId();
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
            return ApiResponse.data(200,json,"");
        }
        addCollection(ids,parentFolderId);
        json.put("result", "1");
        return ApiResponse.data(200,json,"");
    }

    /**
     * @author yjs
     * @date 2018-11-19
     * @param docIds 批量收藏的文档id
     * @return 是否收藏成功
     */
    @RequestMapping("addCollection")
    @ResponseBody
    public ApiResponse addCollection(String docIds,String parentFolderId){
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
            String userId = jwtUtil.getSysUsers().getUserId();
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
        return ApiResponse.data(200,result,"");
    }
}
