package com.jxdinfo.doc.client.foldermanager.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxdinfo.doc.client.foldermanager.service.IClientFsFolderService;
import com.jxdinfo.doc.client.response.ApiResponse;
import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.docutil.service.DocConfigService;
import com.jxdinfo.doc.common.jwt.util.JWTUtil;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.manager.docmanager.model.DocResourceLog;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.foldermanager.model.DocFoldAuthority;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.foldermanager.service.IDocFoldAuthorityService;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import com.jxdinfo.doc.manager.groupmanager.service.DocGroupService;
import com.jxdinfo.hussar.bsp.organ.dao.SysStruMapper;
import com.jxdinfo.hussar.bsp.permit.service.ISysIdtableService;
import com.jxdinfo.hussar.bsp.permit.service.ISysUserRoleService;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
import com.jxdinfo.hussar.core.base.controller.BaseController;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.support.HttpKit;
import com.jxdinfo.hussar.core.util.ToolUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.*;

/**
 * 目录管理控制器。
 *
 * @author zf
 * @date 2018-09-06 15:01:50
 */
@Controller
@RequestMapping("/client/folder")
public class FolderController extends BaseController {

    @Resource
    private JWTUtil jwtUtil;

    /**
     * 文档信息
     */
    @Autowired
     private DocInfoService docInfoService;
     /**
     * 目录管理服务类
     */
    @Autowired
    private IFsFolderService fsFolderService;
    @Autowired
    private IClientFsFolderService iClientFsFolderService;

    /**
     * 文档管理服务类
     */
    @Autowired
    private IDocFoldAuthorityService docFoldAuthorityService;

    @Autowired
    private ISysUserRoleService sysUserRoleService;


    @Autowired
    private ISysUsersService iSysUsersService;

    /**
     * 获取编号公共方法
     */
    @Autowired
    private ISysIdtableService sysIdtableService;

    @Autowired
    private DocConfigService docConfigService;
    @Autowired
    private CacheToolService cacheToolService;

    @Resource
    private SysStruMapper sysStruMapper;

    @Autowired
    private DocGroupService docGroupService;


    /**
     * @return 根节点id和名称
     * @Author zoufeng
     * @Description 获取根节点
     * @Date 14:36 2018/9/7
     * @Param []
     **/
    @RequestMapping(value = "/getRoot")
    @ResponseBody
    public Map getRoot() {
        List<FsFolder> list = fsFolderService.getRoot();
        Map<String, String> map = new HashMap<>();
        FsFolder fsFolder = list.get(0);
        map.put("root", fsFolder.getFolderId());
        map.put("rootName", fsFolder.getFolderName());
        return map;
    }

    /**
     * @param folderParams 目录信息及权限信息，祥看FsFolderParams
     * @return java.lang.Object
     * @Author zoufeng
     * @Description 新增文件夹的保存方法
     * @Date 17:53 2018/9/7
     **/
    @RequestMapping(value = "/add")
    @ResponseBody
    public ApiResponse add(FsFolderParams folderParams) {
        String userId = jwtUtil.getSysUsers().getUserId();
        FsFolder fsFolder = new FsFolder();
        cacheToolService.updateLevelCodeCache(userId);
        fsFolder.setFolderId(folderParams.getFolderId());
        fsFolder.setFolderName(folderParams.getFolderName());
        fsFolder.setVisibleRange(folderParams.getVisible());
        fsFolder.setParentFolderId(folderParams.getParentFolderId());
        FsFolder fsFolderParent = new FsFolder();
        fsFolderParent = fsFolderService.getById(folderParams.getParentFolderId());
        String ownId = fsFolderParent.getOwnId();
        String folderId = null;
        if (ownId != null && !"".equals(ownId)) {
            fsFolder.setOwnId(ownId);
        }
        if (ToolUtil.isNotEmpty(folderParams.getFolderId())) {
            fsFolderService.updateFsFolder(fsFolder);
        } else {
            //新增一下主表
            Date date = new Date();
            Timestamp ts = new Timestamp(date.getTime());
            fsFolder.setCreateTime(ts);
            fsFolder.setUpdateTime(ts);
            folderId = UUID.randomUUID().toString().replaceAll("-", "");

            fsFolder.setFolderId(folderId);
            fsFolder.setCreateUserId(userId);
            String folderParentId = fsFolder.getParentFolderId();
            //生成levelCode
            if (folderParentId != null && !"".equals(folderParentId)) {
                FsFolder parentFolder = fsFolderService.getById(folderParentId);
                String parentCode = parentFolder.getLevelCode();
                String currentCode = fsFolderService.getCurrentLevelCode(parentCode, parentFolder.getFolderId());
                fsFolder.setLevelCode(currentCode);
                String localName = "";
                for (int j = 1; j <= currentCode.length() / 4 - 1; j++) {
                    String levelCodeString = currentCode.substring(0, j * 4);
                    String folderName = fsFolderService.getFolderNameByLevelCode(levelCodeString);
                    localName = localName + "\\" + folderName;
                }
                localName = localName + "\\" + fsFolder.getFolderName();
                fsFolder.setFolderPath(localName);
            }
            //生成showOrder
            String currentCode = this.sysIdtableService.getCurrentCode("FOLDER_NUM", "fs_folder");
            int num = Integer.parseInt(currentCode);
            fsFolder.setShowOrder(num);
            folderParams.setFolderId(folderId);
            //保存目录信息
            fsFolderService.save(fsFolder);
            //保存权限信息
            docFoldAuthorityService.saveDocFoldAuthority(folderParams);
            List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();

            DocResourceLog docResourceLog = new DocResourceLog();
            String id = UUID.randomUUID().toString().replace("-", "");
            docResourceLog.setId(id);
            docResourceLog.setResourceId(folderId);
            docResourceLog.setOperateTime(ts);
            docResourceLog.setResourceType(1);
            docResourceLog.setUserId(userId);
            docResourceLog.setOperateType(0);
            docResourceLog.setValidFlag("1");
            docResourceLog.setAddressIp(HttpKit.getIp());
            resInfoList.add(docResourceLog);
            docInfoService.insertResourceLog(resInfoList);   //插入预览记录
        }
        return ApiResponse.data(200, SUCCESS_TIP, folderId);
    }

