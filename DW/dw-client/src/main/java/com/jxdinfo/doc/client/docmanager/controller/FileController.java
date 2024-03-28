package com.jxdinfo.doc.client.docmanager.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxdinfo.doc.client.docmanager.service.ClientFilesService;
import com.jxdinfo.doc.client.response.ApiResponse;
import com.jxdinfo.doc.common.constant.DocConstant;
import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.docutil.service.DocConfigService;
import com.jxdinfo.doc.common.jwt.util.JWTUtil;
import com.jxdinfo.doc.common.util.*;
import com.jxdinfo.doc.manager.collectionmanager.service.PersonalCollectionService;
import com.jxdinfo.doc.manager.docconfigure.model.DocConfigure;
import com.jxdinfo.doc.manager.docconfigure.service.DocConfigureService;
import com.jxdinfo.doc.manager.docmanager.model.*;
import com.jxdinfo.doc.manager.docmanager.service.DocFileAuthorityService;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.docmanager.service.FilesService;
import com.jxdinfo.doc.manager.docmanager.service.FsFileService;
import com.jxdinfo.doc.manager.docrecycle.service.IDocRecycleService;
import com.jxdinfo.doc.manager.foldermanager.model.DocFoldAuthority;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.foldermanager.service.IDocFoldAuthorityService;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import com.jxdinfo.doc.manager.groupmanager.service.DocGroupService;
import com.jxdinfo.doc.manager.personalcenter.service.PersonalOperateService;
import com.jxdinfo.hussar.bsp.organ.dao.SysStruMapper;
import com.jxdinfo.hussar.bsp.organ.model.SysStru;
import com.jxdinfo.hussar.bsp.permit.model.SysUsers;
import com.jxdinfo.hussar.bsp.permit.service.ISysUserRoleService;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
import com.jxdinfo.hussar.core.base.controller.BaseController;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.support.HttpKit;
import com.jxdinfo.hussar.core.util.ToolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
@Controller
@RequestMapping("/client/file")
public class FileController extends BaseController {


    /**
     * 我的收藏
     */
    @Autowired
    private PersonalCollectionService personalCollectionService;
    @Resource
    private PersonalOperateService operateService;
    @Autowired
    private FsFileService fsFileService;

    @Autowired
    private ClientFilesService clientFilesService;

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


    /**
     * 获取根节点
     */
    @RequestMapping(value = "/getRoot")
    @ResponseBody
    public Map getRoot() {
        List<FsFolder> list = fsFileService.getRoot();
        Map map = new HashMap();
        FsFolder fsFile = list.get(0);
        map.put("root", fsFile.getFolderId());
        map.put("rootName", fsFile.getFolderName());
        return map;
    }

    /**
     * 新增文件夹
     */
    @RequestMapping(value = "/add")
    @ResponseBody
    public Object add(FsFile fsFile, String group, String person) {
        String userId = UserInfoUtil.getUserInfo().get("ID").toString();
        cacheToolService.updateLevelCodeCache(userId);

        if (fsFile.getFileId() != null && !"".equals(fsFile.getFileId())) {
            updateFsFile(fsFile);
        } else {
            Date date = new Date();
            Timestamp ts = new Timestamp(date.getTime());
            fsFile.setCreateTime(ts);
            fsFile.setFileType("folder");
            fsFile.setFileIcon("default");
            String fileId = UUID.randomUUID().toString().replaceAll("-", "");
            fsFile.setFileId(fileId);
            fsFileService.save(fsFile);
            List<DocFoldAuthority> list = new ArrayList<>();
            if (group != null) {
                String[] groupArr = group.split(",");
                for (String element : groupArr) {
                    DocFoldAuthority docFoldAuthority = new DocFoldAuthority();
                    docFoldAuthority.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                    docFoldAuthority.setAuthorId(element);
                    //操作者类型（0：userID,1:groupID,2:roleID）
                    docFoldAuthority.setAuthorType(DocConstant.AUTHORTYPE.GROUP.toString());
                    docFoldAuthority.setFoldId(fileId);
                    list.add(docFoldAuthority);
                }
            }
            if (person != null) {
                String[] personArr = person.split(",");
                for (String element : personArr) {
                    DocFoldAuthority docFoldAuthority = new DocFoldAuthority();
                    docFoldAuthority.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                    docFoldAuthority.setAuthorId(element);
                    //操作者类型（0：userID,1:groupID,2:roleID）
                    docFoldAuthority.setAuthorType(DocConstant.AUTHORTYPE.USER.toString());
                    docFoldAuthority.setFoldId(fileId);
                    list.add(docFoldAuthority);
                }
            }
            docFoldAuthorityService.saveBatch(list);
        }
        return SUCCESS_TIP;
    }

