package com.jxdinfo.doc.mobile.service.impl;

import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.util.ESUtil;
import com.jxdinfo.doc.common.util.FileTool;
import com.jxdinfo.doc.front.docmanager.service.FrontDocInfoService;
import com.jxdinfo.doc.front.docmanager.service.FrontFsFileService;
import com.jxdinfo.doc.manager.docmanager.ex.ServiceException;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.groupmanager.service.DocGroupService;
import com.jxdinfo.doc.manager.personalcenter.service.PersonalOperateService;
import com.jxdinfo.doc.mobile.model.Response;
import com.jxdinfo.doc.mobile.util.ConvertUtil;
import com.jxdinfo.hussar.bsp.permit.dao.SysUsersMapper;
import com.jxdinfo.hussar.bsp.permit.model.SysUsers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jxdinfo.doc.mobile.constants.ApiConstants.DOC_DETAIL;
import static com.jxdinfo.doc.mobile.constants.ApiConstants.USER_INFO;


/**
 * 主页最新动态显示
 */
@Component
public class ApiGetUserInfoImpl extends ApiBaseServiceImpl {


    private static final String businessID = USER_INFO;
    /**
     * 缓存工具服务类
     */
    @Autowired
    private CacheToolService cacheToolService;

    @Autowired
    private DocGroupService docGroupService;
    @Autowired
    private ESUtil esUtil;

    @Resource
    private PersonalOperateService operateService;
    /** 前台文件服务类 */
    @Autowired
    private FrontFsFileService frontFsFileService;

    /**
     * 目录服务类
     */
    @Resource
    private SysUsersMapper  sysUsersMapper;
    @Autowired

    @Override
    public String getBusinessID() {
        return businessID;
    }

    /**
     * @param params 参数
     * @return Response
     * @description: 删除
     * @Title: execute
     * @author: yjs
     */
    @Override
    public Response execute(HashMap<String,String> params) {
        Response response = new Response();

            String userId = params.get("userId");
        SysUsers sysUsers=sysUsersMapper.selectById(userId);
        response.setData(sysUsers);
        response.setSuccess(true);
        response.setBusinessID(businessID);
        return response;
    }
}