    /**
     * 新增重名检测
     *
     * @param name           目录名称
     * @param parentFolderId 打开文件的id
     * @return 是否存在重名
     */
    @RequestMapping(value = "/addCheck")
    @ResponseBody
    public ApiResponse addCheck(String name, String parentFolderId, String folderId) {
        List<FsFolder> list = fsFolderService.addCheck(parentFolderId, name, folderId);
        if (list.size() > 0) {
            return ApiResponse.data(200, "false", "");
        }
        return ApiResponse.data(200, "true", "");
    }
    /**
     * 级联删除文件目录
     *
     * @param path 选中目录id
     * @return 删除条数
     */
    @RequestMapping(value = "/deleteFolder")
    @ResponseBody
    public ApiResponse deleteFolder(String path,String id) {
        FsFolder rootFolder = new FsFolder();
        rootFolder =  fsFolderService.getById(id);
        String rootPath = rootFolder.getFolderPath();
        String parentFolderPath = rootPath+path;

        String userId = jwtUtil.getSysUsers().getUserId();
        FsFolder fsFolderParent = new FsFolder();

        fsFolderParent = fsFolderService.getOne(new QueryWrapper<FsFolder>().eq("folder_path", parentFolderPath));
        Map<String, String> map = new HashMap<>();
            cacheToolService.updateLevelCodeCache(userId);
            String[] strArr = fsFolderParent.getFolderId().split(",");
            List<String> list = new ArrayList();
            for (String element : strArr) {
                String ids = fsFolderService.getChildFsFolder(element);
                String[] childArr = ids.split(",");
                list.addAll(Arrays.asList(childArr));
            }
            //删除目录
            fsFolderService.deleteInIds(list);
            docFoldAuthorityService.remove(new QueryWrapper<DocFoldAuthority>().in("folder_id", list));
            map.put("message", "删除成功");
            map.put("code", "success");
        List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        String resId = UUID.randomUUID().toString().replace("-", "");
        DocResourceLog docResourceLog = new DocResourceLog();
        docResourceLog.setId(resId);
        docResourceLog.setResourceId(fsFolderParent.getFolderId());
        docResourceLog.setOperateTime(ts);
        docResourceLog.setResourceType(1);
        docResourceLog.setUserId(userId);
        docResourceLog.setOperateType(2);
        docResourceLog.setValidFlag("1");
        docResourceLog.setAddressIp(HttpKit.getIp());
        resInfoList.add(docResourceLog);
        docInfoService.insertResourceLog(resInfoList);   //插入预览记录
        return ApiResponse.data(200, map, "");
    }
    /**
     * 级联删除文件目录
     *
     * @param fsFolderIds 选中目录id
     * @return 删除条数
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    public ApiResponse delete(@RequestParam String fsFolderIds) {
        Map<String, String> map = new HashMap<>();
        String num = fsFolderService.checkChildType(fsFolderIds);
        int fileNum = Integer.parseInt(num);
        if (fileNum > 0) {
            map.put("message", "请先删除目录下存放的文件");
            map.put("code", "fail");
        } else {
            String userId = jwtUtil.getSysUsers().getUserId();
            cacheToolService.updateLevelCodeCache(userId);
            String[] strArr = fsFolderIds.split(",");
            List<String> list = new ArrayList();
            for (String element : strArr) {
                String ids = fsFolderService.getChildFsFolder(element);
                String[] childArr = ids.split(",");
                list.addAll(Arrays.asList(childArr));
                FsFolder fsFolder = fsFolderService.getById(element);
                List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
                    DocResourceLog docResourceLog = new DocResourceLog();
                    String id = UUID.randomUUID().toString().replace("-", "");
                    Date date = new Date();
                    Timestamp ts = new Timestamp(date.getTime());
                    docResourceLog.setId(id);
                    docResourceLog.setResourceId(element);
                    docResourceLog.setOperateTime(ts);
                    docResourceLog.setResourceType(1);
                    docResourceLog.setUserId(userId);
                    docResourceLog.setOperateType(2);
                    docResourceLog.setValidFlag("1");
                docResourceLog.setAddressIp(HttpKit.getIp());
                    docResourceLog.setDeletePath(fsFolder.getFolderPath());
                    resInfoList.add(docResourceLog);

                    docInfoService.insertResourceLog(resInfoList);   //插入预览记录

            }
            //删除目录
            fsFolderService.deleteInIds(list);
            docFoldAuthorityService.remove(new QueryWrapper<DocFoldAuthority>().in("folder_id", list));
            map.put("message", "删除成功");
            map.put("code", "success");
        }
        return ApiResponse.data(200, map, "");
    }

    /**
     * 粘贴时检查目标目录是否为粘贴内容的子目录
     *
     * @param fsFolderIds 需要粘贴内容的id集合
     * @param id          打开目录的id
     * @return 是否为子目录
     * @author zf
     * @date 2018-09-06
     */
    @RequestMapping(value = "/checkChild")
    @ResponseBody
    public String checkChild(@RequestParam String fsFolderIds, @RequestParam String id) {
        String res = "success";
        String[] strArr = fsFolderIds.split(",");
        for (String element : strArr) {
            String ids = fsFolderService.getChildFsFolder(element);
            String[] childArr = ids.split(",");
            for (int i = 0; i < childArr.length; i++) {
                if (childArr[i].equals(id)) {
                    res = "have";
                }
            }
        }
        return res;
    }

