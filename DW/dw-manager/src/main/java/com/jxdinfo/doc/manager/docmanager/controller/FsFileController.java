package com.jxdinfo.doc.manager.docmanager.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jxdinfo.doc.common.constant.DocConstant;
import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.docutil.service.DocConfigService;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.common.util.ESUtil;
import com.jxdinfo.doc.common.util.FileTool;
import com.jxdinfo.doc.common.util.StringUtil;
import com.jxdinfo.doc.common.util.UserInfoUtil;
import com.jxdinfo.doc.manager.docconfigure.model.DocConfigure;
import com.jxdinfo.doc.manager.docconfigure.service.DocConfigureService;
import com.jxdinfo.doc.manager.docmanager.model.DocFileAuthority;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.model.DocResourceLog;
import com.jxdinfo.doc.manager.docmanager.model.FsFile;
import com.jxdinfo.doc.manager.docmanager.model.FsFolderView;
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
import com.jxdinfo.doc.semanticAnalysis.service.SemanticAnalysisService;
import com.jxdinfo.hussar.bsp.organ.dao.SysStruMapper;
import com.jxdinfo.hussar.bsp.organ.model.SysStru;
import com.jxdinfo.hussar.bsp.permit.model.SysUsers;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
import com.jxdinfo.hussar.core.base.controller.BaseController;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.support.HttpKit;
import com.jxdinfo.hussar.core.util.ToolUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 文件系统-资源管理
 *
 * @author smallcat
 * @Date 2018-06-30 14:10:08
 */
@Controller
@RequestMapping("/fsFile")
public class FsFileController extends BaseController {

    private String PREFIX = "/doc/manager/docmanager/";
    @Autowired
    private FsFileService fsFileService;

    @Autowired
    private ISysUsersService sysUsersService;

    @Autowired
    private ESUtil esUtil;
    @Resource
    private SysStruMapper sysStruMapper;

    /**
     * 回收站 服务类
     */
    @Resource
    private IDocRecycleService iDocRecycleService;

    /** 配置信息服务层 */
    @Resource
    private DocConfigureService docConfigureService;

    @Autowired
    private DocGroupService docGroupService;
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

    @Resource
    private FilesService filesService;

    @Resource
    private SemanticAnalysisService semanticAnalysisService;

    /**
     * 语义分析：收集数据-是否开启
     */
    @Value("${semanticAnalysis.collectionUsing}")
    private String collectionUsing;

    /**
     * 跳转到文件系统-文件首页
     */
    @RequiresPermissions("fsFile:view")
    @GetMapping("/view")
    public String index() {
        return PREFIX + "resourceManager.html";
    }

    /**
     * 权限设置页面
     */
    @GetMapping("/authority")
    public String authority() {
        return PREFIX + "authority.html";
    }

    /**
     * 释放空间页面
     * @return 页面视图
     */
    @RequiresPermissions("fsFile:releaseView")
    @GetMapping("/releaseView")
    public String releaseView(){return PREFIX + "deletedDoc-manager.html";}


    /**
     * 获取根节点
     */
    @PostMapping(value = "/getRoot")
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
     * 获取已删除的文件信息
     * @param pageNumber 当前页数
     * @param pageSize 每页显示条数
     * @param title 文件名，用来模糊查询
     * @return 查询结果
     */
    @RequestMapping("/getDeletedFileList")
    @ResponseBody
    public Map<String,Object> getList(@RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
                                      @RequestParam(value = "pageSize", defaultValue = "20") int pageSize,String title){
        // 封装查询参数
        Map<String,Object> params = new HashMap<>();
        params.put("title",title);
        // 查询已删除的文件数据
        Page<Object> page = new Page<>(pageNumber,pageSize);
        List<Map> deletedFiles = fsFileService.getDeletedFiles(page, params);

        // 更改文件size的格式
        if(ToolUtil.isNotEmpty(deletedFiles)){
            deletedFiles = deletedFiles.stream().peek(item -> {
                if(ToolUtil.isNotEmpty(item.get("fileSize"))){
                    item.put("fileSize", FileTool.longToString(Long.valueOf(item.get("fileSize").toString())/1024));
                }
            }).collect(Collectors.toList());
        }
        // 向前台返回查询结果
        Map<String, Object> result = new HashMap<>();
        result.put("total", page.getTotal());
        result.put("rows", deletedFiles);
        return result;
    }

