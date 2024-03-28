package com.jxdinfo.doc.client.collectionmanager.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxdinfo.doc.client.response.ApiResponse;
import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.common.jwt.util.JWTUtil;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.front.personalmanager.model.DocVersion;
import com.jxdinfo.doc.front.personalmanager.service.DocVersionService;
import com.jxdinfo.doc.manager.collectionmanager.model.DocCollection;
import com.jxdinfo.doc.manager.collectionmanager.service.PersonalCollectionService;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.model.DocResourceLog;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.docmanager.service.FsFileService;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.foldermanager.service.IDocFoldAuthorityService;
import com.jxdinfo.doc.manager.groupmanager.service.DocGroupService;
import com.jxdinfo.doc.manager.personalcenter.service.PersonalOperateService;
import com.jxdinfo.hussar.bsp.organ.dao.SysStruMapper;
import com.jxdinfo.hussar.bsp.permit.service.ISysUserRoleService;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
import com.jxdinfo.hussar.core.base.controller.BaseController;
import com.jxdinfo.hussar.core.support.HttpKit;
import com.jxdinfo.hussar.core.util.ToolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.*;

/**
 * 个人收藏
 * @author yjs
 * @date 2018-11-13
 */
@Controller
@RequestMapping("/client/collection")
public class ClientCollectionController extends BaseController {
    /**
     * 版本管理 服务
     */
    @Autowired
    private DocVersionService docVersionService;
    /**
     * 我的收藏
     */
    @Autowired
    private PersonalCollectionService personalCollectionService;
    /**
     * 个人操作服务
     */
    @Resource
    private PersonalOperateService operateService;


    @Autowired
    private FsFileService fsFileService;
    /**
     * 文档信息
     */
    @Autowired
    private DocInfoService docInfoService;
    /**
     * 文档信息
     */
    @Autowired
    private IDocFoldAuthorityService docFoldAuthorityService;




    @Autowired
    private DocGroupService docGroupService;

    /**
     * 目录服务类
     */
    @Resource
    private BusinessService businessService;


    @Autowired
    private ISysUserRoleService sysUserRoleService;


    @Resource
    private JWTUtil jwtUtil;

    @Resource
    private SysStruMapper sysStruMapper;