    /**
     * 修改文件
     */
    @RequestMapping(value = "/editFile")
    @ResponseBody
    public Object editFile(FsFile fsFile, String authorId, String contactsId) {
        //修改fs_file表中的信息
        updateFsFile(fsFile);
        //修改作者联系人信息
        fsFileService.updateFileAuthor(fsFile.getFileId(), authorId, contactsId);
        return SUCCESS_TIP;
    }

    /**
     * 新增重名检测
     */
    @RequestMapping(value = "/addCheck")
    @ResponseBody
    public ApiResponse addCheck(String name, String filePid) {
        List list = fsFileService.addCheck(filePid, name);
        if (list.size() > 0) {
            return ApiResponse.data(200, "false", "");
        }
        return ApiResponse.data(200, "true", "");
    }

    /**
     * 删除文件（级联删除）
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    public String delete(@RequestParam String fsFileIds) {
        String[] strArr = fsFileIds.split(",");
        String userId = ShiroKit.getUser().getId();
        cacheToolService.updateLevelCodeCache(userId);
        List list = new ArrayList();
        for (String element : strArr) {
            String ids = fsFileService.getChildFsFile(element);
            String[] childArr = ids.split(",");
            list.addAll(Arrays.asList(childArr));
        }
        int num = fsFileService.deleteInIds(list);
        return "success";
    }


    /**
     * 删除文件（级联删除）
     */
    @RequestMapping(value = "/deleteFileAndFolder")
    @ResponseBody
    public ApiResponse deleteFileAndFolder(@RequestParam String fsFileIds, @RequestParam String fsFolderIds) {
        try {
            if (fsFolderIds != null) {
                Map<String, String> map = new HashMap<>();
                String num = fsFolderService.checkChildType(fsFolderIds);
                int fileNum = Integer.parseInt(num);
                if (fileNum > 0) {
                    return ApiResponse.fail(401, "请先删除目录下存放的文件");
                } else {
                    String userId = jwtUtil.getSysUsers().getUserId();
                    cacheToolService.updateLevelCodeCache(userId);
                    String[] strArr = fsFolderIds.split(",");
                    List<String> list = new ArrayList();
                    for (String element : strArr) {
                        String ids = fsFolderService.getChildFsFolder(element);
                        String[] childArr = ids.split(",");
                        list.addAll(Arrays.asList(childArr));
                    }
                    //删除目录
                    fsFolderService.deleteInIds(list);
                    boolean isDelete = docFoldAuthorityService.remove(new QueryWrapper<DocFoldAuthority>().in("folder_id", list));
                }
            }
            if (fsFileIds != null) {
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

            }
            return ApiResponse.data(200, true, "");
        } catch (Exception e) {
            return ApiResponse.fail(500, "删除失败，请联系管理员");
        }


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

    /**
     * 粘贴时检查是否是下级
     */
    @RequestMapping(value = "/checkChild")
    @ResponseBody
    public String checkChild(@RequestParam String fsFileIds, @RequestParam String id) {
        String[] strArr = fsFileIds.split(",");
        for (String element : strArr) {
            String ids = fsFileService.getChildFsFile(element);
            String[] childArr = ids.split(",");
            for (int i = 0; i < childArr.length; i++) {
                if (childArr[i].equals(id)) {
                    return "have";
                }
            }
        }
        return "success";
    }

    /**
     * 检查目录下是否有文件
     */
    @RequestMapping(value = "/checkFileType")
    @ResponseBody
    public Object checkFileType(@RequestParam String ids) {
        String[] strArr = ids.split(",");
        for (String element : strArr) {
            String types = fsFileService.checkChildType(element);
            String[] typeArr = types.split(",");
            for (int j = 0; j < typeArr.length; j++) {
                if (!("folder".equals(typeArr[j]))) {
                    return "haveFile";
                }
            }
        }
        return SUCCESS_TIP;
    }

    /**
     * 修改文件
     */
    @RequestMapping("/edit")
    @ResponseBody
    public DocInfo edit(Model model, String id) {
        DocInfo docInfo = docInfoService.getDocDetail(id);
        if (docInfo != null) {
            String authorId = docInfo.getAuthorId();
            if (authorId != null) {
                SysUsers user = sysUsersService.getById(authorId);
                docInfo.setAuthorName(user.getUserName());
            }

        }

        return docInfo;
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
                                   String type, String operateType) {
        if (id == null || id.equals("#") || id.equals("")) {
            List<FsFolder> list = fsFileService.getRoot();
            Map map = new HashMap();
            FsFolder fsFile = list.get(0);
            id = fsFile.getFolderId();
        }
        String userId = jwtUtil.getSysUsers().getUserId();
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
        fsFolderParams.setRoleList(roleList);
        fsFolderParams.setType(operateType);
        fsFolderParams.setLevelCodeString(folder.getLevelCode());
        fsFolderParams.setId(id);
//        List<String> levelCodeList = folderService.getlevelCodeList(listGroup, userId, type);
        String deptId = docFoldAuthorityService.getDeptIds(iSysUsersService.getById(userId).getDepartmentId());

        // 查询所有有权限的目录
        String levelCodeString = businessService.getFileLevelCodeFrontMobile(fsFolderParams);
        //获得目录管理权限层级码
        fsFolderParams.setType("2");
        String levelCode = businessService.getLevelCodeByUserUploadClient(fsFolderParams, deptId);
        //获得下一级文件和目录

        list = fsFolderService.getFilesAndFloder((pageNumber - 1) * pageSize, pageSize, id, typeArr, name,
                orderResult, listGroup, userId, adminFlag, operateType, levelCodeString, levelCode, isDesc, deptId,roleList);
        list = changeSize(list);

        //获得下一级文件和目录数量
        num = fsFolderService.getFilesAndFloderNum(id, typeArr, name, orderResult, listGroup, userId,
                adminFlag, operateType, levelCodeString, levelCode, deptId,roleList);
        //显示前台的文件数量
        int amount = fsFolderService.getFileNum(id, typeArr, name, listGroup, userId, adminFlag, operateType, levelCode, deptId,roleList);
        //判断是否有可编辑文件的权限
        if (adminFlag != 1) {
            int isEdits = docFoldAuthorityService.findEditByUploadClient(id, listGroup, userId, deptId);
            result.put("noChildPower", isEdits);
        }
        if (userId.equals(fsFolder.getCreateUserId())) {
            result.put("noChildPower", 2);
        }
        if (adminFlag == 1) {
            result.put("noChildPower", 2);
        }
        String folderAmount = docConfigService.getConfigValueByKey("folder_amount");
        if (folderAmount == null || Integer.parseInt(folderAmount) < 4) {
            folderAmount = "4";
        }
        result.put("folderAmount", folderAmount);

        if (adminFlag != 1) {
            int isEdits = docFoldAuthorityService.findEditNewClient(id, listGroup, userId, deptId);
            result.put("noChildPowerFolder", isEdits);
        }
        if (userId.equals(fsFolder.getCreateUserId())) {
            result.put("noChildPowerFolder", 1);
        }
        if (adminFlag == 1) {
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

        return ApiResponse.data(200, result, "");
    }


    /**
     * @title: 查看文件信息
     * @description: 查看文件信息
     * @date: 2018-8-12.
     * @author: yjs
     */
    @RequestMapping(value = "/getInfo")
    @ResponseBody
    public List<Map> getInfo(@RequestParam String ids) {
        String[] strArr = ids.split(",");
        List<String> idList = new ArrayList<String>();
        for (String element : strArr) {
            idList.add(element);
        }
        String userId = UserInfoUtil.getCurrentUser().getId();
        List<String> listGroup = docGroupService.getPremission(userId);
        String orgId = docFoldAuthorityService.getDeptIds( ShiroKit.getUser().getDeptId());
        List<Map> list = fsFileService.getInfo(idList, userId, listGroup, null, orgId,sysUserRoleService.getRolesByUserId(userId));
        //从缓存去读取预览次数
        if (list != null) {
            for (int i = 0, j = list.size(); i < j; i++) {
                Map dataMap = list.get(i);
                int readNum = cacheToolService.getReadNum(StringUtil.getString(dataMap.get("fileId")));
                dataMap.put("readNum", readNum);
                list.set(i, dataMap);
            }
        }

        return list;
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
                docResourceLog.setOrigin("client");
                docResourceLog.setAddressIp(HttpKit.getIp());
                resInfoList.add(docResourceLog);
                docInfoService.insertResourceLog(resInfoList);
            }
        }
        return ApiResponse.data(200, "success", "");
    }

