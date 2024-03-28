package com.jxdinfo.doc.mobileapi.docmanager.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxdinfo.doc.common.constant.DocConstant;
import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.docutil.service.DocConfigService;
import com.jxdinfo.doc.common.jwt.util.JWTUtil;
import com.jxdinfo.doc.common.jwt.util.RemoteIpMobileUtil;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.common.util.ESUtil;
import com.jxdinfo.doc.common.util.FileTool;
import com.jxdinfo.doc.common.util.StringUtil;
import com.jxdinfo.doc.front.foldermanager.dao.FrontFolderMapper;
import com.jxdinfo.doc.front.groupmanager.service.FrontDocGroupService;
import com.jxdinfo.doc.front.personalmanager.service.FrontUploadService;
import com.jxdinfo.doc.manager.collectionmanager.service.PersonalCollectionService;
import com.jxdinfo.doc.manager.docconfigure.service.DocConfigureService;
import com.jxdinfo.doc.manager.docmanager.model.*;
import com.jxdinfo.doc.manager.docmanager.service.DocFileAuthorityService;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.docmanager.service.FilesService;
import com.jxdinfo.doc.manager.docmanager.service.FsFileService;
import com.jxdinfo.doc.manager.docrecycle.service.IDocRecycleService;
import com.jxdinfo.doc.manager.doctop.service.DocTopService;
import com.jxdinfo.doc.manager.folderextranetauth.service.IFolderExtranetAuthService;
import com.jxdinfo.doc.manager.foldermanager.dao.FsFolderMapper;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.foldermanager.service.IDocFoldAuthorityService;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import com.jxdinfo.doc.manager.groupmanager.service.DocGroupService;
import com.jxdinfo.doc.manager.personalcenter.service.PersonalOperateService;
import com.jxdinfo.doc.manager.personextranetaccess.service.PersonExtranetAccessService;
import com.jxdinfo.doc.mobileapi.docmanager.service.MobileFilesService;
import com.jxdinfo.doc.mobileapi.foldermanager.service.IMobileFsFolderService;
import com.jxdinfo.hussar.bsp.organ.dao.SysStruMapper;
import com.jxdinfo.hussar.bsp.organ.model.SysStru;
import com.jxdinfo.hussar.bsp.permit.dao.SysUsersMapper;
import com.jxdinfo.hussar.bsp.permit.model.SysUsers;
import com.jxdinfo.hussar.bsp.permit.service.ISysIdtableService;
import com.jxdinfo.hussar.bsp.permit.service.ISysUserRoleService;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
import com.jxdinfo.hussar.config.front.common.response.ApiResponse;
import com.jxdinfo.hussar.core.base.controller.BaseController;
import com.jxdinfo.hussar.core.util.ToolUtil;
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
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.*;

/**
 * 文件系统-资源管理
 *
 * @author smallcat
 * @Date 2018-06-30 14:10:08
 */
@CrossOrigin
@Controller
@RequestMapping("/mobile/file")
public class FileMobileController extends BaseController {

    /**
     * 目录dao层
     */
    @Resource
    private FrontFolderMapper frontFolderMapper;
    @Resource
    private FsFolderMapper fsFolderMapper;
    @Autowired
    private FsFileService fsFileService;

    @Autowired
    private MobileFilesService clientFilesService;

    /** 目录管理工具类 */
    @Resource
    private FrontUploadService frontUploadService;


    /** 文档群组服务类 */
    @Resource
    private FrontDocGroupService frontDocGroupService;
    @Resource
    private PersonalOperateService operateService;
    /**
     * 获取编号公共方法
     */
    @Autowired
    private ISysIdtableService sysIdtableService;
    @Autowired
    private DocTopService docTopService;

    /**
     * 我的收藏
     */
    @Autowired
    private PersonalCollectionService personalCollectionService;
    /**
     * 文件处理
     */
    @Autowired
    private FilesService filesService;

    @Autowired
    private ISysUsersService sysUsersService;

    @Resource
    private JWTUtil jwtUtil;

    @Autowired
    private ESUtil esUtil;
    @Resource
    private SysStruMapper sysStruMapper;

    /**
     * 回收站 服务类
     */
    @Resource
    private IDocRecycleService iDocRecycleService;

    @Resource
    SysUsersMapper sysUsersMapper;

    /**
     * 配置信息服务层
     */
    @Resource
    private DocConfigureService docConfigureService;

    @Autowired
    private DocGroupService docGroupService;

    @Autowired
    private ISysUserRoleService sysUserRoleService;


    @Autowired
    private ISysUsersService iSysUsersService;
    /**
     * 文档信息
     */
    @Autowired
    private DocInfoService docInfoService;

    @Autowired
    private IFsFolderService fsFolderService;

    @Autowired
    private DocFileAuthorityService docFileAuthorityService;

    @Autowired
    private CacheToolService cacheToolService;

    @Autowired
    private DocConfigService docConfigService;

    /**
     * 目录服务类
     */
    @Resource
    private BusinessService businessService;