    @Autowired
    private ISysUsersService iSysUsersService;
    @RequestMapping("/list")
    @ResponseBody
    public ApiResponse list(String name, String[] typeArr, @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
                            @RequestParam(defaultValue = "60") int pageSize, String order,String parentFolderId){
        int beginIndex = pageNumber * pageSize - pageSize;
        String userId = jwtUtil.getSysUsers().getUserId();
        List<String> roleList = sysUserRoleService.getRolesByUserId(userId);
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        //获取当前登录人
        FsFolderParams fsFolderParams = new FsFolderParams();
        List<String> listGroup = docGroupService.getPremission(userId);
        String deptId = iSysUsersService.getById(userId).getDepartmentId();
        String orgId = docFoldAuthorityService.getDeptIds(deptId);
        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        fsFolderParams.setUserId(userId);
        fsFolderParams.setGroupList(listGroup);
        fsFolderParams.setRoleList(roleList);
        //获得目录管理权限层级码
        fsFolderParams.setType("2");
        String levelCode = businessService.getLevelCodeByUserUploadClient(fsFolderParams,orgId);
        if (parentFolderId == null || "".equals(parentFolderId)) {
            parentFolderId = "abcde4a392934742915f89a586989292";
        }
        //获取我的操作记录，5对应收藏
        List<Map> list = personalCollectionService.getCollectionList(userId, beginIndex, pageSize, name,typeArr,order,levelCode,orgId,parentFolderId);
        int count = personalCollectionService.getMyCollectionCount(userId, parentFolderId, name);
        //封装传到前台的信息
        Map histories = new HashMap();
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
     * @description 获取个人收藏主页面
     * @return 个人收藏主页面地址
     */
    @RequestMapping("")
    public String collectionList(){
        return "/doc/manager/personalcenter/collection-list.html";
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
    public ApiResponse cancelCollection(String docIds){
        Map<String, Object> result = new HashMap<>(5);
        try {
            //获取当前登录用户
            String userId = jwtUtil.getSysUsers().getUserId();
            //根据文档id、当前登录用户id和操作类型（5对应收藏），进行取消收藏操作
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
            docResourceLog.setAddressIp(HttpKit.getIp());
            docResourceLog.setValidFlag("1");
            resInfoList.add(docResourceLog);
            //将操作记录插入操作记录表中
            docResourceLog.setAddressIp(HttpKit.getIp());
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

    /**
     * @author yjs
     * @date 2018-11-19
     * @param docIds 批量收藏的文档id
     * @return 是否收藏成功
     */
    @RequestMapping("addCollection")
    @ResponseBody
    public Object addCollection(String docIds, String parentFolderId){
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
            docResourceLog.setAddressIp(HttpKit.getIp());
            resInfoList.add(docResourceLog);
            //将操作记录插入操作记录表中
            docInfoService.insertResourceLog(resInfoList);
            if(ToolUtil.isEmpty(parentFolderId)) {
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
    @RequestMapping("deleteCollection")
    @ResponseBody
    public int deleteCollection(String[] ids){
        return operateService.deleteCollection(ids);
    }

    /**
     * 收藏新增目录
     *
     * @param docCollection 目录信息
     * @return
     */
    @PostMapping(value = "/add")
    @ResponseBody
    public Object add(DocCollection docCollection) {
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
    @RequestMapping(value = "/getInfo")
    @ResponseBody
    public ApiResponse getInfo(@RequestParam String ids) {
        String[] strArr = ids.split(",");
        List<String> idList = new ArrayList<String>();
        for (String element : strArr) {
            idList.add(element);
        }
        String userId = jwtUtil.getSysUsers().getUserId();
        List<String> listGroup = docGroupService.getPremission(userId);
        FsFolderParams fsFolderParams = new FsFolderParams();
        fsFolderParams.setGroupList(listGroup);
        fsFolderParams.setUserId(userId);
        List roleList = sysUserRoleService.getRolesByUserId(userId);
        fsFolderParams.setRoleList(roleList);
        fsFolderParams.setType("2");
//        fsFolderParams.setLevelCodeString(folder.getLevelCode());
//        fsFolderParams.setId(id);
        String deptId = docFoldAuthorityService.getDeptIds(iSysUsersService.getById(userId).getDepartmentId());

        String levelCode = businessService.getLevelCodeByUserUploadClient(fsFolderParams,deptId);
        List<Map> list = fsFileService.getInfo(idList,userId,listGroup,levelCode,deptId,roleList);
        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        int fileState = 0;
        String resultStr ="";
        if(list!=null&&list.size()!=0){
            Integer validFlag = Integer.parseInt (list.get(0).get("validFlag").toString());
            if(validFlag == 0){
                if (docVersionService.count(new QueryWrapper<DocVersion>()
                        .eq("doc_id",list.get(0).get("fileId").toString())) == 0){
                    resultStr="1";
                }else {
                    resultStr ="5";
                }
            }else{
                if(adminFlag==1){
                    resultStr ="4";
                    return ApiResponse.data(200,resultStr,"");
                }
                if(list.get(0).get("authority")==null){
                    resultStr="2";
                }else{
                    Integer power =Integer.parseInt (list.get(0).get("authority").toString()) ;
                    if(power<1){
                        resultStr="3";
                    }
                }
            }

        }
        return ApiResponse.data(200,resultStr,"");
    }
    @RequestMapping(value = "/getInfos")
    @ResponseBody
    public ApiResponse getInfos(@RequestParam String ids) {
        String[] strArr = ids.split(",");
        List<String> idList = new ArrayList<String>();
        for (String element : strArr) {
            idList.add(element);
        }
        String userId = jwtUtil.getSysUsers().getUserId();
        List<String> listGroup = docGroupService.getPremission(userId);
        FsFolderParams fsFolderParams = new FsFolderParams();
        fsFolderParams.setGroupList(listGroup);
        fsFolderParams.setUserId(userId);
      List roleList=  sysUserRoleService.getRolesByUserId(userId);
        fsFolderParams.setRoleList(roleList);
        fsFolderParams.setType("2");
//        fsFolderParams.setLevelCodeString(folder.getLevelCode());
//        fsFolderParams.setId(id);
        String deptId = docFoldAuthorityService.getDeptIds(iSysUsersService.getById(userId).getDepartmentId());
        String levelCode = businessService.getLevelCodeByUserUploadClient(fsFolderParams,deptId);
        List<Map> list = fsFileService.getInfo(idList,userId,listGroup,levelCode,deptId,roleList);
        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        int fileState = 0;
        String resultStr ="4";
        if(list!=null&&list.size()!=0){
            for(int i=0;i<list.size();i++) {
                Integer validFlag = Integer.parseInt(list.get(i).get("validFlag").toString());
                if (validFlag == 0) {
                    if (docVersionService.count(new QueryWrapper<DocVersion>()
                            .eq("doc_id", list.get(i).get("fileId").toString())) == 0) {
                        resultStr = "1";
                        return ApiResponse.data(200,resultStr,"");
                    } else {
                        resultStr = "5";
                        return ApiResponse.data(200,resultStr,"");
                    }
                } else {
                    if (adminFlag == 1) {
                        resultStr = "4";
                    }
                    if (list.get(i).get("authority") == null) {
                        resultStr = "2";
                        return ApiResponse.data(200,resultStr,"");
                    } else {
                        Integer power = Integer.parseInt(list.get(i).get("authority").toString());
                        if (power < 1) {
                            resultStr = "3";
                            return ApiResponse.data(200,resultStr,"");
                        }
                    }
                }
            }
        }
        return ApiResponse.data(200,resultStr,"");
    }
}
