package com.jxdinfo.doc.mobile.service.impl;

import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.common.docutil.service.DocConfigService;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.common.util.StringUtil;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jxdinfo.doc.mobile.constants.ApiConstants.DOC_VIEW_DATA;
import static com.jxdinfo.doc.mobile.constants.ApiConstants.FOLDERS_SHOW_MENU;


/**
 * 一次性查询所有三级节点（目录）
 */
@Component
public class ApiShowFoldersServiceImpl extends ApiBaseServiceImpl {


    private static final String businessID = FOLDERS_SHOW_MENU;

    @Autowired
    private IFsFolderService fsFolderService;

    /** 文档群组服务类 */
    @Autowired
    private FrontDocGroupService frontDocGroupService;


    @Resource
    private SysUserRoleMapper sysUserRoleMapper;

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
            String userId = params.get("userId");
            List<String> listGroup = frontDocGroupService.getPremission(userId);
            List<String> rolesList = sysUserRoleMapper.getRolesByUserId(userId);
            Map<String, Object> result = new HashMap<>(5);
            //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
            Integer adminFlag = CommonUtil.getAdminFlag(rolesList);
            FsFolderParams fsFolderParams = new FsFolderParams();
            fsFolderParams.setGroupList(listGroup);
            fsFolderParams.setUserId(userId);
            fsFolderParams.setType("1");
            fsFolderParams.setLevelCodeString("0001");
            String levelCodeString = businessService.getFileLevelCodeFrontMobile(fsFolderParams);
            //获得目录管理权限层级码
            fsFolderParams.setType("2");
            String levelCode = businessService.getLevelCodeByUserMobile(fsFolderParams);
            //获得下一级文件和目录
            List<FsFolder> list1 = fsFolderService.getFolderByLevelCodeStringFirst(levelCodeString,adminFlag);
            List<FsFolder> list2 = fsFolderService.getFolderByLevelCodeStringSecond(levelCodeString,adminFlag);
            List<FsFolder> list3 = fsFolderService.getFolderByLevelCodeStringThird(levelCodeString,adminFlag);
            for (int i = 0; i < list2.size(); i ++){
                list2.get(i).setChildren(new ArrayList<FsFolder>());
                for (int j = 0; j < list3.size(); j ++){
                    if (list2.get(i).getFolderId().equals(list3.get(j).getParentFolderId())){
                        list2.get(i).getChildren().add(list3.get(j));
                    }
                }
            }
            for (int i = 0; i < list1.size(); i ++){
                list1.get(i).setChildren(new ArrayList<FsFolder>());
                for (int j = 0; j < list2.size(); j ++){
                    if (list1.get(i).getFolderId().equals(list2.get(j).getParentFolderId())){
                        list1.get(i).getChildren().add(list2.get(j));
                    }
                }
            }
            result.put("isAdmin", adminFlag);
            result.put("folderList", list1);
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
