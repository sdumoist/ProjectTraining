package com.jxdinfo.doc.client.resourcelog.controller;

import com.jxdinfo.doc.client.response.ApiResponse;
import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.common.jwt.util.JWTUtil;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.model.FsFolderView;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.docmanager.service.FsFileService;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.foldermanager.service.IDocFoldAuthorityService;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import com.jxdinfo.doc.manager.groupmanager.service.DocGroupService;
import com.jxdinfo.doc.manager.resourceLog.model.ResourceLog;
import com.jxdinfo.doc.manager.resourceLog.service.docResourceLogService;
import com.jxdinfo.hussar.bsp.organ.dao.SysStruMapper;
import com.jxdinfo.hussar.bsp.permit.service.ISysUserRoleService;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: ClientResourceLogController
 * @Description:TODO
 * @Author: lishilin
 * @Date: 2019/12/23
 * @Version: 1.0
 */
@Controller
@RequestMapping("/client/resource")
public class ClientResourceLogController {
    @Autowired
    private FsFileService fsFileService;

    /**
     * 文件处理
     */


    @Resource
    private JWTUtil jwtUtil;
    @Resource
    private SysStruMapper sysStruMapper;


    @Autowired
    private DocGroupService docGroupService;

    @Autowired
    private ISysUserRoleService sysUserRoleService;
    @Autowired
    private IDocFoldAuthorityService docFoldAuthorityService;

    @Autowired
    private ISysUsersService iSysUsersService;
    /**
     * 文档信息
     */
    @Autowired
    private IFsFolderService fsFolderService;
    /**
     * 目录服务类
     */
    @Resource
    private BusinessService businessService;

    @Resource
    private docResourceLogService docResourceLogService;

    @Autowired
    private DocInfoService docInfoService;

    @RequestMapping("/getChangeFile")
    @ResponseBody
    public ApiResponse getChangeFile(String folderId, String startTime) {
        FsFolder fsFolder = fsFolderService.getById(folderId);
        if (fsFolder == null){
            return ApiResponse.data(200, null, "");
        }
        String folderLevelCode = fsFolder.getLevelCode();
        Integer length = fsFolder.getFolderPath().length();
        List<FsFolderView> list = docInfoService.getChangeFile(folderLevelCode, length,startTime);
        for(int i=0;i<list.size();i++){
            if("2".equals(list.get(i).getOperateType())){
                continue;
            }


            FsFolderView fsFolderView = list.get(i);
            String foldId = "";
            String foldName = "";
            if(fsFolderView.getFileType().equals("folder")){
                foldId=list.get(i).getFileId();
                foldName= fsFolderService.getById(foldId).getFolderName();
            }else {
               String docId=list.get(i).getFileId();
               DocInfo docInfo = docInfoService.getById(docId);
               if(docInfo!=null){
                   foldId=docInfo.getFoldId();
               }
            }
            String localName ="";
            String  currentCode =list.get(i).getBeforeLevelCode();
            if(currentCode!=null){
            Integer levelLength = currentCode.length()/4;

            for (int j = 1; j <= currentCode.length() / 4; j++) {
                String levelCodeString = currentCode.substring(0, j * 4);
                String folderName = fsFolderService.getFolderNameByLevelCode(levelCodeString);

                localName = localName + "\\" + folderName;

            }
            localName=localName+"\\"+foldName;
                localName=localName.substring(
                        length+1);

            list.get(i).setFolderLocal(localName);}
        }
        Map<String,Object> map = new HashMap();
        map.put("list",list);
        map.put("levelCode",folderLevelCode);
        return ApiResponse.data(200, map, "");
    }

    @RequestMapping("/resourceList")
    @ResponseBody
    public ApiResponse getChildren(String folderId, String startTime) {
        if (folderId == null || folderId.equals("#") || folderId.equals("")) {
            List<FsFolder> list = fsFileService.getRoot();
            FsFolder fsFile = list.get(0);
            folderId = fsFile.getFolderId();
        }
        String userId = jwtUtil.getSysUsers().getUserId();
        List<String> listGroup = docGroupService.getPremission(userId);
        List<String> roleList = sysUserRoleService.getRolesByUserId(userId);
        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        List<FsFolderView> fileList = new ArrayList<>();
        fileList = getResourceList(folderId, listGroup, userId, adminFlag);
        String fileIds = "('";
        List<ResourceLog> ResourceLogList = new ArrayList<>();
        if (fileList.size() > 0) {
            for (int i = 0; i < fileList.size(); i++) {
                FsFolderView ff = fileList.get(i);
                if (i < fileList.size() - 1) {
                    fileIds = fileIds + ff.getFileId() + "','";
                } else {
                    fileIds = fileIds + ff.getFileId() + "')";
                }
            }
            ResourceLogList = docResourceLogService.ClientResourceLogList(fileIds, startTime);
        }
        return ApiResponse.data(200, ResourceLogList, "");
    }

    public List getResourceList(String folderId, List<String> listGroup, String userId, Integer adminFlag) {
        List<FsFolderView> list = new ArrayList<>();
        FsFolder folder = fsFolderService.getById(folderId);
        FsFolderParams fsFolderParams = new FsFolderParams();
        fsFolderParams.setGroupList(listGroup);
        fsFolderParams.setUserId(userId);
        List roleList = sysUserRoleService.getRolesByUserId(userId);
        fsFolderParams.setRoleList(roleList);
        fsFolderParams.setType(null);
        fsFolderParams.setLevelCodeString(folder.getLevelCode());
        fsFolderParams.setId(folderId);
        String deptId = docFoldAuthorityService.getDeptIds(iSysUsersService.getById(userId).getDepartmentId());
        String levelCode = businessService.getLevelCodeByUserUploadClient(fsFolderParams, deptId);
        String levelCodeString = businessService.getFileLevelCodeFrontMobile(fsFolderParams);
        list = fsFolderService.getFilesAndFloder(0, 0, folderId, null, "",
                "fileName", listGroup, userId, adminFlag, null, levelCodeString, levelCode, "0", deptId,roleList);
        List<FsFolderView> fileList = new ArrayList<>();
        for (FsFolderView ff : list) {
            if ("folder".equals(ff.getFileType())) {
                fileList.addAll(getResourceList(ff.getFileId(), listGroup, userId, adminFlag));
            } else {
                fileList.add(ff);
            }
        }
        return fileList;
    }
}