    @Autowired
    private IDocFoldAuthorityService docFoldAuthorityService;

    @Autowired
    private IFolderExtranetAuthService iFolderExtranetAuthService;

    @Autowired
    private PersonExtranetAccessService personExtranetAccessService;

    @Autowired
    private IMobileFsFolderService fsMobileFolderService;

    /**
     * 是否开启外网限制
     */
    @Value("${openExtranetLimit}")
    private String openExtranetLimit;

    @RequestMapping("/download")
    public void getFile(String docId, @RequestParam(required = false) String mtoken, HttpServletRequest request, HttpServletResponse response) {
        if(mtoken!=null){
            String userId = JWT.decode(mtoken).getClaims().get("userId").asString();
            //通过userId 获取USER
            SysUsers user = sysUsersMapper.selectById(userId);
            jwtUtil.setSysUsers(user);
        }
        try {
            String userId = jwtUtil.getSysUsers().getUserId();
            String orgId = iSysUsersService.getById(userId).getDepartmentId();

            if (docId != null) {
                DocInfo docInfo = docInfoService.getDocDetail(docId);
                if (docInfo != null) {
                    filesService.downloadClient(docId, docInfo.getTitle(), request, response, userId, orgId);

                    //获取附件的地址

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/folderList")
    @ResponseBody
    public Object folderList(String id, String type, HttpServletRequest request) {
        Map<String,Object> folderMap = new HashMap<String,Object>();

        String userId = jwtUtil.getSysUsers().getUserId();
        FsFolderParams fsFolderParams = new FsFolderParams();
        List resultList = new ArrayList();
        FsFolder folder;
        if ("#".equals(id)) {
            String fid="2bb61cdb2b3c11e8aacf429ff4208431";
            folder=fsFolderService.getById(fid);
        }else{
            folder=fsFolderService.getById(id);
        }
        List<String> listGroup = docGroupService.getPremission(userId);
        List<String> roleList = sysUserRoleService.getRolesByUserId(userId);
        //所属群组id

        //超级管理员：1 文库管理员：2
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);

        List<String> folderIds = null;
        Map<String, Object> result = new HashMap<>(5);
        //判断是否有外网权限
        if (StringUtils.equals(openExtranetLimit, "true")) {
            // 用户是从外网访问的系统
            if (adminFlag!=1 && RemoteIpMobileUtil.isExtranetVisit(request)) {
                /*Map<String,Object> userExtranet = fsMobileFolderService.isUserExtranet(userId);
                int code = (Integer)userExtranet.get("code");
                String msg = (String)userExtranet.get("msg");
                if (code == 203){
                    return ApiResponse.data(code, result, msg);
                }*/

                folderIds = iFolderExtranetAuthService.getFolderExtranetListMobile();
                if (folderIds == null || folderIds.size() == 0) { // 没有外网可以访问的目录
                    System.out.println("===================没有配置外网可以访问的目录=====================");
                    result.put("list", null);
                    return ApiResponse.data(200, result, "");
                }

            }
        }

        fsFolderParams.setAdminFlag(adminFlag);
        fsFolderParams.setGroupList(listGroup);
        fsFolderParams.setUserId(userId);
        fsFolderParams.setType(type);
        fsFolderParams.setLevelCodeString(folder.getLevelCode());
        String deptId = iSysUsersService.getById(userId).getDepartmentId();
        String orgId = "";
        SysStru stru = sysStruMapper.selectById(deptId);
        if(stru!=null){
            orgId = stru.getOrganAlias();
        }
        String levelCodeString = businessService.getUploadLevelCodeFrontMobile(fsFolderParams,orgId);
        fsFolderParams.setLevelCodeString(levelCodeString);
        List<FsFolder> list=new ArrayList<>();
        if ("#".equals(id)) {
            //首次访问
            String idParam = "root";
            //获取根节点
            fsFolderParams.setId(idParam);
            fsFolderParams.setType("0");

            list = fsFolderService.getTreeDataLazy(fsFolderParams);
            List<String> firstList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                FsFolder fsFolder = list.get(i);
                firstList.add(fsFolder.getFolderId());
            }
            //获取第一级
            List<FsFolder> childList =fsFolderService.getChildList(firstList, listGroup, userId, adminFlag, type,levelCodeString);
            List<String> secondList = new ArrayList<>();
            //将文件id拼接
            for (int i = 0; i < childList.size(); i++) {
                FsFolder fsFolder = childList.get(i);
                secondList.add(fsFolder.getFolderId());
            }
            //获取第一级是否有下级
            List<Map> childCountList = fsFolderService.getChildCountList(secondList, listGroup, userId, adminFlag, type,levelCodeString);
            List<Map> childResultList = fsFolderService.checkChildCount(childList,childCountList);

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
                String createUserId = fsFolder.getCreateUserId();
                String authorName =sysUsersService.getById(createUserId).getUserName();
                parentMap.put("authorName", authorName);
                parentMap.put("createTime", fsFolder.getCreateTime());
                resultList.add(parentMap);
            }

        } else {

            List<String> firstList = new ArrayList<>();

            fsFolderParams.setId(id);
            fsFolderParams.setType(type);
            list = fsFolderService.getTreeDataLazyMobile(fsFolderParams,folderIds);
            for (int i = 0; i < list.size(); i++) {
                FsFolder fsFile = list.get(i);
                firstList.add(fsFile.getFolderId());
            }

            List<Map> childCountList = fsFolderService.getChildCountListMobile(firstList, listGroup, userId, adminFlag, type, levelCodeString,folderIds);
            resultList = clientFilesService.checkChildCount(list, childCountList);

        }
        FsFolder fsFolder =fsFolderService.getById(id);
        if (adminFlag != 1) {
            if (userId.equals(fsFolder.getCreateUserId())) {
                folderMap.put("noChildPowerFolder", 1);
            }else{
            int isEdits = docFoldAuthorityService.findEditNewClient(id, listGroup, userId,orgId);
                folderMap.put("noChildPowerFolder", isEdits);
            }
        }else {
            folderMap.put("noChildPowerFolder", 1);
        }
        folderMap.put("list",resultList);
        return folderMap;
    }

    @RequestMapping("/changeFolder")
    @ResponseBody
    public Object changeFolder(String id) {
        String userId = jwtUtil.getSysUsers().getUserId();
        String deptId = iSysUsersService.getById(userId).getDepartmentId();
        SysStru stru = sysStruMapper.selectById(deptId);
        String orgId = "";
        if( sysStruMapper.selectById(deptId)!=null){
            orgId = stru.getOrganAlias();
        }
        List<String> listGroup = docGroupService.getPremission(userId);
        List<String> roleList = sysUserRoleService.getRolesByUserId(userId);
        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        Map<String, Object> result = new HashMap<>(5);
        if (adminFlag != 1) {
            int isEdits = docFoldAuthorityService.findEditByUploadClient(id, listGroup, userId,orgId);
            result.put("noChildPower", isEdits);
        }else{
            result.put("noChildPower",2);
        }
        return  result;
    }

    /**
     * @title: 查询下级节点
     * @description: 查询下级节点（文件和目录）
     * @date: 2018-8-12.
     * @author: yjs
     */
    @RequestMapping(value = "/getFilesAndFolder")
    @ResponseBody
    public ApiResponse getChildren(@RequestParam String id,
                                   @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
                                   @RequestParam(value = "pageSize", defaultValue = "60") int pageSize, String order, String name,
                                   String type, String operateType, HttpServletRequest request) {
        //long time1 = System.currentTimeMillis();
        //System.out.println("131313===========getFilesAndFolder查询开始====================");
        if (id == null || id.equals("#") || id.equals("")) {
            List<FsFolder> list = fsFileService.getRoot();
            Map map = new HashMap();
            FsFolder fsFile = list.get(0);
            id = fsFile.getFolderId();
        }
        String userId = jwtUtil.getSysUsers().getUserId();
        // System.out.println("131313===========getFilesAndFolder-userId===================="+userId);
        Map orderMap = new HashMap();
        Map typeMap = new HashMap();
        String isDesc = "0";
        if ("1".equals(order) || "3".equals(order) || "5".equals(order)) {
            isDesc = "1";
        }
        //排序和查询规则
        orderMap.put("0", "fileName");
        orderMap.put("1", "fileName");
        orderMap.put("2", "createTime");
        orderMap.put("3", "createTime");
        orderMap.put("4", "createUserName");
        orderMap.put("5", "createUserName");
        typeMap.put("1", ".doc,.docx");
        typeMap.put("2", ".ppt,.pptx");
        typeMap.put("3", ".txt");
        typeMap.put("4", ".pdf");
        typeMap.put("5", ".xls,.xlsx");
        String orderResult = (String) orderMap.get(order);
        Map<String, Object> result = new HashMap<>(5);
        List<FsFolderView> list = new ArrayList<>();
        List<FsFolderView> newList = new ArrayList<>();
        List<String> folderIds = null;
        int num = 0;
        //判断是否为子级目录（只能在子文件夹上传文件）
        boolean isChild = fsFileService.isChildren(id);
        List<String> listGroup = docGroupService.getPremission(userId);
        List<String> roleList = sysUserRoleService.getRolesByUserId(userId);
        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        FsFolder fsFolder = fsFolderService.getById(id);
        String[] typeArr;
        if (type == null) {
            type = "0";
        }
        if ("0".equals(type)) {
            typeArr = null;
        } else {
            String typeResult = (String) typeMap.get(type);
            typeArr = typeResult.split(",");
        }

        name = StringUtil.transferSqlParam(name);
        FsFolder folder = fsFolderService.getById(id);
        FsFolderParams fsFolderParams = new FsFolderParams();
        fsFolderParams.setGroupList(listGroup);
        fsFolderParams.setUserId(userId);
        fsFolderParams.setType(operateType);
        fsFolderParams.setLevelCodeString(folder.getLevelCode());
        fsFolderParams.setId(id);
//        List<String> levelCodeList = folderService.getlevelCodeList(listGroup, userId, type);
        String deptId = iSysUsersService.getById(userId).getDepartmentId();
        String orgId = "";
        SysStru stru = sysStruMapper.selectById(deptId);
        if(stru!=null){
            orgId = stru.getOrganAlias();
        }
        //System.out.println("131313==========查询levelCodeString开始======");
        //long time3 = System.currentTimeMillis();
        String levelCodeString = businessService.getFileLevelCodeFrontMobile(fsFolderParams);
        //long time4 = System.currentTimeMillis();
        //System.out.println("131313==========查询levelCodeString结束======"+(time4-time3));
        //获得目录管理权限层级码
        fsFolderParams.setType("2");
        //System.out.println("131313==========查询levelCode开始======");
        // long time5 = System.currentTimeMillis();
        String levelCode = businessService.getLevelCodeByUserUploadClient(fsFolderParams, orgId);
        //long time6 = System.currentTimeMillis();
        // System.out.println("131313==========查询levelCode结束======"+(time6-time5));
        //获得下一级文件和目录

        // System.out.println("131313==========查询数据开始======");
        // long time7 = System.currentTimeMillis();

        //long time8 = System.currentTimeMillis();
        //System.out.println("131313==========查询数据结束======"+(time8-time7));
        //判断是否有外网权限
        if (StringUtils.equals(openExtranetLimit, "true")) {
            // 用户是从外网访问的系统
            if (adminFlag!=1 && RemoteIpMobileUtil.isExtranetVisit(request)) {
                Map<String,Object> userExtranet = fsMobileFolderService.isUserExtranet(userId);
                int code = (Integer)userExtranet.get("code");
                String msg = (String)userExtranet.get("msg");
                if (code == 203){
                    return ApiResponse.data(code, result, msg);
                }

                folderIds = iFolderExtranetAuthService.getFolderExtranetListMobile();
                if (folderIds == null || folderIds.size() == 0) { // 没有外网可以访问的目录
                    System.out.println("===================没有配置外网可以访问的目录=====================");
                    result.put("list", null);
                    result.put("amount",0 );
                    result.put("pageSize", pageSize);
                    result.put("pageNum", pageNumber);
                    return ApiResponse.data(200, result, "");
                }

            }
        }
        list = fsFolderService.getFilesAndFloderMobile((pageNumber - 1) * pageSize, pageSize, id, typeArr, name,
                orderResult, listGroup, userId, adminFlag, operateType, levelCodeString, levelCode, isDesc, orgId, roleList,folderIds);
        list = changeSize(list);

        //获得下一级文件和目录数量
        // System.out.println("131313==========查询num开始======");
        //long time9 = System.currentTimeMillis();
        num = fsFolderService.getFilesAndFloderNumMobile(id, typeArr, name, orderResult, listGroup, userId,
                adminFlag, operateType, levelCodeString, levelCode, orgId,roleList,folderIds);
        //long time10 = System.currentTimeMillis();
        //System.out.println("131313==========查询num结束======"+(time10-time9));
        list = changeSize(list);
        //显示前台的文件数量
        //System.out.println("131313==========查询amount开始======");
        //long time11 = System.currentTimeMillis();
        int amount = fsFolderService.getFileNum(id, typeArr, name, listGroup, userId, adminFlag, operateType, levelCode, orgId, roleList);
        //long time12 = System.currentTimeMillis();
        //System.out.println("131313==========查询amount结束======"+(time12-time11));
        //判断是否有可编辑文件的权限
        if (adminFlag != 1) {
            int isEdits = docFoldAuthorityService.findEditByUploadClient(id, listGroup, userId, orgId);
            result.put("noChildPower", isEdits);
        }
        if (userId.equals(fsFolder.getCreateUserId())) {
            result.put("noChildPower", 2);
        }
        String folderAmount = docConfigService.getConfigValueByKey("folder_amount");
        if (folderAmount == null || Integer.parseInt(folderAmount) < 4) {
            folderAmount = "4";
        }
        result.put("folderAmount", folderAmount);

        if (adminFlag != 1) {
            int isEdits = docFoldAuthorityService.findEditNewClient(id, listGroup, userId, orgId);
            result.put("noChildPowerFolder", isEdits);
        }
        if (userId.equals(fsFolder.getCreateUserId())) {
            result.put("noChildPowerFolder", 1);
        }
        result.put("isAdmin", adminFlag);
        result.put("total", num);
        result.put("rows", list);
        FsFolder fsfolder = new FsFolder();
        fsfolder = fsFolderService.getById(id);
        if (fsfolder.getOwnId() == null || "".equals(fsfolder.getOwnId())) {
            result.put("isOwn", "0");
        } else {
            result.put("isOwn", "1");
        }
//        result.put("isChild", isChild);
        result.put("amount", amount);

        //long time2 = System.currentTimeMillis();
        // System.out.println("131313===========getFilesAndFolder查询结束===================="+(time2-time1));
        return ApiResponse.data(200, result, "");
    }

    /**
     * 转化文件大小的方法
     */
    public List<FsFolderView> changeSize(List<FsFolderView> list) {

        for (FsFolderView fsFolderView : list) {
            if (fsFolderView.getFileSize() != null && !"".equals(fsFolderView.getFileSize())) {
                fsFolderView.setFileSize(FileTool.longToString(fsFolderView.getFileSize()));
            }
            if (!"folder".equals(fsFolderView.getFileType())) {
                fsFolderView.setCollection(personalCollectionService.getMyCollectionCountByFileId(fsFolderView.getFileId(),jwtUtil.getSysUsers().getUserId())+"");
            }
        }
        return list;
    }

    /**
     * 删除文件（级联删除）
     */
    @RequestMapping(value = "/deleteScope")
    @ResponseBody
    public ApiResponse deleteScope(@RequestParam String fsFileIds) {
        String userId = jwtUtil.getSysUsers().getUserId();
        cacheToolService.updateLevelCodeCache(userId);
        String[] strArr = fsFileIds.split(",");
        List list = new ArrayList();
        list.addAll(Arrays.asList(strArr));
        int num = fsFileService.deleteScopeClient(list, userId);

        for (String id : strArr) {
            Map map = new HashMap(1);
            //0为无效，1为有效
            map.put("recycle", DocConstant.VALIDTYPE.INVALID.getValue());
            esUtil.updateIndex(id, map);
        }
        cacheToolService.deleteEmpUsedSpace(userId);
        return ApiResponse.data(200, num, "");
    }

    @RequestMapping("/getShareFlag")
    @ResponseBody
    public ApiResponse getShareFlag(String docIds) {
        String[] docStrs = docIds.split(",");
        if (docStrs.length > 1) {
            return ApiResponse.data(200, false, "");
        } else {
            String docId = docStrs[0];
            DocInfo docInfo = docInfoService.getDocDetail(docId);
            if ("0".equals(docInfo.getShareFlag())) {
                return ApiResponse.data(200, false, "");
            } else {
                return ApiResponse.data(200, true, "");
            }
        }
    }

    /**
     * 批量设置文档是否可分享
     *
     * @param docIds    文档ID
     * @param shareFlag 是否可分享(0:不可  1:可分享)
     * @return true设置成功/false设置失败
     */
    @RequestMapping("/setShareFlags")
    @ResponseBody
    public ApiResponse setShareFlags(String docIds, String shareFlag) {
        List<String> list = Arrays.asList(docIds.split(","));
        List<DocInfo> docInfos = new ArrayList<>();
        for (String i : list) {
            DocInfo docInfo = new DocInfo();
            docInfo.setShareFlag(shareFlag);
            docInfo.setDocId(i);
            docInfos.add(docInfo);
        }
        return ApiResponse.data(200, docInfoService.updateBatchById(docInfos) + "", "");
    }

    /**
     * @return 获得标签
     * @Author yjs
     * @Description 跳转到新增标签页面
     * @Date 14:36 2018/10/30
     * @Param []
     **/
    @RequestMapping("/getTip")
    @ResponseBody
    public ApiResponse gettip(String docId) {
        DocInfo docInfo = docInfoService.getDocDetail(docId);
        JSONObject json = new JSONObject();
        if (docInfo.getTags() != null) {
            json.put("tip", docInfo.getTags());
        }
        return ApiResponse.data(200, json, "");
    }

    /**
     * @return 新增标签
     * @Author yjs
     * @Description 跳转到新增标签页面
     * @Date 14:36 2018/10/30
     * @Param []
     **/
    @RequestMapping("/addtip")
    @ResponseBody
    public JSON addtip(String docId, String tip) {
        DocInfo docInfo = new DocInfo();
        docInfo.setDocId(docId);
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        docInfo.setUpdateTime(ts);
        docInfo.setTags(tip);
        JSONObject json = new JSONObject();
        boolean success = docInfoService.updateById(docInfo);
        Map map = new HashMap(1);
        //0为无效，1为有效
        map.put("tags", tip);
        esUtil.updateIndex(docId, map);
        json.put("success", success);
        return json;
    }

    /**
     * 动态加载文件树
     */
    @RequestMapping(value = "/getMoveTreeDataLazy")
    @ResponseBody
    public ApiResponse getMoveTreeDataLazy(String id, String type, HttpServletRequest request) {
        String userId = jwtUtil.getSysUsers().getUserId();
        type = "1";
        List resultList = new ArrayList();
        FsFolderParams fsFolderParams = new FsFolderParams();

        String deptId = iSysUsersService.getById(userId).getDepartmentId();
        String orgId = "";
        SysStru stru = sysStruMapper.selectById(deptId);
        if(stru!=null){
            orgId = stru.getOrganAlias();
        }
        List<String> listGroup = docGroupService.getPremission(userId);
        List<String> roleList = sysUserRoleService.getRolesByUserId(userId);

        Integer adminFlag = CommonUtil.getAdminFlag(roleList);

        fsFolderParams.setAdminFlag(adminFlag);
        fsFolderParams.setGroupList(listGroup);
        fsFolderParams.setUserId(userId);
        fsFolderParams.setType("2");

        String levelCodeString = businessService.getLevelCodeByUserClient(fsFolderParams, orgId);
        List<String> folderIds = new ArrayList<>();
        //判断是否有外网权限
        if (StringUtils.equals(openExtranetLimit, "true")) {
            // 用户是从外网访问的系统
            if (adminFlag!=1 && RemoteIpMobileUtil.isExtranetVisit(request)) {
                Map<String,Object> userExtranet = fsMobileFolderService.isUserExtranet(userId);
                int code = (Integer)userExtranet.get("code");
                String msg = (String)userExtranet.get("msg");
                if (code == 203){
                    return ApiResponse.data(code, resultList, msg);
                }

                folderIds = iFolderExtranetAuthService.getFolderExtranetListMobile();
                if (folderIds == null || folderIds.size() == 0) { // 没有外网可以访问的目录
                    System.out.println("===================没有配置外网可以访问的目录=====================");
                    return ApiResponse.data(200, resultList, "");
                }

            }
        }
        fsFolderParams.setLevelCodeString(levelCodeString);
        if ("#".equals(id) || "".equals(id)) {
            String idParam = "root";
            List<String> firstList = new ArrayList<>();
            List<String> secondList = new ArrayList<>();
            fsFolderParams.setId(idParam);
            fsFolderParams.setType(DocConstant.OPERATETYPE.FRONT.getValue());
            List<FsFolder> list = fsFolderService.getTreeDataLazy(fsFolderParams);

            for (int i = 0; i < list.size(); i++) {
                FsFolder fsFile = list.get(i);
                firstList.add(fsFile.getFolderId());
            }
            //
            List<FsFolder> childList = fsFolderService.getChildListMobile(firstList, listGroup, userId, adminFlag, type, levelCodeString,folderIds);

            for (int i = 0; i < childList.size(); i++) {
                FsFolder fsFile = childList.get(i);
                secondList.add(fsFile.getFolderId());
            }
            List<Map> childCountList = fsFolderService.getChildCountListMobile(secondList, listGroup, userId, adminFlag, type, levelCodeString,folderIds);
            //
            List<Map> childResultList = clientFilesService.checkChildCount(childList, childCountList);

            for (int i = 0; i < list.size(); i++) {
                Map parentMap = new HashMap();
                FsFolder fsFile = list.get(i);
                parentMap.put("id", fsFile.getFolderId());
                parentMap.put("text", fsFile.getFolderName());
                String createUserId = fsFile.getCreateUserId();
                if (createUserId == null) {
                    parentMap.put("authorName", "");
                } else {
                    SysUsers sysUsers = iSysUsersService.getById(createUserId);
                    if (sysUsers != null) {
                        String authorName = iSysUsersService.getById(createUserId).getUserName();
                        parentMap.put("authorName", authorName);
                    } else {
                        parentMap.put("authorName", createUserId);
                    }
                }
                parentMap.put("createTime", fsFile.getCreateTime());
                List childMapList = new ArrayList();
                for (int j = 0; j < childResultList.size(); j++) {
                    Map map = childResultList.get(j);
                    if (fsFile.getFolderId().equals(map.get("pid"))) {
                        childMapList.add(map);
                    }
                }
                parentMap.put("children", childMapList);
                parentMap.put("opened", true);
                resultList.add(parentMap);
            }
        } else {
            List<String> firstList = new ArrayList<>();

            fsFolderParams.setId(id);
            fsFolderParams.setType(type);
            List<FsFolder> list = fsFolderService.getTreeDataLazyMobile(fsFolderParams,folderIds);
            for (int i = 0; i < list.size(); i++) {
                FsFolder fsFile = list.get(i);
                firstList.add(fsFile.getFolderId());
            }

            List<Map> childCountList = fsFolderService.getChildCountListMobile(firstList, listGroup, userId, adminFlag, type, levelCodeString,folderIds);
            resultList = clientFilesService.checkChildCount(list, childCountList);
        }
        return ApiResponse.data(200, resultList, "");
    }

    /**
     * 移动
     *
     * @return boolean
     * @author: ChenXin
     */
    @RequestMapping("/move")
    @ResponseBody
    public ApiResponse move(String fileId, String folderId, String fileName) {

        String userId = jwtUtil.getSysUsers().getUserId();
        cacheToolService.updateLevelCodeCache(userId);
        String deptId = "";
        SysUsers users =  iSysUsersService.getById(userId);
        if(users!=null){
            deptId = iSysUsersService.getById(userId).getDepartmentId();
        }
        String orgId = "";
        SysStru sysStru = sysStruMapper.selectById(deptId);
        if(sysStru!=null){
            orgId = sysStruMapper.selectById(deptId).getOrganAlias();
        }
        List<String> listGroup = docGroupService.getPremission(userId);
        List<String> roleList = sysUserRoleService.getRolesByUserId(userId);
        JSONObject json = new JSONObject();
        if (ToolUtil.isEmpty(fileId)) {
            fileId = "";
        }
        //判断是否为子级目录（只能在子文件夹上传文件）
        boolean isChild = fsFileService.isChildren(folderId);
        String[] strFileId = fileId.split(",");
        String[] strFileName = fileName.split(",");
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        for (int i = 0; i < strFileId.length; i++) {
            DocInfo docInfo = docInfoService.getDocDetail(strFileId[i]);
            if (adminFlag != 1) {
                int isEdits = docFoldAuthorityService.findEditClient(folderId, listGroup, userId, orgId);
                if (isEdits != 2) {
                    json.put("result", "3");
                    return ApiResponse.data(200, json, "");
                }
            }
            String fileNameStr = strFileName[i] + docInfo.getDocType();
            if (iDocRecycleService.checkDocExist(folderId, fileNameStr)) {
                json.put("result", "0");
                return ApiResponse.data(200, json, "");
            } else {
                if (fsFileService.remove(strFileId[i], folderId,userId)) {
                    FsFolder parentFolder = fsFolderService.getById(folderId);
                    if (parentFolder != null && parentFolder.getOwnId() != null && parentFolder.getOwnId().equals(userId)) {
                        docFileAuthorityService.remove(new QueryWrapper<DocFileAuthority>().eq("file_id", strFileId[i]));
                    }
                    json.put("result", "1");
                } else {
                    json.put("result", "2");

                }
            }
        }
        return ApiResponse.data(200, json, "");
    }

    /**
     * @title: 修改文件
     * @description: 修改文件名字和粘贴
     * @date: 2018-8-13.
     * @author: yjs
     */
    @RequestMapping(value = "/update")
    @ResponseBody
    public ApiResponse update(String ids, String filePid, String fileName, String type) {
        //如果没有传值filename，则执行粘贴操作
        if (fileName == null || "".equals(fileName)) {
            String[] strArr = ids.split(",");
            List<DocInfo> listDocInfos = new ArrayList<DocInfo>();
            for (String element : strArr) {
                DocInfo docInfo = new DocInfo();
                docInfo.setFileId(element);
                docInfo.setDocId(element);
                docInfo.setFoldId(filePid);
                listDocInfos.add(docInfo);
            }
            docInfoService.saveOrUpdateBatch(listDocInfos);
        } else {
            //如果传值filename，则执行修改文件名操作
            if (!"folder".equals(type)) {
                List<Map> idList = fsFileService.getDocId(ids);
                Map mapParam = idList.get(0);
                String docId = (String) mapParam.get("id");
                DocInfo docInfo = docInfoService.getById(docId);
                Timestamp ts = new Timestamp(System.currentTimeMillis());
                docInfo.setUpdateTime(ts);
                docInfo.setTitle(fileName);
                docInfoService.updateById(docInfo);
                FsFile fsFile = fsFileService.getById(docId);
                fsFile.setFileName(fileName);
                fsFileService.updateById(fsFile);
                Map<String, Object> map = new HashMap<String, Object>(16);
                map.put("title", fileName);
                esUtil.updateIndex(docId, map);
                String userId = jwtUtil.getSysUsers().getUserId();
                List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
                DocResourceLog docResourceLog = new DocResourceLog();
                String id = UUID.randomUUID().toString().replace("-", "");
                docResourceLog.setId(id);
                docResourceLog.setResourceId(docId);
                docResourceLog.setOperateTime(ts);
                docResourceLog.setResourceType(0);
                docResourceLog.setUserId(userId);
                docResourceLog.setOperateType(8);
                resInfoList.add(docResourceLog);
                docInfoService.insertResourceLog(resInfoList);
            }
        }
        return ApiResponse.data(200, "success", "");
    }

    /**
     * 查询文件权限
     *
     * @return
     */
    @RequestMapping("/getAuthority")
    @ResponseBody
    public ApiResponse getAuthority(String fileId) {
        DocInfo docInfo = docInfoService.getDocDetail(fileId);
        if (docInfo != null) {
            String authorId = docInfo.getAuthorId();
            if (authorId != null) {
                SysUsers user = sysUsersService.getById(authorId);
            }
        }
        List list = fsFileService.getAuthority(fileId);
        return ApiResponse.data(200, list, "");
    }

    /**
     * 修改目录权限
     *
     * @return
     */
    @RequestMapping("/editAuthority")
    @ResponseBody
    public ApiResponse editAuthority(String fileId, String group, String person, String authorId, String personOrgan
            , String authorTypeStrGroup, String authorTypeStrPerson, String operateTypeStrGroup, String operateTypeStrPerson
    ) {
        boolean flag = false;
        String userId = jwtUtil.getSysUsers().getUserId();
        cacheToolService.updateLevelCodeCache(userId);
        Date date = new Date();
        String[] fileIdArr = fileId.split(",");
        for (int j = 0; j < fileIdArr.length; j++) {
            DocInfo docInfo = new DocInfo();
            Timestamp ts = new Timestamp(date.getTime());
            docInfo.setUpdateTime(ts);
            docInfo.setDocId(fileIdArr[j]);
            docInfo.setSetAuthority("0");
            docInfoService.updateById(docInfo);
//        Map<String, Object> map = new HashMap<String, Object>(16);
//        map.put("title", docInfo.getTitle());
//        esUtil.updateIndex(fileId, map);
            docFileAuthorityService.remove(new QueryWrapper<DocFileAuthority>().eq("file_id", fileIdArr[j]));
            List<String> indexList = new ArrayList<>();
            List<DocFileAuthority> list = new ArrayList<>();
            if (group != null && !("".equals(group))) {
                String[] groupArr = group.split(",");
                String[] authorTypeArrGroup = authorTypeStrGroup.split(",");
                String[] operateTypeArrGroup = operateTypeStrGroup.split(",");
                for (int i = 0; i < groupArr.length; i++) {
                    DocFileAuthority docFileAuthority = new DocFileAuthority();
                    docFileAuthority.setFileAuthorityId(UUID.randomUUID().toString().replaceAll("-", ""));
                    docFileAuthority.setAuthorId(groupArr[i]);
                    //操作者类型（0：userID,1:groupID,2:roleID）
                    docFileAuthority.setAuthorType(StringUtil.getInteger(authorTypeArrGroup[i]));
                    docFileAuthority.setFileId(fileIdArr[j]);
                    docFileAuthority.setAuthority(StringUtil.getInteger(operateTypeArrGroup[i]));
                    list.add(docFileAuthority);
                    indexList.add(groupArr[i]);
                }
            }
            if (person != null && !("".equals(person))) {
                String[] personArr = person.split(",");
                String[] personOrganArr = personOrgan.split(",");
                String[] authorTypeArrPerson = authorTypeStrPerson.split(",");
                String[] operateTypeArrPerson = operateTypeStrPerson.split(",");
                for (int i = 0; i < personArr.length; i++) {
                    DocFileAuthority docFileAuthority = new DocFileAuthority();
                    docFileAuthority.setFileAuthorityId(UUID.randomUUID().toString().replaceAll("-", ""));
                    if (authorTypeArrPerson[i].equals("2")) {
                        SysStru sysStru = sysStruMapper.selectById(personArr[i]);
                        if (sysStru == null) {
                            docFileAuthority.setOrganId(personOrganArr[i]);
                            docFileAuthority.setAuthorId(personArr[i]);
                        } else {
                            docFileAuthority.setOrganId(sysStru.getOrganId());
                            docFileAuthority.setAuthorId(sysStru.getOrganAlias());
                        }
                    } else {
                        docFileAuthority.setAuthorId(personArr[i]);
                    }
                    //操作者类型（0：userID,1:groupID,2:roleID）
                    docFileAuthority.setAuthorType(StringUtil.getInteger(authorTypeArrPerson[i]));
                    docFileAuthority.setFileId(fileIdArr[j]);
                    docFileAuthority.setAuthority(Integer.parseInt(operateTypeArrPerson[i]));
                    list.add(docFileAuthority);
                    if (StringUtil.getInteger(authorTypeArrPerson[i]) == 0) {
                        indexList.add(personArr[i]);
                    }
                    if (StringUtil.getInteger(authorTypeArrPerson[i]) == 2) {
                        indexList.add(personOrganArr[i]);
                    }


                }

            }
            indexList.add(authorId);
            Map map = new HashMap(1);
            //0为无效，1为有效
            map.put("permission", indexList.toArray(new String[indexList.size()]));
            esUtil.updateIndex(fileIdArr[j], map);
            if (list.size() > 0) {
                flag = docFileAuthorityService.saveBatch(list);
            }
        }
        return ApiResponse.data(200, flag, "");
    }

    /**
     * 缓存专题查看数据数据
     *
     * @author xubin
     * @date 2018-07-10 9:04
     */
    @RequestMapping("/cacheViewNum")
    @ResponseBody
    public void saveCache(String docId) {
        cacheToolService.getAndUpdateReadNum(docId);
        List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
        DocResourceLog docResourceLog = new DocResourceLog();
        String id = UUID.randomUUID().toString().replace("-", "");
        docResourceLog.setId(id);
        docResourceLog.setResourceId(docId);
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        docResourceLog.setOperateTime(ts);
        docResourceLog.setResourceType(0);
        String userId = jwtUtil.getSysUsers().getUserId();
        docResourceLog.setUserId(userId);
        docResourceLog.setOperateType(3);
        docResourceLog.setValidFlag("1");
        resInfoList.add(docResourceLog);
        docInfoService.insertResourceLog(resInfoList);   //插入预览记录
    }
}
