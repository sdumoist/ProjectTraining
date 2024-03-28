package com.jxdinfo.doc.mobile.service.impl;


import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.common.util.FileTool;
import com.jxdinfo.doc.manager.docmanager.model.FsFolderView;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import com.jxdinfo.doc.manager.groupmanager.service.DocGroupService;
import com.jxdinfo.doc.manager.personalcenter.service.PersonalOperateService;
import com.jxdinfo.doc.mobile.model.Response;
import com.jxdinfo.hussar.bsp.permit.dao.SysUserRoleMapper;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jxdinfo.doc.mobile.constants.ApiConstants.PERSONAL_DOWNLOAD_RECORD;
import static com.jxdinfo.doc.mobile.constants.ApiConstants.PERSONAL_UPLOAD_RECORD;

/**
 * 获取下载、预览列表
 */
@Component
public class ApiPersonalDownloadOrPreviewServiceImpl extends ApiBaseServiceImpl {


    private static final String businessID = PERSONAL_DOWNLOAD_RECORD;

    /*个人中心数据处理的服务*/
    @Autowired
    private PersonalOperateService operateService;

    @Autowired
    private DocGroupService docGroupService;

    @Autowired
    private ISysUsersService iSysUsersService;


    /**
     * 目录服务类
     */
    @Resource
    private BusinessService businessService;
    @Resource
    private SysUserRoleMapper sysUserRoleMapper;
    @Override
    public String getBusinessID() {
        return businessID;
    }

    /**
     * @param params 参数,page当前页、size每页条数、userId当前用户Id
     *               name:文件名，模糊查询
     *               order：排序规则（0：文件名降序；1：文件名升序；2：上传时间升序；3：上传时间降序）
     *               opType:操作类型（3：预览；4：下载）
     * @return Response
     * @description: 获取下载、预览列表
     * @Title: execute
     * @author:zhongguangrui
    */
    @Override
    public Response execute(HashMap<String,String> params) {
        Response response = new Response();
        try {
            String pageStr = params.get("pageNum");
            String sizeStr = params.get("pageSize");
            String name = params.get("name");
            String order = params.get("order");
            String userId = params.get("userId");
            String opType = params.get("opType");
            String fileType = params.get("fileType");
            int pageNumber = Integer.parseInt(pageStr);
            int pageSize = Integer.parseInt(sizeStr);
            int beginIndex = pageNumber * pageSize - pageSize;
            Map typeMap = new HashMap();
            //查询规则
            typeMap.put("1", "");
            typeMap.put("2", ".doc,.docx,.ppt,.pptx,.txt,.pdf,.xls,.xlsx");
            typeMap.put("3", ".png,.jpg,.bmp,.psd");
            typeMap.put("4", ".mp4,.avi,.mov");
            typeMap.put("5", ".mp3,.wav");
            String[] typeArr;
            if (fileType == null) {
                fileType = "0";
            }
            if ("0".equals(fileType)) {
                typeArr = null;
            } else {
                String typeResult = (String) typeMap.get(fileType);
                typeArr = typeResult.split(",");
            }
            List<String> roleList = sysUserRoleMapper.getRolesByUserId(userId);
            Integer adminFlag = CommonUtil.getAdminFlag(roleList);
            FsFolderParams fsFolderParams = new FsFolderParams();
            List<String> listGroup = docGroupService.getPremission(userId);
            fsFolderParams.setUserId(userId);
            fsFolderParams.setGroupList(listGroup);
            //获得目录管理权限层级码
            fsFolderParams.setType("2");
            String orgId = iSysUsersService.getById(userId).getDepartmentId();
            String levelCode = businessService.getLevelCodeByUserMobile(fsFolderParams);
            List<Map> list = operateService.getMyHistory(userId, opType, beginIndex, pageSize, name,typeArr,order,levelCode,orgId);
            int count = operateService.getMyHistoryCount(userId, opType, name);
            Map histories = new HashMap();
            histories.put("adminFlag",adminFlag);
            histories.put("rows",list);
            histories.put("pageCount",count);
            histories.put("pageSize",pageSize);
            histories.put("pageNum",pageNumber);
            response.setSuccess(true);
            response.setData(histories);
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