    /**
     * 检查目录下是否存在文件
     *
     * @param ids 选中文件的id
     * @return 是否存在文件
     */
    @RequestMapping(value = "/checkFolderType")
    @ResponseBody
    public Object checkFolderType(@RequestParam String ids) {
        String flag = "success";
        //目录下面的文件数
        String num = fsFolderService.checkChildType(ids);
        int fileNum = Integer.parseInt(num);
        if (fileNum > 0) {
            return "haveFile";
        }
        return flag;
    }

    /**
     * @param fsFolderParams
     * @param nameFlag
     * @return 目录树信息
     * @title: 查询下级目录
     * @description: 查询下级节点（目录）
     * @date: 2018-8-15
     * @author: yjs
     */
    @RequestMapping(value = "/getChildren")
    @ResponseBody
    public Object getChildren(FsFolderParams fsFolderParams, String nameFlag) {
        return fsFolderService.getChildren(fsFolderParams, nameFlag);
    }
    @RequestMapping(value = "/updateName")
    @ResponseBody
    public ApiResponse updateName(String id, String path, String folderName) {
        FsFolder rootFolder = new FsFolder();
        rootFolder =  fsFolderService.getById(id);
        String rootPath = rootFolder.getFolderPath();
        String parentFolderPath = rootPath+path;

        String userId = jwtUtil.getSysUsers().getUserId();
        FsFolder fsFolderParent = new FsFolder();

        fsFolderParent = fsFolderService.getOne(new QueryWrapper<FsFolder>().eq("folder_path", parentFolderPath));
        List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
        DocResourceLog docResourceLog = new DocResourceLog();
        docResourceLog.setBeforeName(fsFolderParent.getFolderName());
        fsFolderParent.setFolderName(folderName);
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        fsFolderParent.setUpdateTime(ts);
        fsFolderService.updateById(fsFolderParent);

        String resId = UUID.randomUUID().toString().replace("-", "");
        docResourceLog.setId(resId);
        docResourceLog.setResourceId(fsFolderParent.getFolderId());
        docResourceLog.setOperateTime(ts);
        docResourceLog.setResourceType(1);
        docResourceLog.setUserId(userId);
        docResourceLog.setOperateType(8);
        docResourceLog.setValidFlag("1");
        docResourceLog.setAddressIp(HttpKit.getIp());
        docResourceLog.setAfterName(folderName);
        resInfoList.add(docResourceLog);
        docInfoService.insertResourceLog(resInfoList);   //插入预览记录
        return ApiResponse.data(200, true, "");
    }
    /**
     * 修改更新文件--只修改名字或粘贴
     *
     * @param ids            修改或粘贴文件的id
     * @param parentFolderId 打开文件的id
     * @param folderName     文件名称
     * @return
     */
    @RequestMapping(value = "/update")
    @ResponseBody
    public ApiResponse update(String ids, String parentFolderId, String folderName) {
        String res = "success";
        String parentLevelCode = fsFolderService.getById(parentFolderId).getLevelCode();
        //粘贴或改名
        if (folderName == null || "".equals(folderName)) {

            String userId = jwtUtil.getSysUsers().getUserId();
            cacheToolService.updateLevelCodeCache(userId);
            String[] strArr = ids.split(",");
            List<FsFolder> listFs = new ArrayList<>();
            for (String element : strArr) {
                int codeNum = fsFolderService.getChildCodeCount(element);
                int totalCode = parentLevelCode.length() / 4 + codeNum;
                String folderAmount = docConfigService.getConfigValueByKey("folder_amount");
                if (folderAmount == null || Integer.parseInt(folderAmount) < 4) {
                    folderAmount = "4";
                }
                if (totalCode > Integer.parseInt(folderAmount)) {
                    res = folderAmount;
                    return ApiResponse.data(200, res, "");
                }
                FsFolder fsFolder = new FsFolder();
                fsFolder.setFolderId(element);
                fsFolder.setParentFolderId(parentFolderId);

                FsFolder parentFolder = fsFolderService.getById(parentFolderId);
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
            fsFolderService.addLevel(parentFolderId);
            //生成路径
            fsFolderService.addPath(parentFolderId);

        } else {
            List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
            FsFolder fsFolder = new FsFolder();
            fsFolder.setFolderId(ids);
            fsFolder.setFolderName(folderName);
            Date date = new Date();
            Timestamp ts = new Timestamp(date.getTime());
            fsFolder.setUpdateTime(ts);
            fsFolderService.updateById(fsFolder);
            DocResourceLog docResourceLog = new DocResourceLog();
            String id = UUID.randomUUID().toString().replace("-", "");
            docResourceLog.setId(id);
            docResourceLog.setResourceId(ids);
            docResourceLog.setOperateTime(ts);
            docResourceLog.setResourceType(1);
            String userId =  jwtUtil.getSysUsers().getUserId();
            docResourceLog.setUserId(userId);
            docResourceLog.setOperateType(8);
            docResourceLog.setValidFlag("1");
            docResourceLog.setAddressIp(HttpKit.getIp());
            resInfoList.add(docResourceLog);
            docInfoService.insertResourceLog(resInfoList);   //插入预览记录
        }
        return ApiResponse.data(200, res, "");
    }

    /**
     * 粘贴重命名检测
     *
     * @param nameStr   目录名称
     * @param folderPid 打开文件夹的id
     * @return 是否存在重名
     */
    @RequestMapping(value = "/checkName")
    @ResponseBody
    public String checkName(String nameStr, String folderPid) {
        String res = "success";
        String[] nameArr = nameStr.split("\\*");
        List<Map> list = new ArrayList<>();
        for (int i = 0; i < nameArr.length; i++) {
            Map<String, String> map = new HashMap<>();
            map.put("name", nameArr[i]);
            list.add(map);
        }
        List listFs = fsFolderService.countFolderName(folderPid, list);
        if (listFs.size() > 0) {
            res = "fail";
        }
        return res;
    }

    @RequestMapping(value = "/checkNameClient")
    @ResponseBody
    public String checkNameClient(String nameStr, String id,String path) {
        FsFolder rootFolder = new FsFolder();
        rootFolder =  fsFolderService.getById(id);
        String rootPath = rootFolder.getFolderPath();
        String parentFolderPath = rootPath+path;

        String userId = jwtUtil.getSysUsers().getUserId();
        FsFolder fsFolderParent = new FsFolder();

        fsFolderParent = fsFolderService.getOne(new QueryWrapper<FsFolder>().eq("folder_path", parentFolderPath));
        String res = "success";
        String[] nameArr = nameStr.split("\\*");
        List<Map> list = new ArrayList<>();
        for (int i = 0; i < nameArr.length; i++) {
            Map<String, String> map = new HashMap<>();
            map.put("name", nameArr[i]);
            list.add(map);
        }
        List listFs = fsFolderService.countFolderName(fsFolderParent.getParentFolderId(), list);
        if (listFs.size() > 0) {
            res = "fail";
        }
        return res;
    }
    /**
     * 动态加载目录树
     *
     * @param id   节点id
     * @param type 前台：0 后台：1
     * @return 返回目录信息
     */
    @RequestMapping(value = "/getTreeDataLazy")
    @ResponseBody
    public ApiResponse getTreeDataLazy(String id, @RequestParam(defaultValue = "2") String type) {
        String userId = jwtUtil.getSysUsers().getUserId();
        type = "2";
        String deptId = iSysUsersService.getById(userId).getDepartmentId();
        String orgId = sysStruMapper.selectById(deptId).getOrganAlias();
        List<String> listGroup = docGroupService.getPremission(userId);
        List<String> roleList = sysUserRoleService.getRolesByUserId(userId);
        return ApiResponse.data(200, iClientFsFolderService.getTreeDataLazyClient(id, type, userId, orgId, listGroup, roleList), "");
    }

    /**
     * @return java.util.List 权限集合
     * @Author zoufeng
     * @Description 查询目录权限
     * @Date 10:26 2018/9/10
     * @Param [folderId] 目录id
     **/
    @RequestMapping("/getAuthority")
    @ResponseBody
    public ApiResponse getAuthority(String folderId) {
        return ApiResponse.data(200, fsFolderService.getAuthority(folderId), "");
    }

    @RequestMapping("/getFolderById")
    @ResponseBody
    public ApiResponse getFolderById(String folderId) {

        FsFolder fsfolder =  fsFolderService.getById(folderId);
        return ApiResponse.data(200,fsfolder.getFolderName(), "");
    }

    /**
     * @return boolean 是否修改成功
     * @Author zoufeng
     * @Description 修改目录结构
     * @Date 10:25 2018/9/10
     * @Param [fsFolderEdit]
     **/
    @RequestMapping("/editAuthority")
    @ResponseBody
    public ApiResponse editAuthority(FsFolderParams fsFolderEdit) {
        String userId = jwtUtil.getSysUsers().getUserId();
        cacheToolService.updateLevelCodeCache(userId);
        String folderIds = fsFolderEdit.getFolderId();
        boolean flag = false;
        Date date = new Date();

        if (folderIds.length() > 0) {
            String[] folderId = folderIds.split(",");
            for (int i = 0; i < folderId.length; i++) {
                FsFolder fsFolder = new FsFolder();

                fsFolder.setUpdateUserId(userId);
                fsFolder.setFolderId(folderId[i]);
                fsFolder.setVisibleRange(fsFolderEdit.getVisible());
                Timestamp ts = new Timestamp(date.getTime());
                fsFolder.setUpdateTime(ts);
                fsFolderEdit.setFolderId(folderId[i]);
                //更新目录信息
                fsFolderService.updateById(fsFolder);
                //删除权限信息
                docFoldAuthorityService.remove(new QueryWrapper<DocFoldAuthority>().eq("folder_id", folderId[i]));
                //保存权限信息
                flag = docFoldAuthorityService.saveDocFoldAuthority(fsFolderEdit);
            }
        } else {
            FsFolder fsFolder = new FsFolder();
            fsFolder.setUpdateUserId(userId);
            fsFolder.setFolderId(fsFolderEdit.getFolderId());
            fsFolder.setFolderName(fsFolderEdit.getFolderName());
            fsFolder.setVisibleRange(fsFolderEdit.getVisible());
            Timestamp ts = new Timestamp(date.getTime());
            fsFolder.setUpdateTime(ts);
            //更新目录信息
            fsFolderService.updateById(fsFolder);
            //删除权限信息
            docFoldAuthorityService.remove(new QueryWrapper<DocFoldAuthority>().eq("folder_id", fsFolderEdit.getFolderId()));
            //保存权限信息
            flag = docFoldAuthorityService.saveDocFoldAuthority(fsFolderEdit);
        }
        return ApiResponse.data(200, flag, "");
    }

    /**
     * 生成目录层级码
     *
     * @return
     */
    @RequiresPermissions("fsFolder:addLevel")
    @RequestMapping("/addLevel")
    public void addLevel() {
        fsFolderService.addLevel(null);
    }

    /**
     * @return
     * @Author zoufeng
     * @Description 查询选中目录是否有可编辑权限
     * @Date 15:03 2018/10/16
     * @Param
     **/
    @RequestMapping("/moveFolderClient")
    @ResponseBody
    public ApiResponse moveFolderClient(String id , String oldPath, String newPath) {
            FsFolder rootFolder = new FsFolder();
            rootFolder =  fsFolderService.getById(id);
            String rootPath = rootFolder.getFolderPath();
            String parentFolderPath = rootPath+oldPath;
        String parentFolderNewPath = rootPath+newPath;
            String userId = jwtUtil.getSysUsers().getUserId();
            FsFolder fsFolderParent = new FsFolder();

            fsFolderParent = fsFolderService.getOne(new QueryWrapper<FsFolder>().eq("folder_path", parentFolderPath));
        FsFolder fsFolderNewParent = new FsFolder();
        fsFolderNewParent = fsFolderService.getOne(new QueryWrapper<FsFolder>().eq("folder_path", parentFolderNewPath));
        Map<String, String> map = new HashMap<>();
        String deptId = iSysUsersService.getById(userId).getDepartmentId();
        String orgId = docFoldAuthorityService.getDeptIds(deptId);
        List<String> listGroup = docGroupService.getPremission(userId);
        List<String> roleList = sysUserRoleService.getRolesByUserId(userId);
        boolean isEdit = fsFolderService.getIsEditClient(fsFolderNewParent.getFolderId(), userId, orgId, listGroup, roleList);
        String[] nameArr = fsFolderParent.getFolderName().split("\\*");
        List<Map> list = new ArrayList<>();
        for (int i = 0; i < nameArr.length; i++) {
            Map<String, String> nameMap = new HashMap<>();
            nameMap.put("name", nameArr[i]);
            list.add(nameMap);
        }
        List listFs = fsFolderService.countFolderName(fsFolderNewParent.getParentFolderId(), list);
        if (listFs.size() > 0) {
            map.put("message", "存在重名目录");
            map.put("code", "1");
            return ApiResponse.data(200, map, "");
        }
        if (!isEdit) {
            map.put("message", "您没有移动到此目录的权限");
            map.put("code", "2");
            return ApiResponse.data(200, map, "");
        }
        String[] strArr = fsFolderParent.getFolderId().split(",");
        for (String element : strArr) {
            String ids = fsFolderService.getChildFsFolder(element);
            String[] childArr = ids.split(",");
            for (int i = 0; i < childArr.length; i++) {
                if (childArr[i].equals(fsFolderNewParent.getFolderId())) {
                    map.put("message", "目标目录不能是移动目录的本身或子目录");
                    map.put("code", "3");
                    return ApiResponse.data(200, map, "");
                }
            }
        }
        String parentLevelCode = fsFolderService.getById(fsFolderNewParent.getFolderId()).getLevelCode();
        cacheToolService.updateLevelCodeCache(userId);
        String[] strIds =  fsFolderParent.getFolderId().split(",");
        List<FsFolder> folderList = new ArrayList<>();
        for (String element : strIds) {
            int codeNum = fsFolderService.getChildCodeCount(element);
            int totalCode = parentLevelCode.length() / 4 + codeNum;
            String folderAmount = docConfigService.getConfigValueByKey("folder_amount");
            if (folderAmount == null || Integer.parseInt(folderAmount) < 4) {
                folderAmount = "4";
            }
            if (totalCode > Integer.parseInt(folderAmount)) {
                map.put("message", "目录最多为" + folderAmount + "级");
                map.put("code", "4");
            }
            FsFolder fsFolder = new FsFolder();
            String beforeId = fsFolderService.getById(element).getParentFolderId();
            fsFolder.setFolderId(element);
            fsFolder.setParentFolderId( fsFolderNewParent.getFolderId());
            FsFolder parentFolder = fsFolderService.getById(fsFolderNewParent.getFolderId());
            if (parentFolder != null && parentFolder.getOwnId() != null && parentFolder.getOwnId().equals(userId)) {
                fsFolder.setOwnId(userId);
            } else {
                fsFolder.setOwnId("");
            }
            Date date = new Date();
            Timestamp ts = new Timestamp(date.getTime());
            fsFolder.setUpdateTime(ts);
            folderList.add(fsFolder);
            List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
            DocResourceLog docResourceLog = new DocResourceLog();
            String resId = UUID.randomUUID().toString().replace("-", "");
            docResourceLog.setId(resId);
            docResourceLog.setResourceId(element);
            docResourceLog.setOperateTime(ts);
            docResourceLog.setResourceType(1);
            docResourceLog.setUserId(userId);
            docResourceLog.setOperateType(12);
            docResourceLog.setValidFlag("1");
            docResourceLog.setBeforeId(beforeId);
            docResourceLog.setAfterId(fsFolderNewParent.getFolderId());
            docResourceLog.setAddressIp(HttpKit.getIp());
            resInfoList.add(docResourceLog);
            docInfoService.insertResourceLog(resInfoList);   //插入预览记录
        }
        fsFolderService.saveOrUpdateBatch(folderList);
        //根节点生成层级码
        fsFolderService.addLevel(fsFolderNewParent.getFolderId());

        map.put("message", "移动成功");
        map.put("code", "0");
        return ApiResponse.data(200, map, "");

    }
    /**
     * @return
     * @Author zoufeng
     * @Description 查询选中目录是否有可编辑权限
     * @Date 15:03 2018/10/16
     * @Param
     **/
    @RequestMapping("/moveFolder")
    @ResponseBody
    public ApiResponse checkIsMove(String chooseFolder, String nameStr, String fsFolderIds) {
        Map<String, String> map = new HashMap<>();
        String userId = jwtUtil.getSysUsers().getUserId();
        String deptId = iSysUsersService.getById(userId).getDepartmentId();
        String orgId = docFoldAuthorityService.getDeptIds(deptId);
        List<String> listGroup = docGroupService.getPremission(userId);
        List<String> roleList = sysUserRoleService.getRolesByUserId(userId);
        boolean isEdit = fsFolderService.getIsEditClient(chooseFolder, userId, orgId, listGroup, roleList);
        String[] nameArr = nameStr.split("\\*");
        List<Map> list = new ArrayList<>();
        for (int i = 0; i < nameArr.length; i++) {
            Map<String, String> nameMap = new HashMap<>();
            nameMap.put("name", nameArr[i]);
            list.add(nameMap);
        }
        List listFs = fsFolderService.countFolderName(chooseFolder, list);
        if (listFs.size() > 0) {
            map.put("message", "存在重名目录");
            map.put("code", "1");
            return ApiResponse.data(200, map, "");
        }
        if (!isEdit) {
            map.put("message", "您没有移动到此目录的权限");
            map.put("code", "2");
            return ApiResponse.data(200, map, "");
        }
        String[] strArr = fsFolderIds.split(",");
        for (String element : strArr) {
            String ids = fsFolderService.getChildFsFolder(element);
            String[] childArr = ids.split(",");
            for (int i = 0; i < childArr.length; i++) {
                if (childArr[i].equals(chooseFolder)) {
                    map.put("message", "目标目录不能是移动目录的本身或子目录");
                    map.put("code", "3");
                    return ApiResponse.data(200, map, "");
                }
            }
        }
        String parentLevelCode = fsFolderService.getById(chooseFolder).getLevelCode();
        cacheToolService.updateLevelCodeCache(userId);
        String[] strIds = fsFolderIds.split(",");
        List<FsFolder> folderList = new ArrayList<>();
        for (String element : strIds) {
            int codeNum = fsFolderService.getChildCodeCount(element);
            int totalCode = parentLevelCode.length() / 4 + codeNum;
            String folderAmount = docConfigService.getConfigValueByKey("folder_amount");
            if (folderAmount == null || Integer.parseInt(folderAmount) < 4) {
                folderAmount = "4";
            }
            if (totalCode > Integer.parseInt(folderAmount)) {
                map.put("message", "目录最多为" + folderAmount + "级");
                map.put("code", "4");
            }
            FsFolder fsFolder = new FsFolder();
            String beforeId = fsFolderService.getById(element).getParentFolderId();
            fsFolder.setFolderId(element);
            fsFolder.setParentFolderId(chooseFolder);
            FsFolder parentFolder = fsFolderService.getById(chooseFolder);
            if (parentFolder != null && parentFolder.getOwnId() != null && parentFolder.getOwnId().equals(userId)) {
                fsFolder.setOwnId(userId);
            } else {
                fsFolder.setOwnId("");
            }
            Date date = new Date();
            Timestamp ts = new Timestamp(date.getTime());
            fsFolder.setUpdateTime(ts);
            folderList.add(fsFolder);
            List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
            DocResourceLog docResourceLog = new DocResourceLog();
            String id = UUID.randomUUID().toString().replace("-", "");
            docResourceLog.setId(id);
            docResourceLog.setResourceId(element);
            docResourceLog.setOperateTime(ts);
            docResourceLog.setResourceType(1);
            docResourceLog.setUserId(userId);
            docResourceLog.setOperateType(12);
            docResourceLog.setValidFlag("1");
            docResourceLog.setBeforeId(beforeId);
            docResourceLog.setAddressIp(HttpKit.getIp());
            docResourceLog.setAfterId(chooseFolder);
            resInfoList.add(docResourceLog);

            docInfoService.insertResourceLog(resInfoList);   //插入预览记录
        }
        fsFolderService.saveOrUpdateBatch(folderList);
        //根节点生成层级码
        fsFolderService.addLevel(chooseFolder);
        fsFolderService.addPath(chooseFolder);
        map.put("message", "移动成功");
        map.put("code", "0");
        return ApiResponse.data(200, map, "");

    }

    /**
     * @return
     * @Author zoufeng
     * @Description 查询选中目录是否有可编辑权限
     * @Date 15:03 2018/10/16
     * @Param
     **/
    @RequestMapping("/checkIsEdit")
    @ResponseBody
    public boolean checkIsEdit(String chooseFolder) {
        return fsFolderService.getIsEdit(chooseFolder);
    }


    /**
     * @param fsFolderParams 目录信息及权限信息，祥看FsFolderParams
     * @return java.lang.Object
     * @Author zoufeng
     * @Description 新增文件夹的保存方法
     * @Date 17:53 2018/9/7
     **/
    @RequestMapping(value = "/addFolder")
    @ResponseBody
    public Object addFolder(FsFolderParams fsFolderParams) {

        Map<String, Object> result = new HashMap<>(5);
        FsFolder fsFolder = new FsFolder();
        String userId = ShiroKit.getUser().getId();
        List<String> roleList = ShiroKit.getUser().getRolesList();
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        cacheToolService.updateLevelCodeCache(userId);
        fsFolder.setFolderId(fsFolderParams.getFolderId());
        fsFolder.setFolderName(fsFolderParams.getFolderName());
        fsFolder.setVisibleRange(fsFolderParams.getVisible());
        FsFolder fsFolderParent = new FsFolder();
        fsFolderParent = fsFolderService.getById(fsFolderParams.getParentFolderId());
        String ownId = fsFolderParent.getOwnId();
        fsFolder.setParentFolderId(fsFolderParams.getParentFolderId());
        List<String> listGroup = docGroupService.getPremission(userId);
        String parentId = fsFolderParams.getParentFolderId();
        FsFolder pFolder = fsFolderService.getById(parentId);
        if (ownId != null && !"".equals(ownId)) {
            fsFolder.setOwnId(ownId);
        }
        String parentLevelCode = pFolder.getLevelCode();
        String folderAmount = docConfigService.getConfigValueByKey("folder_amount");
        if (folderAmount == null || Integer.parseInt(folderAmount) < 4) {
            folderAmount = "4";
        }
        if ((parentLevelCode.length() / 4) >= Integer.parseInt(folderAmount) + 1) {
            result.put("fail", 1);
            result.put("folderAmount", folderAmount);
            return result;
        }
        if (adminFlag != 1) {
            int isEdits = docFoldAuthorityService.findEditByUpload(parentId, listGroup, userId);
            if (isEdits != 2) {
                result.put("fail", 2);
                return result;
            }

        }

        if (ToolUtil.isNotEmpty(fsFolderParams.getFolderId())) {
            fsFolderService.updateFsFolder(fsFolder);
        } else {
            //新增一下主表
            Date date = new Date();
            Timestamp ts = new Timestamp(date.getTime());
            fsFolder.setCreateTime(ts);
            fsFolder.setUpdateTime(ts);
            String folderId = UUID.randomUUID().toString().replaceAll("-", "");

            fsFolder.setFolderId(folderId);
            fsFolder.setCreateUserId(userId);
            String folderParentId = fsFolder.getParentFolderId();
            //生成levelCode
            if (folderParentId != null && !"".equals(folderParentId)) {
                FsFolder parentFolder = fsFolderService.getById(folderParentId);
                String parentCode = parentFolder.getLevelCode();
                String currentCode = fsFolderService.getCurrentLevelCode(parentCode, parentFolder.getFolderId());
                fsFolder.setLevelCode(currentCode);
            }
            //生成showOrder
            String currentCode = this.sysIdtableService.getCurrentCode("FOLDER_NUM", "fs_folder");
            int num = Integer.parseInt(currentCode);
            fsFolder.setShowOrder(num);
            fsFolderParams.setFolderId(folderId);
            //保存目录信息
            fsFolderService.save(fsFolder);
            //保存权限信息
            docFoldAuthorityService.saveDocFoldAuthority(fsFolderParams);
            String levelCode = fsFolder.getLevelCode();
            String localName = "";
            for (int i = 2; i <= levelCode.length() / 4; i++) {
                String levelCodeString = levelCode.substring(0, i * 4);
                String folderName = fsFolderService.getFolderNameByLevelCode(levelCodeString);
                localName = localName + ">" + folderName;
            }
            localName = localName.substring(1, localName.length());
            result.put("localName", localName);
            result.put("folderId", folderId);

        }

        return result;
    }

    @RequestMapping(value = "/isOwn")
    @ResponseBody
    public Object addFolders(String categoryId) {
        FsFolder fsFolder = new FsFolder();
        String isOwn = fsFolderService.getById(categoryId).getOwnId();
        if (isOwn == null || isOwn.equals("")) {
            return "0";
        } else {
            return "1";
        }

    }

    @RequestMapping(value = "/addFolders")
    @ResponseBody
    public Object addFolders(String pathContent, String categoryId) {
        Map<String, Object> result = new HashMap<>(5);
        FsFolder fsFolder = new FsFolder();
        String userId = ShiroKit.getUser().getId();
        List<String> roleList = ShiroKit.getUser().getRolesList();
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);

        String[] list = pathContent.split(",");
        List<String> listGroup = docGroupService.getPremission(userId);
        int max = 0;
        for (int i = 0; i < list.length; i++) {

            String[] folderList = list[i].split("/");
            if (folderList.length - 1 > max) {
                max = folderList.length - 1;
            }
        }
        String parentLevelCode = fsFolderService.getById(categoryId).getLevelCode();
        String folderAmount = docConfigService.getConfigValueByKey("folder_amount");
        if (folderAmount == null || Integer.parseInt(folderAmount) < 4) {
            folderAmount = "4";
        }
        if ((parentLevelCode.length() / 4) + max > Integer.parseInt(folderAmount) + 1) {
            result.put("fail", 1);
            result.put("folderAmount", folderAmount);
            return result;
        }
        if (adminFlag != 1) {
            int isEdits = docFoldAuthorityService.findEditByUpload(categoryId, listGroup, userId);
            if (isEdits != 2) {
                result.put("fail", 2);
                return result;
            }

        }
        List<String> idList = new ArrayList<>();
        List<String> nameList = new ArrayList<>();
        for (int i = 0; i < list.length; i++) {

            String[] folderList = list[i].split("/");
            String folderId = categoryId;
            for (int j = 0; j < folderList.length - 1; j++) {
                folderId = addFolder(folderList[j], folderId);

            }
            String levelCode = fsFolderService.getById(folderId).getLevelCode();
            String localName = "";
            for (int n = 2; n <= levelCode.length() / 4; n++) {
                String levelCodeString = levelCode.substring(0, n * 4);
                String folderName = fsFolderService.getFolderNameByLevelCode(levelCodeString);
                localName = localName + ">" + folderName;
            }
            localName = localName.substring(1, localName.length());
            nameList.add(localName);

            idList.add(folderId);
        }
        result.put("idList", idList);
        result.put("localName", nameList);
        return result;

    }

