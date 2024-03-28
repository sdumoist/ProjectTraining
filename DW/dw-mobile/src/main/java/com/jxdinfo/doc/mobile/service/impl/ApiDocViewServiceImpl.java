package com.jxdinfo.doc.mobile.service.impl;

import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.docutil.service.DocConfigService;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.common.util.FileTool;
import com.jxdinfo.doc.common.util.StringUtil;
import com.jxdinfo.doc.front.foldermanager.service.FrontFolderService;
import com.jxdinfo.doc.front.groupmanager.service.FrontDocGroupService;
import com.jxdinfo.doc.manager.docmanager.model.FsFolderView;
import com.jxdinfo.doc.manager.docmanager.service.FsFileService;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.foldermanager.service.IDocFoldAuthorityService;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import com.jxdinfo.doc.mobile.model.Response;
import com.jxdinfo.doc.mobile.util.ConvertUtil;
import com.jxdinfo.hussar.bsp.permit.dao.SysUserRoleMapper;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jxdinfo.doc.mobile.constants.ApiConstants.DOC_VIEW_DATA;


/**
 * 查询下级节点（文件和目录）
 */
@Component
public class ApiDocViewServiceImpl extends ApiBaseServiceImpl {


    private static final String businessID = DOC_VIEW_DATA;

    @Autowired
    private IFsFolderService fsFolderService;
    @Autowired
    private FsFileService fsFileService;

    /** 文档群组服务类 */
    @Autowired
    private FrontDocGroupService frontDocGroupService;

    @Autowired
    private DocConfigService docConfigService;
    @Autowired
    private IDocFoldAuthorityService docFoldAuthorityService;

    @Resource
    private SysUserRoleMapper sysUserRoleMapper;
    @Autowired
    private ISysUsersService iSysUsersService;
    /**
     * 目录服务类
     */
    @Resource
    private BusinessService businessService;

    @Override
    public String getBusinessID() {
        return businessID;
    }

    private static final Map orderMap = new HashMap();
    private static final Map typeMap = new HashMap();

    static {
        //排序和查询规则
        orderMap.put("0", "fileName");
        orderMap.put("1", "fileName");
        orderMap.put("2", "createTime");
        orderMap.put("3", "createTime");
        typeMap.put("1", ".doc,.docx");
        typeMap.put("2", ".ppt,.pptx");
        typeMap.put("3", ".txt");
        typeMap.put("4", ".pdf");
        typeMap.put("5", ".xls,.xlsx");
    }
    /**
     * @param params 参数：详见文档
     * @return Response
     * @description: 查询下级节点（文件和目录）
     * @Title: execute
     * @author:zhongguangrui
    */
    @Override
    public Response execute(HashMap<String,String> params) {
        Response response = new Response();
        try {
            Integer pageNumber =Integer.parseInt(params.get("pageNum")) ;
            Integer pageSize = Integer.parseInt(params.get("pageSize"));
            String name = params.get("name");
            String order = params.get("order");
            String userId = params.get("userId");
            String folderId = params.get("folderId");
            String type = params.get("type");
            String operateType = params.get("operateType");
            List<String> listGroup = frontDocGroupService.getPremission(userId);
            List<String> rolesList = sysUserRoleMapper.getRolesByUserId(userId);
       //     String orgId = iSysUsersService.selectById(userId).getDepartmentId();
            String  isDesc="0";
            if("1".equals(order)||"3".equals(order)){
                isDesc = "1";
            }
            String orderResult = (String) orderMap.get(order);
            Map<String, Object> result = new HashMap<>(5);
            List<FsFolderView> list = new ArrayList<>();
            int num = 0;
            //判断是否为子级目录（只能在子文件夹上传文件）
            //boolean isChild = fsFileService.isChildren(folderId);
            //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
            Integer adminFlag = CommonUtil.getAdminFlag(rolesList);
            String[] typeArr;
            if ("".equals(type)) {
                type = "0";
            }
            if ("0".equals(type)) {
                typeArr = null;
            } else {
                String typeResult = (String) typeMap.get(type);
                typeArr = typeResult.split(",");
            }

            name = StringUtil.transferSqlParam(name);
            FsFolder folder=fsFolderService.getById(folderId);
            FsFolderParams fsFolderParams = new FsFolderParams();
            fsFolderParams.setGroupList(listGroup);
            fsFolderParams.setRoleList(null);
            fsFolderParams.setUserId(userId);
            fsFolderParams.setType(operateType);
            fsFolderParams.setLevelCodeString(folder.getLevelCode());
            fsFolderParams.setId(folderId);
//        List<String> levelCodeList = folderService.getlevelCodeList(listGroup, userId, type);
            String levelCodeString = businessService.getFileLevelCodeFrontMobile(fsFolderParams);
            //获得目录管理权限层级码
            fsFolderParams.setType("2");
            String levelCode = businessService.getLevelCodeByUserMobile(fsFolderParams);
            //获得下一级文件和目录
            String orgId = iSysUsersService.getById(userId).getDepartmentId();
            list = fsFolderService.getFilesAndFloder((pageNumber - 1) * pageSize, pageSize, folderId, typeArr, name,
                    orderResult, listGroup, userId, adminFlag, operateType, levelCodeString, levelCode,isDesc,orgId,null);
            list = ConvertUtil.changeSize(list);

            //获得下一级文件和目录数量
            num = fsFolderService.getFilesAndFloderNum(folderId, typeArr, name, orderResult, listGroup, userId,
                    adminFlag, operateType, levelCodeString, levelCode,orgId,null);
            //显示前台的文件数量
            int amount = fsFolderService.getFileNum(folderId, typeArr, name, listGroup, userId, adminFlag, operateType, levelCode,orgId,null);
            //判断是否有可编辑文件的权限
            if (adminFlag != 1) {
                int isEdits = docFoldAuthorityService.findEditByUploadMobile(folderId, listGroup, userId);
                result.put("noChildPower", isEdits);
            }
            if (userId.equals(folder.getCreateUserId())) {
                result.put("noChildPower", 2);
            }
            String folderAmount = docConfigService.getConfigValueByKey("folder_amount");
            if(folderAmount ==null||Integer.parseInt(folderAmount)<4){
                folderAmount="4";
            }
            result.put("folderAmount", folderAmount);

            if (adminFlag != 1) {
                int isEdits = docFoldAuthorityService.findEditNew(folderId, listGroup, userId);
                result.put("noChildPowerFolder", isEdits);
            }
            if (userId.equals(folder.getCreateUserId())) {
                result.put("noChildPowerFolder", 1);
            }
            result.put("userName", iSysUsersService.getById(userId).getUserName());
            result.put("isAdmin", adminFlag);
            result.put("pageCount", num);
            result.put("pageSize",pageSize);
            result.put("pageNum",pageNumber);
            result.put("rows", list);
            result.put("fileCount", amount);
            response.setSuccess(true);
            response.setData(result);
        } catch (Exception e) {
            e.printStackTrace();
            response.setSuccess(false);
            response.setData(false);
            response.setMsg(e.getMessage());
        }
        response.setBusinessID(businessID);
        return response;
    }
}
