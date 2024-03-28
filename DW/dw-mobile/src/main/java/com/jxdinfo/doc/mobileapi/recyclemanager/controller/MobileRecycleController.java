package com.jxdinfo.doc.mobileapi.recyclemanager.controller;

import com.alibaba.fastjson.JSONObject;
import com.jxdinfo.doc.common.jwt.util.JWTUtil;
import com.jxdinfo.doc.common.jwt.util.RemoteIpMobileUtil;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.docmanager.service.FsFileService;
import com.jxdinfo.doc.manager.docrecycle.service.IDocRecycleService;
import com.jxdinfo.doc.manager.folderextranetauth.service.IFolderExtranetAuthService;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.foldermanager.service.IDocFoldAuthorityService;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import com.jxdinfo.doc.manager.groupmanager.service.DocGroupService;
import com.jxdinfo.doc.manager.statistics.service.DeptStatisticsService;
import com.jxdinfo.doc.manager.statistics.service.EmpStatisticsService;
import com.jxdinfo.doc.mobileapi.foldermanager.service.IMobileFsFolderService;
import com.jxdinfo.hussar.bsp.organ.dao.SysStruMapper;
import com.jxdinfo.hussar.bsp.organ.model.SysStru;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@Controller
@RequestMapping("/mobile/recycle")
public class MobileRecycleController extends BaseController {
    @Autowired
    private FsFileService fsFileService;
    @Autowired
    private DocInfoService docInfoService;

    @Autowired
    private DocGroupService docGroupService;

    @Autowired
    private IFsFolderService fsFolderService;

    @Autowired
    private ISysUserRoleService sysUserRoleService;

    @Autowired

    /**
     * 目录服务类
     */
    @Resource
    private JWTUtil jwtUtil;

    @Resource
    private SysStruMapper sysStruMapper;


    @Autowired
    private ISysUsersService iSysUsersService;
    /**
     * 回收站服务类
     */
    @Autowired
    private IDocRecycleService iDocRecycleService;
    @Autowired
    private IDocFoldAuthorityService docFoldAuthorityService;
    /**
     * 部门文件大小 Service接口
     */
    @Autowired
    private DeptStatisticsService deptStatisticsService;

    /**
     * 个人空间文件大小 Service接口
     */
    @Autowired
    private EmpStatisticsService empStatisticsService;

    /**
     * 外网权限方法
     */
    @Autowired
    private IMobileFsFolderService fsMobileFolderService;

    @Autowired
    private IFolderExtranetAuthService iFolderExtranetAuthService;

    @Value("${openExtranetLimit}")
    private String openExtranetLimit;

    /**
     * @author luzhanzhao
     * @date 2018-11-16
     * @description 返回预览记录列表
     * @param name 关键字
     * @param pageNumber 当前页数
     * @param pageSize 每页长度
     * @return
     * @edit zhongguangrui
     */
    @RequestMapping("/list")
    @ResponseBody
    public ApiResponse list(String name, @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber, @RequestParam(defaultValue = "60") int pageSize, String order, HttpServletRequest request){
        String userId = jwtUtil.getSysUsers().getUserId();
        List<String> roleList =sysUserRoleService.getRolesByUserId(userId);
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        //获取当前登录人
        String deptId = iSysUsersService.getById(userId).getDepartmentId();
        String orgId = "";
        SysStru stru = sysStruMapper.selectById(deptId);
        if(stru!=null){
            orgId = stru.getOrganAlias();
        }
        FsFolderParams fsFolderParams = new FsFolderParams();
        List<String> listGroup = docGroupService.getPremission(userId);

        List<String> folderExtranetIds = null;
        // 开启了外网访问限制
        if (adminFlag != 1 && StringUtils.equals(openExtranetLimit, "true")) {
            // 用户是从外网访问的系统
            if (adminFlag!=1 && RemoteIpMobileUtil.isExtranetVisit(request)) {

                folderExtranetIds = iFolderExtranetAuthService.getFolderExtranetListMobile();
                if (folderExtranetIds == null || folderExtranetIds.size() == 0) { // 没有外网可以访问的目录
                    System.out.println("===================没有配置外网可以访问的目录=====================");


                    return ApiResponse.data(200, null, "");
                }

            }
        }
        fsFolderParams.setUserId(userId);
        fsFolderParams.setGroupList(listGroup);
        //获得目录管理权限层级码
        fsFolderParams.setType("2");
//        String levelCode = businessService.getLevelCodeByUserUpload(fsFolderParams);
        Map<String, Object> list = iDocRecycleService.getDocRecycleListMobile(String.valueOf(pageNumber),String.valueOf(pageSize),name,order,userId,orgId,folderExtranetIds);
        Map histories = new HashMap();

        histories.put("adminFlag",adminFlag);
        histories.put("rows",list);

        return ApiResponse.data(200,histories,"");
    }