    /**
     * 修改更新文件--只修改名字和权限
     */
    public String updateFsFile(FsFile fsFile) {
        if (fsFile.getFileName() == null || "".equals(fsFile.getFileName())) {
            fsFileService.updateById(fsFile);
        } else {
            fsFileService.updateById(fsFile);
            if (!"folder".equals(fsFile.getFileType())) {
                List<Map> idList = fsFileService.getDocId(fsFile.getFileId());
                Map mapParam = idList.get(0);
                String docId = (String) mapParam.get("id");
                docInfoService.updateDocName(docId, fsFile.getFileName());
                Map<String, Object> map = new HashMap<String, Object>(16);
                map.put("title", fsFile.getFileName());
                esUtil.updateIndex(docId, map);
            }
        }
        return "success";
    }

    /**
     * 修改更新文件
     */
    @RequestMapping(value = "/getDocId")
    @ResponseBody
    public String getDocId(String id) {
        List<Map> idList = fsFileService.getDocId(id);
        Map map = idList.get(0);
        String fileId = (String) map.get("id");
        return fileId;
    }

    /**
     * 粘贴重命名检测
     */
    @RequestMapping(value = "/checkName")
    @ResponseBody
    public String checkName(String typeStr, String nameStr, String filePid) {
        String[] typeArr = typeStr.split(",");
        String[] nameArr = nameStr.split("\\*");
        List<Map> list = new ArrayList<>();
        for (int i = 0; i < typeArr.length; i++) {
            Map<String, String> map = new HashMap<>();
            map.put("name", nameArr[i]);
            map.put("type", typeArr[i]);
            list.add(map);
        }
        List listFs = fsFileService.countFileName(filePid, list);
        if (listFs.size() > 0) {
            return "fail";
        }
        return "success";
    }

