package com.jxdinfo.doc.front.docmanager.controller;

import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.common.util.FileTool;
import com.jxdinfo.doc.common.util.StringUtil;
import com.jxdinfo.doc.common.util.UserInfoUtil;
import com.jxdinfo.doc.front.docmanager.service.FrontFsFileService;
import com.jxdinfo.doc.front.foldermanager.service.FrontFoldAuthorityService;
import com.jxdinfo.doc.front.foldermanager.service.FrontFolderService;
import com.jxdinfo.doc.front.groupmanager.service.FrontDocGroupService;
import com.jxdinfo.doc.manager.docmanager.model.FsFolderView;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.foldermanager.service.IDocFoldAuthorityService;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类的用途：跳转前台资源管理器
 * 创建日期：2018年9月4日
 * 修改历史：
 * 修改日期：2018年9月6日
 * 修改作者：yjs
 * 修改内容：重构代码
 */
@Controller
@RequestMapping("/frontFile")
public class FrontFsFileController  {


    @Autowired
    private FrontFsFileService frontFsFileService;
    @Autowired
    private FrontFolderService frontFolderService;

    @Autowired
    private CacheToolService cacheToolService;

    @Autowired
    private FrontFoldAuthorityService frontFoldAuthorityService;

    @Autowired
    private IDocFoldAuthorityService docFoldAuthorityService;
    private IFsFolderService fsFolderService;
    /**
     * 目录服务类
     */
    @Resource
    private BusinessService businessService;

    /** 文档群组服务类 */
    @Autowired
    private FrontDocGroupService frontDocGroupService;

    /**
     * 跳转前台资源管理器
     *
     * @param model model类
     * @param fileName 文件名
     * @param fileId 文件ID
     * @return string 返回路径
     */
    @GetMapping("")
    public String index(String fileId,String fileName,Model model) {
        String id = fileId == null ? "" : fileId;
        String name = fileName == null ? "" : fileName;
        String userName = ShiroKit.getUser().getName();
        model.addAttribute("id", id);
        model.addAttribute("userName", userName);
        model.addAttribute("fileName", name);
        model.addAttribute("isPersonCenter",false);
        return "/doc/front/docmanager/frontResourceManager.html";
    }


    /**
     * 获取根节点
     */
    @PostMapping(value = "/getRoot")
    @ResponseBody
    public Map getRoot() {
        List<FsFolder> list = frontFsFileService.getRoot();
        Map map = new HashMap();
        FsFolder fsFile = list.get(0);
        map.put("root", fsFile.getFolderId());
        map.put("rootName", fsFile.getFolderName());
        return map;
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
        List<String> listGroup = frontDocGroupService.getPremission(userId);
        List<String> roleList =ShiroKit.getUser().getRolesList();
        List<Map> list = frontFsFileService.getInfo(idList,userId,listGroup,roleList);
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
     * @title: 查询下级节点
     * @description: 查询下级节点（文件和目录）
     * @date: 2018-8-12.
     * @author: yjs
     */
    @PostMapping(value = "/getChildren")
    @ResponseBody
    public Object getChildren(@RequestParam String id,
                              @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
                              @RequestParam(value = "pageSize", defaultValue = "20") int pageSize, String order, String name,
                              String type, String nameFlag, String operateType) {
        Map orderMap = new HashMap();
        Map typeMap = new HashMap();
        //排序和查询规则
        orderMap.put("0", "createTime");
        orderMap.put("1", "fileName");
        orderMap.put("2", "fileType");
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
        boolean isChild = frontFsFileService.isChildren(id);
        String userId = ShiroKit.getUser().getId();
        List<String> listGroup = frontDocGroupService.getPremission(userId);
        List<String> roleList = ShiroKit.getUser().getRolesList();
        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        FsFolder fsFolder = frontFolderService.getById(id);
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

        FsFolderParams fsFolderParams = new FsFolderParams();
        fsFolderParams.setGroupList(listGroup);
        fsFolderParams.setUserId(userId);
        fsFolderParams.setRoleList(ShiroKit.getUser().getRolesList());
        fsFolderParams.setType("front");
        fsFolderParams.setLevelCodeString(fsFolder.getLevelCode());
        fsFolderParams.setRoleList(roleList);
//        List<String> levelCodeList = folderService.getlevelCodeList(listGroup, userId, type);
        String levelCodeString = businessService.getFileLevelCodeFront(fsFolderParams);
        //获得目录管理权限层级码
        fsFolderParams.setType("2");
        String levelCode = businessService.getLevelCodeByUserUpload(fsFolderParams);
        //获得下一级文件和目录
        String orgId =docFoldAuthorityService.getDeptIds( ShiroKit.getUser().getDeptId());
        list = frontFolderService.getFilesAndFloder((pageNumber - 1) * pageSize, pageSize, id, typeArr, name,
                orderResult, listGroup, userId, adminFlag, operateType, levelCodeString, levelCode,orgId,roleList);
        list = changeSize(list);

        //获得下一级文件和目录数量
        num = frontFolderService.getFilesAndFloderNum(id, typeArr, name, orderResult, listGroup, userId,
                adminFlag, operateType, levelCodeString, levelCode,orgId,roleList);
        //显示前台的文件数量
        int amount = frontFolderService.getFileNum(id, typeArr, name, listGroup, userId, adminFlag, operateType, levelCode,orgId,roleList);
        //判断是否有可编辑文件的权限
        if (adminFlag != 1) {
            int isEdits = frontFoldAuthorityService.findEdit(id, listGroup, userId);
            result.put("noChildPower", isEdits);

        }
        if (userId.equals(fsFolder.getCreateUserId())) {
            result.put("noChildPower", 2);
        }
        result.put("userId", ShiroKit.getUser().getName());
        result.put("isAdmin", adminFlag);
        result.put("total", num);
        result.put("rows", list);
        result.put("isChild", isChild);
        result.put("amount", amount);
        return result;
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


}