    /**
     * 还原回收站
     *
     * @return boolean
     * @author: ChenXin
     */
    @RequestMapping("/restore")
    @ResponseBody
    public ApiResponse restore() {
        String userId = jwtUtil.getSysUsers().getUserId();
        List<String> listGroup = docGroupService.getPremission(userId);
        List<String> roleList = sysUserRoleService.getRolesByUserId(userId);
        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        JSONObject json = new JSONObject();
        String fileId = super.getPara("fileId");
        if (ToolUtil.isEmpty(fileId)) {
            fileId = "";
        }
        String deptId = iSysUsersService.getById(userId).getDepartmentId();
        String orgId = sysStruMapper.selectById(deptId).getOrganAlias();
        String folderId = super.getPara("folderId");
        String fileName = super.getPara("fileName");
        if (adminFlag != 1) {
            int isEdits = docFoldAuthorityService.findEditClient(folderId, listGroup, userId,orgId);
            if (isEdits == 0) {
                json.put("result", "3");
                return ApiResponse.data(200,json,"您没有还原到此目录的权限");
            }
        }
        //判断是否为根目录（不能在根目录上传文件）
        DocInfo docInfo = docInfoService.getDocDetail(fileId);
        boolean isRoot = (folderId.equals(fsFileService.getRoot().get(0).getFolderId()));
        if (!isRoot) {
            String fileNameStr = fileName + docInfo.getDocType();
            if (iDocRecycleService.checkDocExist(folderId, fileNameStr)) {
                json.put("result", "0");
                return ApiResponse.data(200,json,"文件已存在");
            } else {
                if (docInfo != null) {
                    if (adminFlag != 1) {
                        double lackShare = empStatisticsService.getStatisticsDataByUserId(userId, docInfo.getFileSize());
                        if (lackShare < 0) {
                            json.put("result", "5");
                            return ApiResponse.data(200,"空间不足");
                        }
                    }
                }

                if (iDocRecycleService.restore(fileId, folderId)) {
                    json.put("result", "1");
                    return ApiResponse.data(200,json,"还原成功");
                } else {
                    json.put("result", "2");
                    return ApiResponse.data(200,json,"还原失败");
                }
            }
        } else {
            json.put("result", "4");
            return ApiResponse.data(200,json,"无法还原到根目录");
        }
    }

    /**
     * 还原回收站
     *
     * @return boolean
     * @author: ChenXin
     */
    @RequestMapping("/restoreOldFolder")
    @ResponseBody
    public ApiResponse restoreOldFolder() {
        String userId = jwtUtil.getSysUsers().getUserId();
        List<String> listGroup = docGroupService.getPremission(userId);
        List<String> roleList = sysUserRoleService.getRolesByUserId(userId);
        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        JSONObject json = new JSONObject();
        String fileId = super.getPara("fileId");
        if (ToolUtil.isEmpty(fileId)) {
            fileId = "";
        }
        DocInfo docInfoOld =docInfoService.getById(fileId);
        String folderId = docInfoOld.getFoldId();
        String fileName = docInfoOld.getTitle();
        FsFolder fsFolder = fsFolderService.getById(folderId);
        if(fsFolder == null){
            json.put("result", "6");
            return ApiResponse.data(200,json,"此目录已不存在");
        }
//        if (adminFlag != 1) {
//            int isEdits = docFoldAuthorityService.findEditClient(folderId, listGroup, userId,orgId);
//            if (isEdits == 0) {
//                json.put("result", "3");
//                return ApiResponse.data(200,json,"您没有还原到此目录的权限");
//            }
//        }
        //判断是否为根目录（不能在根目录上传文件）
        DocInfo docInfo = docInfoService.getDocDetail(fileId);
        boolean isRoot = (folderId.equals(fsFileService.getRoot().get(0).getFolderId()));
        if (!isRoot) {
            String fileNameStr = fileName + docInfo.getDocType();
            if (iDocRecycleService.checkDocExist(folderId, fileNameStr)) {
                json.put("result", "0");
                return ApiResponse.data(200,json,"文件已存在");
            } else {
                if (docInfo != null) {
                    if (adminFlag != 1) {
                        double lackShare = empStatisticsService.getStatisticsDataByUserId(userId, docInfo.getFileSize());
                        if (lackShare < 0) {
                            json.put("result", "5");
                            return ApiResponse.data(200,"空间不足");
                        }
                    }
                }


                if (iDocRecycleService.restore(fileId, folderId)) {
                    json.put("result", "1");
                    return ApiResponse.data(200,json,"还原成功");
                } else {
                    json.put("result", "2");
                    return ApiResponse.data(200,json,"还原失败");
                }
            }
        } else {
            json.put("result", "4");
            return ApiResponse.data(200,json,"无法还原到根目录");
        }

    }
}