    /**
     * 动态加载文件树
     */
    @RequestMapping(value = "/getTreeDataLazy")
    @ResponseBody
    public List getTreeDataLazy(String id, String type) {
        List resultList = new ArrayList();
        FsFolderParams fsFolderParams = new FsFolderParams();
        List<String> roleList = UserInfoUtil.getCurrentUser().getRolesList();
        String userId = UserInfoUtil.getCurrentUser().getId();
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        List<String> listGroup = docGroupService.getPremission(userId);
        FsFolder folder = new FsFolder();
        if ("#".equals(id)) {
            String fid = "2bb61cdb2b3c11e8aacf429ff4208431";
            folder = fsFolderService.getById(fid);
        } else {
            folder = fsFolderService.getById(id);
        }
        fsFolderParams.setAdminFlag(adminFlag);
        fsFolderParams.setGroupList(listGroup);
        fsFolderParams.setUserId(userId);
        fsFolderParams.setRoleList(roleList);
        fsFolderParams.setType(type);
        fsFolderParams.setLevelCodeString(folder.getLevelCode());
        fsFolderParams.setId(id);


        String levelCodeString = businessService.getFileLevelCode(fsFolderParams);
        fsFolderParams.setLevelCodeString(levelCodeString);
        if ("#".equals(id)) {
            String idParam = "root";
            List<String> firstList = new ArrayList<>();
            List<String> secondList = new ArrayList<>();
            fsFolderParams.setId(idParam);
            fsFolderParams.setRoleList(roleList);
            fsFolderParams.setType(DocConstant.OPERATETYPE.FRONT.getValue());
            List<FsFolder> list = fsFolderService.getTreeDataLazy(fsFolderParams);

            for (int i = 0; i < list.size(); i++) {
                FsFolder fsFile = list.get(i);
                firstList.add(fsFile.getFolderId());
            }
            List<FsFolder> childList = fsFolderService.getChildList(firstList, listGroup, userId, adminFlag, type, levelCodeString);

            for (int i = 0; i < childList.size(); i++) {
                FsFolder fsFile = childList.get(i);
                secondList.add(fsFile.getFolderId());
            }
            List<Map> childCountList = fsFolderService.getChildCountList(secondList, listGroup, userId, adminFlag, type, levelCodeString);
            List<Map> childResultList = fsFolderService.checkChildCount(childList, childCountList);

            for (int i = 0; i < list.size(); i++) {
                Map parentMap = new HashMap();
                FsFolder fsFile = list.get(i);
                parentMap.put("id", fsFile.getFolderId());
                parentMap.put("text", fsFile.getFolderName());
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
            fsFolderParams.setRoleList(roleList);
            fsFolderParams.setId(id);
            fsFolderParams.setType(type);
            List<FsFolder> list = fsFolderService.getTreeDataLazy(fsFolderParams);
            for (int i = 0; i < list.size(); i++) {
                FsFolder fsFile = list.get(i);
                firstList.add(fsFile.getFolderId());
            }

            List<Map> childCountList = fsFolderService.getChildCountList(firstList, listGroup, userId, adminFlag, type, levelCodeString);
            resultList = fsFolderService.checkChildCount(list, childCountList);
        }
        return resultList;
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
        docResourceLog.setOrigin("client");
        docResourceLog.setAddressIp(HttpKit.getIp());

        docResourceLog.setValidFlag("1");
        resInfoList.add(docResourceLog);
        docInfoService.insertResourceLog(resInfoList);   //插入预览记录
    }

    /**
     * 查询保密等级下拉框
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/searchLevel")
    @ResponseBody
    public JSON searchLevel(HttpServletRequest request, HttpServletResponse response) {
        List list = fsFileService.searchLevel();
        JSONObject json = new JSONObject();
        json.put("data", list);
        return json;
    }

    /**
     * 查询需要修改的目录的信息
     *
     * @param fsFileId
     * @return
     */
    @RequestMapping("/getFsFolderDetail")
    @ResponseBody
    public JSON getFsFolderDetail(String fsFileId) {
        List list = fsFileService.getFsFolderDetail(fsFileId);
        JSONObject json = new JSONObject();
        json.put("data", list);
        return json;
    }

    /**
     * 查询需要修改的文件的信息
     *
     * @param fsFileId
     * @return
     */
    @RequestMapping("/getFsFileDetail")
    @ResponseBody
    public JSON getFsFileDetail(String fsFileId) {
        List list = fsFileService.getFsfileDetail(fsFileId);
        JSONObject json = new JSONObject();
        json.put("data", list);
        return json;
    }

    /**
     * 查询需要修改的文件的信息
     *
     * @return
     */
    @RequestMapping("/getPersonList")
    @ResponseBody
    public JSON getPersonList(String name, @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
                              @RequestParam(value = "pageSize", defaultValue = "15") int pageSize, String deptId) {
        List<Map> list = fsFileService.getPersonList((pageNumber - 1) * pageSize, pageSize, name, deptId);
        int num = fsFileService.getPersonNum(name, deptId);
        JSONObject json = new JSONObject();
        json.put("count", num);
        json.put("data", list);
        json.put("msg", "success");
        json.put("code", 0);
        return json;
    }


    /**
     * 查询是否允许下载下拉框
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/downloadAble")
    @ResponseBody
    public JSON downloadAble(HttpServletRequest request, HttpServletResponse response) {
        List list = fsFileService.downloadAble();
        JSONObject json = new JSONObject();
        json.put("data", list);
        return json;
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
     * 转化文件大小的方法
     */
    public List<FsFolderView> changeSize(List<FsFolderView> list) {
        for (FsFolderView fsFolderView : list) {
            if (fsFolderView.getFileSize() != null && !"".equals(fsFolderView.getFileSize())) {
                fsFolderView.setFileSize(FileTool.longToString(fsFolderView.getFileSize()));
            }
            if (!fsFolderView.getFileType().equals("folder")) {
                int collection = personalCollectionService.getMyCollectionCountByFileId(fsFolderView.getFileId(),jwtUtil.getSysUsers().getUserId());
                if(collection>1){
                    collection = 1;
                }
                fsFolderView.setCollection(collection+"");
            }
        }
        return list;
    }

    /**
     * @return 文件权限页面调整后
     * @Author zoufeng
     * @Description 跳转到权限设置页面
     * @Date 14:36 2018/9/7
     * @Param []
     **/
    @RequestMapping("/fileAuthority")
    public String fileAuthority() {
        return "/doc/manager/docmanager/fileAuthority.html";
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
     * @return 新增标签
     * @Author yjs
     * @Description 跳转到新增标签页面
     * @Date 14:36 2018/10/30
     * @Param []
     **/
    @RequestMapping("/addTips")
    @ResponseBody
    public ApiResponse addTips(String docId, String tip) {
        Map<String, String> map = new HashMap<>();
        List<String> list = Arrays.asList(docId.split(","));
        for (String i : list) {
            DocInfo docInfo = new DocInfo();
            docInfo.setDocId(i);
            docInfo.setTags(tip);

            docInfoService.updateById(docInfo);
            Map esMap = new HashMap(1);
            //0为无效，1为有效
            esMap.put("tags", tip);
            esUtil.updateIndex(i, esMap);
        }
        map.put("success", "true");
        return ApiResponse.data(200, map, "");
    }

    /**
     * 设置文档是否可分享
     *
     * @param docId     文档ID
     * @param shareFlag 是否可分享(0:不可  1:可分享)
     * @return true设置成功/false设置失败
     */
    @RequestMapping("/setShareFlag")
    @ResponseBody
    public String setShareFlag(String docId, String shareFlag) {
        DocInfo docInfo = new DocInfo();
        docInfo.setDocId(docId);
        docInfo.setShareFlag(shareFlag);
        return docInfoService.updateById(docInfo) + "";
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

    @RequestMapping("/getShareFlag")
    @ResponseBody
    public ApiResponse getShareFlag(String docIds) {
        String[] docStrs = docIds.split(",");
        if (docStrs.length > 1) {
            return ApiResponse.data(200, false, "");
        } else {
            String docId = docStrs[0];
            DocInfo docInfo = docInfoService.getDocDetail(docId);
            if (docInfo.getShareFlag().equals(0)) {
                return ApiResponse.data(200, false, "");
            } else {
                return ApiResponse.data(200, true, "");
            }
        }
    }

    @RequestMapping("/shareFlagView")
    public String shareFlagView(String docId, String docIds, Model model) {
        if (docId != null) {
            DocInfo docInfo = docInfoService.getDocDetail(docId);
            String shareFlag = docInfo.getShareFlag() == null ? "0" : docInfo.getShareFlag();
            model.addAttribute("shareFlag", shareFlag);
            model.addAttribute("docId", docId);
            model.addAttribute("fileName", docInfo.getTitle());
            model.addAttribute("docIds", -1);
        } else {
            model.addAttribute("shareFlag", 0);
            model.addAttribute("docId", -1);
            model.addAttribute("docIds", docIds);
        }
        return "/doc/front/personalcenter/share_flag_view.html";
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
     * @return 标签设置
     * @Author yjs
     * @Description 跳转到标签设置页面
     * @Date 14:36 2018/10/30
     * @Param []
     **/
    @RequestMapping("/setTip")
    public String setTip() {
        return "/doc/manager/docmanager/setTip.html";
    }

    /**
     * 动态加载文件树
     */
    @RequestMapping(value = "/getMoveTreeDataLazy")
    @ResponseBody
    public ApiResponse getMoveTreeDataLazy(String id, String type) {
        String userId = jwtUtil.getSysUsers().getUserId();
        type = "1";
        List resultList = new ArrayList();
        FsFolderParams fsFolderParams = new FsFolderParams();

        String deptId = iSysUsersService.getById(userId).getDepartmentId();
        String orgId = sysStruMapper.selectById(deptId).getOrganAlias();
        List<String> listGroup = docGroupService.getPremission(userId);
        List<String> roleList = sysUserRoleService.getRolesByUserId(userId);

        Integer adminFlag = CommonUtil.getAdminFlag(roleList);

        fsFolderParams.setAdminFlag(adminFlag);
        fsFolderParams.setGroupList(listGroup);
        fsFolderParams.setUserId(userId);
        fsFolderParams.setRoleList(roleList);
        fsFolderParams.setType("2");
        fsFolderParams.setRoleList(roleList);

        String levelCodeString = businessService.getLevelCodeByUserClient(fsFolderParams, orgId);
        fsFolderParams.setLevelCodeString(levelCodeString);
        if ("#".equals(id) || "".equals(id)) {
            String idParam = "root";
            List<String> firstList = new ArrayList<>();
            List<String> secondList = new ArrayList<>();
            fsFolderParams.setId(idParam);
            fsFolderParams.setRoleList(roleList);
            fsFolderParams.setType(DocConstant.OPERATETYPE.FRONT.getValue());
            List<FsFolder> list = fsFolderService.getTreeDataLazy(fsFolderParams);

            for (int i = 0; i < list.size(); i++) {
                FsFolder fsFile = list.get(i);
                firstList.add(fsFile.getFolderId());
            }
            List<FsFolder> childList = fsFolderService.getChildList(firstList, listGroup, userId, adminFlag, type, levelCodeString);

            for (int i = 0; i < childList.size(); i++) {
                FsFolder fsFile = childList.get(i);
                secondList.add(fsFile.getFolderId());
            }
            List<Map> childCountList = fsFolderService.getChildCountList(secondList, listGroup, userId, adminFlag, type, levelCodeString);
            List<Map> childResultList = clientFilesService.checkChildCount(childList, childCountList);

            for (int i = 0; i < list.size(); i++) {
                Map parentMap = new HashMap();
                FsFolder fsFile = list.get(i);
                parentMap.put("id", fsFile.getFolderId());
                parentMap.put("text", fsFile.getFolderName());
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
            fsFolderParams.setRoleList(roleList);
            fsFolderParams.setId(id);
            fsFolderParams.setType(type);
            List<FsFolder> list = fsFolderService.getTreeDataLazy(fsFolderParams);
            for (int i = 0; i < list.size(); i++) {
                FsFolder fsFile = list.get(i);
                firstList.add(fsFile.getFolderId());
            }

            List<Map> childCountList = fsFolderService.getChildCountList(firstList, listGroup, userId, adminFlag, type, levelCodeString);
            resultList = clientFilesService.checkChildCount(list, childCountList);
        }
        return ApiResponse.data(200, resultList, "");
    }

    /**
     * 移动文件夹及文件
     *
     * @return boolean
     * @author: ChenXin
     */
    @RequestMapping("/moveFolderAndFile")
    @ResponseBody
    public ApiResponse moveFolderAndFile(String fileIds, String folderIds, String folderId, String fileName) {
        if (fileIds != null) {
            String userId = jwtUtil.getSysUsers().getUserId();
            cacheToolService.updateLevelCodeCache(userId);
            String deptId = iSysUsersService.getById(userId).getDepartmentId();

            List<String> listGroup = docGroupService.getPremission(userId);
            List<String> roleList = sysUserRoleService.getRolesByUserId(userId);
            JSONObject json = new JSONObject();
            if (ToolUtil.isEmpty(fileIds)) {
                fileIds = "";
            }
            String[] strFileId = fileIds.split(",");
            String[] strFileName = fileName.split(",");
            Integer adminFlag = CommonUtil.getAdminFlag(roleList);
            for (int i = 0; i < strFileId.length; i++) {
                DocInfo docInfo = docInfoService.getDocDetail(strFileId[i]);
                if (adminFlag != 1) {
                    int isEdits = docFoldAuthorityService.findEditClient(folderId, listGroup, userId, deptId);
                    if (isEdits != 2) {
                        json.put("result", "3");
                        return ApiResponse.data(200, json, "您没有移动到此目录的权限");
                    }
                }
                String fileNameStr = strFileName[i] + docInfo.getDocType();
                if (iDocRecycleService.checkDocExist(folderId, fileNameStr)) {
                    json.put("result", "0");
                    return ApiResponse.data(200, json, "文件已存在");
                } else {
                    if (fsFileService.remove(strFileId[i], folderId,userId)) {
                        FsFolder parentFolder = fsFolderService.getById(folderId);
                        if (parentFolder != null && parentFolder.getOwnId() != null && parentFolder.getOwnId().equals(userId)) {
                            docFileAuthorityService.remove(new QueryWrapper<DocFileAuthority>().eq("file_id", strFileId[i]));
                        }
                        json.put("result", "1");
                    } else {
                        json.put("result", "2");
                        return ApiResponse.data(200, json, "移动失败");
                    }
                }

            }
        }
        if (folderIds != null) {
            String parentLevelCode = fsFolderService.getById(folderId).getLevelCode();
            String userId = jwtUtil.getSysUsers().getUserId();
            cacheToolService.updateLevelCodeCache(userId);
            String[] strArr = folderIds.split(",");
            List<FsFolder> listFs = new ArrayList<>();
            for (String element : strArr) {
                int codeNum = fsFolderService.getChildCodeCount(element);
                int totalCode = parentLevelCode.length() / 4 + codeNum;
                String folderAmount = docConfigService.getConfigValueByKey("folder_amount");
                if (folderAmount == null || Integer.parseInt(folderAmount) < 4) {
                    folderAmount = "4";
                }
                if (totalCode > Integer.parseInt(folderAmount)) {
                    return ApiResponse.data(200, "false", "目录不要超过" + folderAmount + "层");
                }
                FsFolder fsFolder = new FsFolder();
                fsFolder.setFolderId(element);
                fsFolder.setParentFolderId(folderId);

                FsFolder parentFolder = fsFolderService.getById(folderId);
                if (parentFolder != null && parentFolder.getOwnId() != null && parentFolder.getOwnId().equals(userId)) {
                    fsFolder.setOwnId(userId);
                    docFoldAuthorityService.remove(new QueryWrapper<DocFoldAuthority>().eq("folder_id", element));
                } else {
                    fsFolder.setOwnId("");
                }
                Date date = new Date();
                Timestamp ts = new Timestamp(date.getTime());
                fsFolder.setUpdateTime(ts);
                listFs.add(fsFolder);
            }
            fsFolderService.saveOrUpdateBatch(listFs);
            //根节点生成层级码
            fsFolderService.addLevel(folderId);
        }
        return ApiResponse.data(200, "true", "移动成功");
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
        String deptId = iSysUsersService.getById(userId).getDepartmentId();
        String orgId = sysStruMapper.selectById(deptId).getOrganAlias();
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
     * 动态加载文件树
     */
    @RequestMapping(value = "/getMoveTreeDataLazyNew")
    @ResponseBody
    public List getMoveTreeDataLazyNew(String id, String type) {
        List resultList = new ArrayList();
        FsFolderParams fsFolderParams = new FsFolderParams();

        List<String> roleList = UserInfoUtil.getCurrentUser().getRolesList();
        String userId = UserInfoUtil.getCurrentUser().getId();

        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        List<String> listGroup = docGroupService.getPremission(userId);

        fsFolderParams.setAdminFlag(adminFlag);
        fsFolderParams.setGroupList(listGroup);
        fsFolderParams.setUserId(userId);
        fsFolderParams.setRoleList(roleList);
        fsFolderParams.setType("2");

        String levelCodeString = businessService.getLevelCodeByUserRecycle(fsFolderParams);
        fsFolderParams.setLevelCodeString(levelCodeString);
        if ("#".equals(id)) {
            String idParam = "root";
            List<String> firstList = new ArrayList<>();
            List<String> secondList = new ArrayList<>();
            fsFolderParams.setId(idParam);
            fsFolderParams.setRoleList(roleList);
            fsFolderParams.setType(DocConstant.OPERATETYPE.FRONT.getValue());
            List<FsFolder> list = fsFolderService.getTreeDataLazy(fsFolderParams);

            for (int i = 0; i < list.size(); i++) {
                FsFolder fsFile = list.get(i);
                firstList.add(fsFile.getFolderId());
            }
            List<FsFolder> childList = fsFolderService.getChildList(firstList, listGroup, userId, adminFlag, type, levelCodeString);

            for (int i = 0; i < childList.size(); i++) {
                FsFolder fsFile = childList.get(i);
                secondList.add(fsFile.getFolderId());
            }
            List<Map> childCountList = fsFolderService.getChildCountList(secondList, listGroup, userId, adminFlag, type, levelCodeString);
            List<Map> childResultList = fsFolderService.checkChildCount(childList, childCountList);

            for (int i = 0; i < list.size(); i++) {
                Map parentMap = new HashMap();
                FsFolder fsFile = list.get(i);
                parentMap.put("id", fsFile.getFolderId());
                parentMap.put("text", fsFile.getFolderName());
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
            fsFolderParams.setRoleList(roleList);
            fsFolderParams.setId(id);
            fsFolderParams.setType(type);
            List<FsFolder> list = fsFolderService.getTreeDataLazy(fsFolderParams);
            for (int i = 0; i < list.size(); i++) {
                FsFolder fsFile = list.get(i);
                firstList.add(fsFile.getFolderId());
            }

            List<Map> childCountList = fsFolderService.getChildCountList(firstList, listGroup, userId, adminFlag, type, levelCodeString);
            resultList = fsFolderService.checkChildCount(list, childCountList);
        }
        return resultList;
    }


    /**
     * @return 获得标签
     * @Author yjs
     * @Description 跳转到新增标签页面
     * @Date 14:36 2019/6/19
     * @Param []
     **/
    @RequestMapping("/getPreviewType")
    @ResponseBody
    public Map<String, String> getPreviewType(String docId, String suffix) {
        String json = "";
        suffix = suffix.substring(1, suffix.length());
        List<DocConfigure> typeList = docConfigureService.getConfigure();
        if (typeList.get(5).getConfigValue().contains(suffix.toLowerCase())) {
            Map<String, String> resultMap = new HashMap<String, String>();
            resultMap.put("code", "1");
            return resultMap;
        }
        if (typeList.get(6).getConfigValue().contains(suffix.toLowerCase())) {
            Map<String, String> resultMap = new HashMap<String, String>();
            resultMap.put("code", "2");
            return resultMap;
        }
        if (typeList.get(7).getConfigValue().contains(suffix.toLowerCase())) {
            Map<String, String> resultMap = new HashMap<String, String>();
            resultMap.put("code", "3");
            return resultMap;
        }
        if (typeList.get(8).getConfigValue().contains(suffix.toLowerCase())) {
            Map<String, String> resultMap = new HashMap<String, String>();
            resultMap.put("code", "4");
            return resultMap;
        }
        if (suffix.equals("component")) {
            Map<String, String> resultMap = new HashMap<String, String>();
            resultMap.put("code", "10");
            return resultMap;
        } else {
            Map<String, String> resultMap = new HashMap<String, String>();
            resultMap.put("code", "5");
            return resultMap;
        }
    }

    @RequestMapping("/download")
    public void getFile(String docId, HttpServletRequest request, HttpServletResponse response) {
        try {
            String userId = jwtUtil.getSysUsers().getUserId();
            String orgId =   docFoldAuthorityService.getDeptIds(iSysUsersService.getById(userId).getDepartmentId());

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

    /**
     * @return 获得标签
     * @Author yjs
     * @Description 跳转到新增标签页面
     * @Date 14:36 2019/6/19
     * @Param []
     **/
    @RequestMapping("/getFolderDetail")
    @ResponseBody
    public ApiResponse getFolderDetail(String folderId) {
        Map<String, Object> resultMap = new HashMap<>();
        FsFolder fsFolder = fsFolderService.getById(folderId);
        String levelCode = fsFolder.getLevelCode();
        if (fsFolder == null) {
            ApiResponse.fail(500, "目录不存在");
        }
        String userId = fsFolder.getCreateUserId();
        if (userId != null && !userId.equals("")) {
            String userName = sysUsersService.getById(userId).getUserName();
            resultMap.put("userName", userName);
        }
        resultMap.put("folderName", fsFolder.getFolderName());
        resultMap.put("updateTime", fsFolder.getUpdateTime());

        //获得目录总数
        Integer folderAmount = fsFolderService.getChildFolderNum(levelCode);
        Integer fileAmount = fsFolderService.getChildFileNum(levelCode);
        Long fileSizeAmount = fsFolderService.getTotalFileSize(levelCode);
        if(fileSizeAmount == null){
            fileSizeAmount = 0L;
        }
        resultMap.put("folderAmount", folderAmount);
        resultMap.put("fileAmount", fileAmount);
        resultMap.put("fileSizeAmount", FileTool.longToString(fileSizeAmount));
        return ApiResponse.data(200, resultMap, "");
    }
}
