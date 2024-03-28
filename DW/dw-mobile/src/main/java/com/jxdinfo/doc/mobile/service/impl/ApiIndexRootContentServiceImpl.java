package com.jxdinfo.doc.mobile.service.impl;

import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.common.util.UserInfoUtil;
import com.jxdinfo.doc.front.docmanager.service.FrontFsFileService;
import com.jxdinfo.doc.front.groupmanager.service.FrontDocGroupService;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.mobile.model.Response;
import com.jxdinfo.hussar.bsp.permit.dao.SysUserRoleMapper;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.jxdinfo.doc.mobile.constants.ApiConstants.INDEX_ROOT_CONTENT;


/**
 * 主页根目录获取
 */
@Component
public class ApiIndexRootContentServiceImpl extends ApiBaseServiceImpl {


    private static final String businessID = INDEX_ROOT_CONTENT;

    /** 前台文件服务类 */
    @Autowired
    private FrontFsFileService frontFsFileService;
    /** 文档群组服务类 */
    @Autowired
    private FrontDocGroupService frontDocGroupService;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;
    @Override
    public String getBusinessID() {
        return businessID;
    }

    /**
     * @param params 参数userId
     * @return Response
     * @description: 获取目录
     * @Title: execute
     * @author:zhongguangrui
    */
    @Override
    public Response execute(HashMap<String,String> params) {
        Response response = new Response();
        try {
            // 获取当前登录人
            String userId = params.get("userId");
            List<String> listGroup = frontDocGroupService.getPremission(userId);
            List<String> roleList = sysUserRoleMapper.getRolesByUserId(userId);
            //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
            Integer adminFlag = CommonUtil.getAdminFlag(roleList);
            // 查询首页的文件目录
            List<FsFolder> fsList = frontFsFileService.getFsFileListMobile(userId, listGroup, adminFlag);
            response.setSuccess(true);
            response.setData(fsList);
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