    public String addFolder(String name, String id) {
        List<FsFolder> list = fsFolderService.addCheck(id, name, null);
        if (list.size() > 0) {
            return list.get(0).getFolderId();
        } else {
            Date date = new Date();
            FsFolder fsFolder = new FsFolder();
            fsFolder.setFolderName(name);

            fsFolder.setParentFolderId(id);
            FsFolderParams fsFolderParams = new FsFolderParams();
            Timestamp ts = new Timestamp(date.getTime());
            fsFolder.setCreateTime(ts);
            fsFolder.setUpdateTime(ts);
            String folderId = UUID.randomUUID().toString().replaceAll("-", "");
            String userId = ShiroKit.getUser().getId();
            fsFolder.setFolderId(folderId);
            fsFolder.setCreateUserId(userId);
            String folderParentId = fsFolder.getParentFolderId();
            FsFolder fsFolderParent = new FsFolder();
            fsFolderParent = fsFolderService.getById(fsFolder.getParentFolderId());
            if (fsFolderParent != null) {
                String ownId = fsFolderParent.getOwnId();
                if (ownId != null && !"".equals(ownId)) {
                    fsFolder.setOwnId(ownId);
                }
            }
            //生成levelCode
            if (folderParentId != null && !"".equals(folderParentId)) {
                FsFolder parentFolder = fsFolderService.getById(folderParentId);
                String parentCode = parentFolder.getLevelCode();
                String currentCode = fsFolderService.getCurrentLevelCode(parentCode, parentFolder.getFolderId());
                fsFolder.setLevelCode(currentCode);
            }
            //生成showOrder
            String currentCode = this.sysIdtableService.getCurrentCode("FOLDER_NUM", "fs_folder");
            int num = Integer.parseInt(currentCode);
            fsFolder.setShowOrder(num);
            fsFolderParams.setFolderId(folderId);
            fsFolderParams.setFolderName(name);
            //保存目录信息
            fsFolderService.save(fsFolder);
            //保存权限信息
            docFoldAuthorityService.saveDocFoldAuthority(fsFolderParams);
            return folderId;
        }
    }
    @RequestMapping(value = "/addFolderClient")
    @ResponseBody
    public synchronized ApiResponse addFolderClient(String folderName ,String path ,String id) {
        FsFolder rootFolder = new FsFolder();
        rootFolder =  fsFolderService.getById(id);
        String rootPath = rootFolder.getFolderPath();
        String parentFolderPath = rootPath+path;

        String userId = jwtUtil.getSysUsers().getUserId();
        FsFolder fsFolder = new FsFolder();
        fsFolder.setFolderName(folderName);
        cacheToolService.updateLevelCodeCache(userId);
        FsFolder fsFolderParent = new FsFolder();
//      parentFolderPath=  parentFolderPath.replace("\\","\\\\");

        fsFolderParent = fsFolderService.getOne(new QueryWrapper<FsFolder>().eq("folder_path", parentFolderPath));
        fsFolder.setParentFolderId(fsFolderParent.getFolderId());
        String ownId = fsFolderParent.getOwnId();
        String folderId = null;
        if (ownId != null && !"".equals(ownId)) {
            fsFolder.setOwnId(ownId);
        }
        String res = "success";
        String[] nameArr = folderName.split("\\*");
        List<Map> list = new ArrayList<>();
        for (int i = 0; i < nameArr.length; i++) {
            Map<String, String> map = new HashMap<>();
            map.put("name", nameArr[i]);
            list.add(map);
        }
        List listFs = fsFolderService.countFolderName( fsFolder.getParentFolderId(), list);
        if (listFs.size() > 0) {
            return ApiResponse.data(200, SUCCESS_TIP, "存在此目录");
        }



            //新增一下主表
            Date date = new Date();
            Timestamp ts = new Timestamp(date.getTime());
            fsFolder.setCreateTime(ts);
            fsFolder.setUpdateTime(ts);
            folderId = UUID.randomUUID().toString().replaceAll("-", "");

            fsFolder.setFolderId(folderId);
            fsFolder.setCreateUserId(userId);
            String folderParentId = fsFolder.getParentFolderId();
            //生成levelCode
            if (folderParentId != null && !"".equals(folderParentId)) {
                FsFolder parentFolder = fsFolderService.getById(folderParentId);
                String parentCode = parentFolder.getLevelCode();
                String currentCode = fsFolderService.getCurrentLevelCode(parentCode, parentFolder.getFolderId());
                fsFolder.setLevelCode(currentCode);
                String localName = "";
                for (int j = 1; j <= currentCode.length() / 4 - 1; j++) {
                    String levelCodeString = currentCode.substring(0, j * 4);
                    String folderName2 = fsFolderService.getFolderNameByLevelCode(levelCodeString);
                    localName = localName + "\\" + folderName2;
                }
                localName = localName + "\\" + fsFolder.getFolderName();
                fsFolder.setFolderPath(localName);
            }
            //生成showOrder
            String currentCode = this.sysIdtableService.getCurrentCode("FOLDER_NUM", "fs_folder");
            int num = Integer.parseInt(currentCode);
            fsFolder.setShowOrder(num);
            //保存目录信息
            fsFolderService.save(fsFolder);

            List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
            DocResourceLog docResourceLog = new DocResourceLog();
            String resourceId = UUID.randomUUID().toString().replace("-", "");
            docResourceLog.setId(resourceId);
            docResourceLog.setResourceId(folderId);
            docResourceLog.setOperateTime(ts);
            docResourceLog.setResourceType(1);
            docResourceLog.setUserId(userId);
            docResourceLog.setOperateType(0);
        docResourceLog.setAddressIp(HttpKit.getIp());
            resInfoList.add(docResourceLog);
            docInfoService.insertResourceLog(resInfoList);
            //保存权限信息

        return ApiResponse.data(200, SUCCESS_TIP, folderId);
    }
}