    /**
     * 删除假删的文档（真删）
     * @param docIds   要删除的文档
     * @return      删除结果（1：成功；0：失败）
     */
    @PostMapping("/delDoc")
    @ResponseBody
    public int delDoc(String docIds){
        // 将字符串转换为List集合
        String[] ids = docIds.split(",");
        // 循环删除
        try{
            return fsFileService.delDoc(ids);
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 新增文件夹
     */
    @PostMapping(value = "/add")
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
    @PostMapping(value = "/editFile")
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
    @PostMapping(value = "/addCheck")
    @ResponseBody
    public String addCheck(String name, String filePid) {
        List list = fsFileService.addCheck(filePid, name);
        if (list.size() > 0) {
            return "false";
        }
        return "true";
    }

    /**
     * 删除文件（级联删除）
     */
    @PostMapping(value = "/delete")
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
    @PostMapping(value = "/deleteScope")
    @ResponseBody
    public int deleteScope(@RequestParam String fsFileIds) {
        String userId = ShiroKit.getUser().getId();
        cacheToolService.updateLevelCodeCache(userId);
        String[] strArr = fsFileIds.split(",");
        List list = new ArrayList();
        list.addAll(Arrays.asList(strArr));
       /* boolean authorityFlag = filesService.checkFileManageAuthority(list);
        if(!authorityFlag){
            return 0;
        }*/
        int num = fsFileService.deleteScope(list);

        for (String id : strArr) {
            Map map = new HashMap(1);
            //讲title置为空 防止搜索联想框搜索出来
            map.put("title","");
            //0为无效，1为有效
            map.put("recycle", DocConstant.VALIDTYPE.INVALID.getValue());
            esUtil.updateIndex(id, map);
        }
        cacheToolService.deleteEmpUsedSpace(userId);
        return num;
    }

    /**
     * 回收权限
     *
     * @param fsFileIds  回收的文件或目录id (多个用,号隔开)
     * @param chooseType 回收权限类型 folder：目录     file:文件
     * @return
     */
    @PostMapping(value = "/backAuth")
    @ResponseBody
    public void backAuth(@RequestParam String fsFileIds, @RequestParam String chooseType) {
        fsFileService.backAuth(fsFileIds, chooseType);
    }

    /**
     * 粘贴时检查是否是下级
     */
    @PostMapping(value = "/checkChild")
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
    @PostMapping(value = "/checkFileType")
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
    @PostMapping("/edit")
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
    @RequestMapping(value = "/getChildren")
    @ResponseBody
    public Object getChildren(@RequestParam String id,
                              @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
                              @RequestParam(value = "pageSize", defaultValue = "60") int pageSize, String order, String name,
                              String type, String nameFlag, String operateType) {
        Map orderMap = new HashMap();
        Map typeMap = new HashMap();
        String  isDesc="0";
        if("1".equals(order)||"3".equals(order)||"5".equals(order)||"7".equals(order)){
            isDesc = "1";
        }
        //排序和查询规则
        orderMap.put("0", "fileName");
        orderMap.put("1", "fileName");
        orderMap.put("2", "createTime");
        orderMap.put("3", "createTime");
        orderMap.put("4", "createUserName");
        orderMap.put("5", "createUserName");
        orderMap.put("6", "SUBSTRING_INDEX(fileSize,'k',1)+0");
        orderMap.put("7", "SUBSTRING_INDEX(fileSize,'k',1)+0");
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
        String userId = ShiroKit.getUser().getId();
        /// 用户所在群组列表
        List<String> listGroup = docGroupService.getPremission(userId);
        List<String> roleList = ShiroKit.getUser().getRolesList();
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
        FsFolder folder=fsFolderService.getById(id);
        FsFolderParams fsFolderParams = new FsFolderParams();
        fsFolderParams.setGroupList(listGroup);
        fsFolderParams.setUserId(userId);
        fsFolderParams.setRoleList(ShiroKit.getUser().getRolesList());
        fsFolderParams.setType("front");
        fsFolderParams.setLevelCodeString(folder.getLevelCode());
        fsFolderParams.setId(id);
//        List<String> levelCodeList = folderService.getlevelCodeList(listGroup, userId, type);
        // 所有权限
        String levelCodeString = businessService.getFileLevelCodeFront(fsFolderParams);
        //获得目录管理权限层级码
        fsFolderParams.setRoleList(ShiroKit.getUser().getRolesList());
        fsFolderParams.setType("2");

        /// 对目录有管理权限,对子目录和文件有管理权限
        /// 查询有管理权限的目录levelCode
        String levelCode = businessService.getLevelCodeByUserUpload(fsFolderParams);
        //获得下一级文件和目录

        String orgId = docFoldAuthorityService.getDeptIds(ShiroKit.getUser().getDeptId());
        list = fsFolderService.getFilesAndFloder((pageNumber - 1) * pageSize, pageSize, id, typeArr, name,
                orderResult, listGroup, userId, adminFlag, operateType, levelCodeString, levelCode,isDesc,orgId,roleList);
        list = changeSize(list);

        //获得下一级文件和目录数量
        num = fsFolderService.getFilesAndFloderNum(id, typeArr, name, orderResult, listGroup, userId,
                adminFlag, operateType, levelCodeString, levelCode,orgId,roleList);
        //显示前台的文件数量
        int amount = fsFolderService.getFileNum(id, typeArr, name, listGroup, userId, adminFlag, operateType, levelCode,orgId,roleList);
        //判断是否有可编辑文件的权限
        if (adminFlag != 1) {
            int isEdits = docFoldAuthorityService.findEditByUpload(id, listGroup, userId);
            result.put("noChildPower", isEdits);
        }
        if (userId.equals(fsFolder.getCreateUserId())) {
            result.put("noChildPower", 2);
        }
        String folderAmount = docConfigService.getConfigValueByKey("folder_amount");
        if(folderAmount ==null||Integer.parseInt(folderAmount)<4){
            folderAmount="4";
        }
        result.put("folderAmount", folderAmount);

        if (adminFlag != 1) {
            int isEdits = docFoldAuthorityService.findEditNew(id, listGroup, userId);
            result.put("noChildPowerFolder", isEdits);
        }
        if (userId.equals(fsFolder.getCreateUserId())) {
            result.put("noChildPowerFolder", 1);
        }
        result.put("userId", ShiroKit.getUser().getId());
        result.put("isAdmin", adminFlag);
        result.put("total", num);
        result.put("rows", list);
        FsFolder fsfolder = new FsFolder();
        fsfolder=fsFolderService.getById(id);
        if(fsfolder.getOwnId()==null||"".equals(fsfolder.getOwnId())){
            result.put("isOwn", "0");
        }else{
            result.put("isOwn", "1");
        }
//        result.put("isChild", isChild);
        result.put("amount", amount);

        return result;
    }

    /**
     * 获取子目录
     */
    @PostMapping(value = "/getChildreTable")
    @ResponseBody
    public Object getChildrenTable(@RequestParam String id,
                                   @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
                                   @RequestParam(value = "pageSize", defaultValue = "60") int pageSize, String order, String name,
                                   String type, String nameFlag) {
        //List<FsFile> list=fsFileService.getChildren(id);
        boolean isChild = fsFileService.isChildren(id);
        Map orderMap = new HashMap();
        Map typeMap = new HashMap();
        orderMap.put("0", "create_time");
        orderMap.put("1", "file_name");
        orderMap.put("2", "file_type");
        typeMap.put("1", ".doc,.docx");
        typeMap.put("2", ".ppt,.pptx");
        typeMap.put("3", ".txt");
        typeMap.put("4", ".pdf");
        typeMap.put("5", ".xls,.xlsx");
        String orderResult = (String) orderMap.get(order);
        Map<String, Object> result = new HashMap<>(5);
        List<FsFile> list = new ArrayList<>();
        int num = 0;
        String userId = ShiroKit.getUser().getId();
        List<String> listGroup = docGroupService.getPremission(userId);
        List<String> roleList = ShiroKit.getUser().getRolesList();
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        if ("0".equals(type)) {
            if (nameFlag == null || "".equals(nameFlag)) {
                list = fsFileService.getChildren((pageNumber - 1) * pageSize, pageSize, id, null, null, orderResult, listGroup, userId, adminFlag);
                num = fsFileService.getNum(id, null, null, listGroup, userId, adminFlag);
            } else {
                list = fsFileService.getChildren((pageNumber - 1) * pageSize, pageSize, id, null, name, orderResult, listGroup, userId, adminFlag);
                num = fsFileService.getNum(id, null, name, listGroup, userId, adminFlag);
            }
        } else {
            String typeResult = (String) typeMap.get(type);
            String[] typeArr = typeResult.split(",");
            if (nameFlag == null || "".equals(nameFlag)) {
                list = fsFileService.getChildren((pageNumber - 1) * pageSize, pageSize, id, typeArr, null, orderResult, listGroup, userId, adminFlag);
                num = fsFileService.getNum(id, typeArr, null, listGroup, userId, adminFlag);
            } else {
                list = fsFileService.getChildren((pageNumber - 1) * pageSize, pageSize, id, typeArr, name, orderResult, listGroup, userId, adminFlag);
                num = fsFileService.getNum(id, typeArr, name, listGroup, userId, adminFlag);
            }
        }
        result.put("userId", ShiroKit.getUser().getName());
        result.put("isAdmin", adminFlag);
        result.put("total", num);
        result.put("rows", list);
        result.put("isChild", isChild);
        return result;
    }

    /**
     * @title: 查看文件信息
     * @description: 查看文件信息
     * @date: 2018-8-12.
     * @author: yjs
     */
    @PostMapping(value = "/getInfo")
    @ResponseBody
    public List<Map> getInfo(@RequestParam String ids) {
        String[] strArr = ids.split(",");
        List<String> idList = new ArrayList<String>();
        for (String element : strArr) {
            idList.add(element);
        }
        String userId = UserInfoUtil.getCurrentUser().getId();
        List<String> listGroup = docGroupService.getPremission(userId);
        String orgId = docFoldAuthorityService.getDeptIds(ShiroKit.getUser().getDeptId());
        List<Map> list = fsFileService.getInfo(idList,userId,listGroup,null,orgId,ShiroKit.getUser().getRolesList());
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
    @PostMapping(value = "/update")
    @ResponseBody
    public String update(String ids, String filePid, String fileName, String type) {
        String[] fileIdArr = ids.split(",");
        List fileIdList = new ArrayList();
        fileIdList.addAll(Arrays.asList(fileIdArr));
      /*  boolean authorityFlag = filesService.checkFileManageAuthority(fileIdList);
        if(!authorityFlag){
            return "false";
        }*/
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
                DocInfo  docInfo= docInfoService.getById(docId);
                Timestamp ts = new Timestamp(System.currentTimeMillis());
                docInfo.setUpdateTime(ts);
                docInfo.setTitle(fileName);
                docInfoService.updateById(docInfo);
                FsFile fsFile=fsFileService.getById(docId);
                fsFile.setFileName(fileName);
                fsFileService.updateById(fsFile);
                Map<String, Object> map = new HashMap<String, Object>(16);
                map.put("title", fileName);
                esUtil.updateIndex(docId, map);
                List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
                DocResourceLog docResourceLog = new DocResourceLog();
                String id = UUID.randomUUID().toString().replace("-", "");
                docResourceLog.setId(id);
                docResourceLog.setResourceId(docId);
                docResourceLog.setOperateTime(ts);
                docResourceLog.setResourceType(0);
                docResourceLog.setUserId(ShiroKit.getUser().getId());
                docResourceLog.setOperateType(8);
                docResourceLog.setAddressIp(HttpKit.getIp());
                docResourceLog.setOrigin("client");
                resInfoList.add(docResourceLog);
                docInfoService.insertResourceLog(resInfoList);
            }
        }
        return "success";
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
    @PostMapping(value = "/getDocId")
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
    @PostMapping(value = "/checkName")
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
    @GetMapping(value = "/getTreeDataLazy")
    @ResponseBody
    public List getTreeDataLazy(String id, String type) {
        List resultList = new ArrayList();
        FsFolderParams fsFolderParams = new FsFolderParams();
        List<String> roleList = UserInfoUtil.getCurrentUser().getRolesList();
        String userId = UserInfoUtil.getCurrentUser().getId();
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        List<String> listGroup = docGroupService.getPremission(userId);
        FsFolder folder=new FsFolder();
        if ("#".equals(id)) {
            String fid="2bb61cdb2b3c11e8aacf429ff4208431";
             folder=fsFolderService.getById(fid);
        }else{
             folder=fsFolderService.getById(id);
        }
        fsFolderParams.setAdminFlag(adminFlag);
        fsFolderParams.setGroupList(listGroup);
        fsFolderParams.setUserId(userId);
        fsFolderParams.setType(type);
        fsFolderParams.setRoleList(ShiroKit.getUser().getRolesList());
        fsFolderParams.setLevelCodeString(folder.getLevelCode());
        fsFolderParams.setId(id);


        String levelCodeString = businessService.getFileLevelCode(fsFolderParams);
        fsFolderParams.setLevelCodeString(levelCodeString);
        if ("#".equals(id)) {
            String idParam = "root";
            List<String> firstList = new ArrayList<>();
            List<String> secondList = new ArrayList<>();
            fsFolderParams.setId(idParam);
            fsFolderParams.setRoleList(ShiroKit.getUser().getRolesList());
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
            fsFolderParams.setRoleList(ShiroKit.getUser().getRolesList());
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
     * 跳转到上传文件页面
     */
    @GetMapping("/upload.do")
    public String upload() {
        return PREFIX + "upload.html";
    }

    /**
     * 缓存专题查看数据数据
     *
     * @author xubin
     * @date 2018-07-10 9:04
     */
    @PostMapping("/cacheViewNum")
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
        String userId = UserInfoUtil.getUserInfo().get("ID").toString();
        docResourceLog.setUserId(userId);
        docResourceLog.setOperateType(3);
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
    @PostMapping("/searchLevel")
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
    @PostMapping("/getFsFolderDetail")
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
    @PostMapping("/getFsFileDetail")
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
    @PostMapping("/getPersonList")
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
    @PostMapping("/downloadAble")
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
    @PostMapping("/getAuthority")
    @ResponseBody
    public List getAuthority(String fileId) {
        DocInfo docInfo = docInfoService.getDocDetail(fileId);
        if (docInfo != null) {
            String authorId = docInfo.getAuthorId();
            if (authorId != null) {
                SysUsers user = sysUsersService.getById(authorId);

            }
        }
        List list = fsFileService.getAuthority(fileId);
        return list;
    }

    /**
     * 修改目录权限
     *
     * @return
     */
    @PostMapping("/editAuthority")
    @ResponseBody
    public boolean editAuthority(String fileId, String role, String group, String person, String authorId, String personOrgan, String authorTypeRole,String authorTypeGroup,String authorTypePerson,String operateTypeRole,String operateTypeGroup,String operateTypePerson, String authType
    ) {
        List<String> idList = new ArrayList<String>();
        idList.add(fileId);
       /* boolean authorityFlag = filesService.checkFileManageAuthority(idList);
        if(!authorityFlag){
            return false;
        }*/
        String userId = ShiroKit.getUser().getId();
        cacheToolService.updateLevelCodeCache(userId);
        Date date = new Date();
        DocInfo docInfo = new DocInfo();
        Timestamp ts = new Timestamp(date.getTime());
        docInfo.setUpdateTime(ts);
        docInfo.setDocId(fileId);
        docInfo.setSetAuthority("0");
        docInfoService.updateById(docInfo);
//        Map<String, Object> map = new HashMap<String, Object>(16);
//        map.put("title", docInfo.getTitle());
//        esUtil.updateIndex(fileId, map);
        String delAuth = "";
        if (group.length() > 0 && person.length() > 0) {
            delAuth = group + "," + person;
        } else {
            delAuth = group + person;
        }
        if (role.length() > 0 && delAuth.length() > 0) {
            delAuth = delAuth + "," + role;
        } else {
            delAuth = delAuth + role;
        }
        if (!"add".equals(authType)) {
            docFileAuthorityService.remove(new QueryWrapper<DocFileAuthority>().eq("file_id", fileId));
        } else {
            if (!"".equals(delAuth)) {
                docFileAuthorityService.remove(new QueryWrapper<DocFileAuthority>().eq("file_id", fileId).in("author_id", delAuth));
            }
        }
        List<String> indexList = new ArrayList<>();
        List<DocFileAuthority> list = new ArrayList<>();
        if (role != null && !("".equals(role))) {
            String[] roleArr = role.split(",");
            String[] authorTypeStrRole = authorTypeRole.split(",");
            String[] operateTypeStrRole =operateTypeRole.split(",");
            for (int i = 0; i < roleArr.length; i++) {
                DocFileAuthority docFileAuthority = new DocFileAuthority();
                docFileAuthority.setFileAuthorityId(UUID.randomUUID().toString().replaceAll("-", ""));
                docFileAuthority.setAuthorId(roleArr[i]);
                //操作者类型（0：userID,1:groupID,2:roleID）
                docFileAuthority.setAuthorType(StringUtil.getInteger(authorTypeStrRole[i]));
                docFileAuthority.setFileId(fileId);
                docFileAuthority.setAuthority(StringUtil.getInteger(operateTypeStrRole[i]));
                list.add(docFileAuthority);
                indexList.add(roleArr[i]);
            }
        }
        if (group != null && !("".equals(group))) {
            String[] groupArr = group.split(",");
            String[] authorTypeStrGroup = authorTypeGroup.split(",");
            String[] operateTypeStrGroup =operateTypeGroup.split(",");
            for (int i = 0; i < groupArr.length; i++) {
                DocFileAuthority docFileAuthority = new DocFileAuthority();
                docFileAuthority.setFileAuthorityId(UUID.randomUUID().toString().replaceAll("-", ""));
                docFileAuthority.setAuthorId(groupArr[i]);
                //操作者类型（0：userID,1:groupID,2:roleID）
                docFileAuthority.setAuthorType(StringUtil.getInteger(authorTypeStrGroup[i]));
                docFileAuthority.setFileId(fileId);
                    docFileAuthority.setAuthority(StringUtil.getInteger(operateTypeStrGroup[i]));
                list.add(docFileAuthority);
                indexList.add(groupArr[i]);
            }
        }
        if (person != null && !("".equals(person))) {
            String[] personArr = person.split(",");
            String[] personOrganArr = personOrgan.split(",");
            String[] authorTypeStrPerson = authorTypePerson.split(",");
            String[] operateTypeStrPerson = operateTypePerson.split(",");
            for (int i = 0; i < personArr.length; i++) {
                DocFileAuthority docFileAuthority = new DocFileAuthority();
                docFileAuthority.setFileAuthorityId(UUID.randomUUID().toString().replaceAll("-", ""));
                if(authorTypeStrPerson[i].equals("2")){
                    SysStru sysStru=sysStruMapper.selectById(personArr[i]);
                    if(sysStru==null){
                        docFileAuthority.setOrganId(personOrganArr[i]);
                        docFileAuthority.setAuthorId(personArr[i]);
                    }else{
                        docFileAuthority.setOrganId(sysStru.getStruId());
                        docFileAuthority.setAuthorId(sysStru.getOrganAlias());
                    }
                }else {
                    docFileAuthority.setAuthorId(personArr[i]);
                }
                //操作者类型（0：userID,1:groupID,2:roleID）
                docFileAuthority.setAuthorType(StringUtil.getInteger(authorTypeStrPerson[i]));
                docFileAuthority.setFileId(fileId);
                docFileAuthority.setAuthority(Integer.parseInt(operateTypeStrPerson[i]));
                list.add(docFileAuthority);
                if(StringUtil.getInteger(authorTypeStrPerson[i])==0){
                    indexList.add(personArr[i]);
                }
                if(StringUtil.getInteger(authorTypeStrPerson[i])==2){
                    indexList.add(personOrganArr[i]);
                }


            }

        }
        indexList.add(authorId);
        Map map = new HashMap(1);
        //0为无效，1为有效
        map.put("permission", indexList.toArray(new String[indexList.size()]));
        esUtil.updateIndex(fileId, map);
        boolean flag = true;
        if (list.size() > 0) {
            flag = docFileAuthorityService.saveBatch(list);
        }
        //保存权限信息
        List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
        DocResourceLog docResourceLog = new DocResourceLog();
        String id = UUID.randomUUID().toString().replace("-", "");
        docResourceLog.setId(id);
        docResourceLog.setResourceId(fileId);
        docResourceLog.setOperateTime(ts);
        docResourceLog.setResourceType(0);
        docResourceLog.setUserId(userId);
        docResourceLog.setOperateType(21);
        docResourceLog.setAddressIp(HttpKit.getIp());
        docResourceLog.setValidFlag("1");

        resInfoList.add(docResourceLog);
        docInfoService.insertResourceLog(resInfoList);   //插入预览记录
        return flag;
    }


    /**
     * 转化文件大小的方法
     */
    public List<FsFolderView> changeSize(List<FsFolderView> list) {
        for (FsFolderView fsFolderView : list) {
            if (fsFolderView.getFileSize() != null && !"".equals(fsFolderView.getFileSize())) {
                fsFolderView.setFileSize(FileTool.longToString(fsFolderView.getFileSize()));
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
    @GetMapping("/fileAuthority")
    public String fileAuthority(Model model) {
        String authType = super.getPara("authType");
        if (!"add".equals(authType)) {
            authType = "set";
        }
        model.addAttribute("authType", authType);
        return "/doc/manager/docmanager/fileAuthority.html";
    }

    /**
     * @return 新增标签
     * @Author yjs
     * @Description 跳转到新增标签页面
     * @Date 14:36 2018/10/30
     * @Param []
     **/
    @PostMapping("/addtip")
    @ResponseBody
    public JSON addtip(String docId, String tip) {
        List<String> idList = new ArrayList<String>();
        idList.add(docId);
      /*  boolean authorityFlag = filesService.checkFileManageAuthority(idList);
        if(!authorityFlag){
            JSONObject json = new JSONObject();
            json.put("success",false);
            return json;
        }*/
        DocInfo docInfo = new DocInfo();
        docInfo.setDocId(docId);
        docInfo.setTags(tip);
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        docInfo.setUpdateTime(ts);
        JSONObject json = new JSONObject();
        boolean success=docInfoService.updateById(docInfo);
        Map map = new HashMap(1);
        //0为无效，1为有效
        map.put("tags",tip );
        esUtil.updateIndex(docId, map);
        json.put("success",success);

        if("true".equals(collectionUsing)) {
            // 根据文件id查询文件信息
            FsFile fsFileTemp =  fsFileService.getById(docId);
            String fileType = fsFileTemp.getFileType();
            String fileName = fsFileTemp.getFileName() + fileType;
            String path = null;
            if (".txt".equals(fileType)) {
                // 下载文件并解密
                path = filesService.downloadFile(fsFileTemp);
            } else {
                // 转为txt文件
                path = filesService.changeToTxt(docId);
            }
            if (path != null) {
                // 提供语义分析数据
                semanticAnalysisService.uploadToAnalyse(fileName, path, tip);
            }
        }

        return  json;
    }

    /**
     * 设置文档是否可分享
     * @param docId 文档ID
     * @param shareFlag 是否可分享(0:不可  1:可分享)
     * @return  true设置成功/false设置失败
     */
    @PostMapping("/setShareFlag")
    @ResponseBody
    public String setShareFlag(String docId,String shareFlag){
        List<String> idList = new ArrayList<String>();
        idList.add(docId);
       /* boolean authorityFlag = filesService.checkFileManageAuthority(idList);
        if(!authorityFlag){
            return "false";
        }*/
        DocInfo docInfo = new DocInfo();
        docInfo.setDocId(docId);
        docInfo.setShareFlag(shareFlag);
        return docInfoService.updateById(docInfo) + "";
    }
    /**
     * 批量设置文档是否可分享
     * @param docIds 文档ID
     * @param shareFlag 是否可分享(0:不可  1:可分享)
     * @return  true设置成功/false设置失败
     */
    @PostMapping("/setShareFlags")
    @ResponseBody
    public String setShareFlags(String docIds,String shareFlag){
        List<String> list = Arrays.asList(docIds.split(","));
        List<DocInfo> docInfos = new ArrayList<>();
        for (String i : list){
            DocInfo docInfo = new DocInfo();
            docInfo.setShareFlag(shareFlag);
            docInfo.setDocId(i);
            docInfos.add(docInfo);
        }
        return docInfoService.updateBatchById(docInfos) + "";
    }
    @PostMapping("/setFileOpen")
    @ResponseBody
    public String setFileOpen(String docIds,String fileOpen){
        List<String> list = Arrays.asList(docIds.split(","));
        List<DocInfo> docInfos = new ArrayList<>();
        Map map = new HashMap(1);
        Integer count = 0;
        List<String> indexList = new ArrayList<>();
        for (String i : list){
            docFileAuthorityService.remove(new QueryWrapper<DocFileAuthority>().eq("file_id", i));
            if(fileOpen!=null&&!fileOpen.equals("0")){
            DocFileAuthority docFileAuthority = new DocFileAuthority();
            docFileAuthority.setFileAuthorityId(UUID.randomUUID().toString().replaceAll("-", ""));
            docFileAuthority.setAuthorId("allpersonflag");
            //操作者类型（0：userID,1:groupID,2:roleID）
            docFileAuthority.setAuthorType(3);
            docFileAuthority.setFileId(i);
            docFileAuthority.setAuthority(0);
            docFileAuthorityService.save(docFileAuthority);
            count++;
                indexList.add("allpersonflag");
                //0为无效，1为有效
                map.put("permission", indexList.toArray(new String[indexList.size()]));
                esUtil.updateIndex(i, map);
            }
            indexList.add(ShiroKit.getUser().getId());
            //0为无效，1为有效
            map.put("permission", indexList.toArray(new String[indexList.size()]));
            esUtil.updateIndex(i, map);

        }
        return count + "";
    }
    @GetMapping("/shareFlagView")
    public String shareFlagView(String docId,String docIds, Model model){
        if (docId != null) {
            DocInfo docInfo = docInfoService.getDocDetail(docId);
            String shareFlag = docInfo.getShareFlag() == null ? "0" : docInfo.getShareFlag();
            model.addAttribute("shareFlag", shareFlag);
            model.addAttribute("docId", docId);
            model.addAttribute("fileName", docInfo.getTitle());
            model.addAttribute("docIds",-1);
        }else {
            model.addAttribute("shareFlag", 0);
            model.addAttribute("docId", -1);
            model.addAttribute("docIds",docIds);
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
    @PostMapping("/gettip")
    @ResponseBody
    public JSON gettip(String docId) {
        DocInfo docInfo = docInfoService.getDocDetail(docId);
        JSONObject json = new JSONObject();
        if(docInfo.getTags()!=null){
        json.put("tip",docInfo.getTags());
        }
        return json;

    }



    /**
     * @return 标签设置
     * @Author yjs
     * @Description 跳转到标签设置页面
     * @Date 14:36 2018/10/30
     * @Param []
     **/
    @GetMapping("/setTip")
    public String setTip() {
        return "/doc/manager/docmanager/setTip.html";
    }
    /**
     * 动态加载文件树
     */
    @GetMapping(value = "/getMoveTreeDataLazy")
    @ResponseBody
    public List getMoveTreeDataLazy(String id, String type) {
        List resultList = new ArrayList();
        FsFolderParams fsFolderParams = new FsFolderParams();

        List<String> roleList = UserInfoUtil.getCurrentUser().getRolesList();
        String userId = UserInfoUtil.getCurrentUser().getId();

        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        List<String> listGroup = docGroupService.getPremission(userId);

        fsFolderParams.setAdminFlag(adminFlag);
        fsFolderParams.setGroupList(listGroup);
        fsFolderParams.setUserId(userId);
        fsFolderParams.setType("2");
        fsFolderParams.setRoleList(ShiroKit.getUser().getRolesList());
        String levelCodeString = businessService.getLevelCodeByUser(fsFolderParams);
        fsFolderParams.setLevelCodeString(levelCodeString);
        if ("#".equals(id)) {
            String idParam = "root";
            List<String> firstList = new ArrayList<>();
            List<String> secondList = new ArrayList<>();
            fsFolderParams.setId(idParam);
            fsFolderParams.setRoleList(ShiroKit.getUser().getRolesList());
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
                String createUserId = fsFile.getCreateUserId();
                String authorName =sysUsersService.getById(createUserId).getUserName();
                parentMap.put("authorName", authorName);
                parentMap.put("createTime", fsFile.getCreateTime());
                parentMap.put("children", childMapList);
                parentMap.put("opened", true);
                resultList.add(parentMap);
            }
        } else {
            List<String> firstList = new ArrayList<>();
            fsFolderParams.setRoleList(ShiroKit.getUser().getRolesList());
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
     * 移动
     *
     * @return boolean
     * @author: ChenXin
     */
    @PostMapping("/move")
    @ResponseBody
    public JSON move() {
        String userId = ShiroKit.getUser().getId();
        cacheToolService.updateLevelCodeCache(userId);
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
        //判断是否为子级目录（只能在子文件夹上传文件）
        boolean isChild = fsFileService.isChildren(folderId);
        String fileName = super.getPara("fileName");
        String[] strFileId = fileId.split(",");
        String[] strFileName  = fileName.split(",");
        for(int i = 0 ; i< strFileId.length ; i++){
            DocInfo docInfo = docInfoService.getDocDetail(strFileId[i]);
            if (adminFlag != 1) {
                int isEdits = docFoldAuthorityService.findEdit(folderId, listGroup, userId);
                if (isEdits !=2) {
                    json.put("result", "3");
                    return json;
                }
            }
            String fileNameStr = strFileName[i] + docInfo.getDocType();
            if (iDocRecycleService.checkDocExist(folderId, fileNameStr)) {
                json.put("result", "0");
                return  json;
            } else if (iDocRecycleService.checkAuditDocExist(folderId, fileNameStr)) {
                json.put("result", "4");
                return  json;
            } else {
                    if (fsFileService.remove(strFileId[i], folderId,userId)) {
                        json.put("result", "1");
                        FsFolder parentFolder = fsFolderService.getById(folderId);
                        if(parentFolder!=null&&parentFolder.getOwnId()!=null&&parentFolder.getOwnId().equals(userId)){
                            docFileAuthorityService.remove(new QueryWrapper<DocFileAuthority>().eq("file_id", strFileId[i]));
                        }
                        Map map = new HashMap(1);
                        //0为无效，1为有效
                        map.put("folderId", folderId);
                        esUtil.updateIndex(strFileId[i], map);

                    } else {
                        json.put("result", "2");

                    }
                }

        }
        return json;
    }

    /**
     * 动态加载文件树
     */
    @GetMapping(value = "/getMoveTreeDataLazyNew")
    @ResponseBody
    public List getMoveTreeDataLazyNew(String id, String type) {
        List resultList = new ArrayList();
        FsFolderParams fsFolderParams = new FsFolderParams();

        List<String> roleList = UserInfoUtil.getCurrentUser().getRolesList();
        String userId = UserInfoUtil.getCurrentUser().getId();

        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        List<String> listGroup = docGroupService.getPremission(userId);
        fsFolderParams.setRoleList(ShiroKit.getUser().getRolesList());
        fsFolderParams.setAdminFlag(adminFlag);
        fsFolderParams.setGroupList(listGroup);
        fsFolderParams.setUserId(userId);
        fsFolderParams.setType("2");

        String levelCodeString = businessService.getLevelCodeByUserRecycle(fsFolderParams);
        fsFolderParams.setLevelCodeString(levelCodeString);
        if ("#".equals(id)) {
            String idParam = "root";
            fsFolderParams.setRoleList(ShiroKit.getUser().getRolesList());
            List<String> firstList = new ArrayList<>();
            List<String> secondList = new ArrayList<>();
            fsFolderParams.setId(idParam);
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
            fsFolderParams.setRoleList(ShiroKit.getUser().getRolesList());
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
    @PostMapping("/getPreviewType")
    @ResponseBody
    public Map<String, String>  getPreviewType(String docId, String suffix) {
        if("c".equals(suffix)) {
            Map<String, String> resultMap = new HashMap<String, String>();
            resultMap.put("code","5");
            return resultMap;
        }
        String json= "";
        suffix = suffix.substring(1,suffix.length());
        List<DocConfigure> typeList =  docConfigureService.getConfigure();
        if(typeList.get(5).getConfigValue().contains(suffix.toLowerCase())){
            Map<String, String> resultMap = new HashMap<String, String>();
            resultMap.put("code","1");
            return resultMap;
        } if(typeList.get(6).getConfigValue().contains(suffix.toLowerCase())){
            Map<String, String> resultMap = new HashMap<String, String>();
            resultMap.put("code","2");
            return resultMap;
        }
        if(typeList.get(7).getConfigValue().contains(suffix.toLowerCase())){
            Map<String, String> resultMap = new HashMap<String, String>();
            resultMap.put("code","3");
            return resultMap;
        }
        if(typeList.get(8).getConfigValue().contains(suffix.toLowerCase())){
            Map<String, String> resultMap = new HashMap<String, String>();
            resultMap.put("code","4");
            return resultMap;
        } if(suffix.equals("component")){
            Map<String, String> resultMap = new HashMap<String, String>();
            resultMap.put("code","10");
            return resultMap;
        }else{
            Map<String, String> resultMap = new HashMap<String, String>();
            resultMap.put("code","5");
            return resultMap;
        }
    }

}
