package com.jxdinfo.doc.mobile.service.impl;

import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.util.FileTool;
import com.jxdinfo.doc.front.docmanager.service.FrontFsFileService;
import com.jxdinfo.doc.manager.docmanager.model.DocResourceLog;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.personalcenter.service.PersonalOperateService;
import com.jxdinfo.doc.mobile.model.Response;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.jxdinfo.doc.mobile.constants.ApiConstants.PERSONAL_CANCEL_COLLECTION;


/**
 * 主页最新动态显示
 */
@Component
public class ApiPersonalCancelCollectionServiceImpl extends ApiBaseServiceImpl {


    private static final String businessID = PERSONAL_CANCEL_COLLECTION;
    /**
     * 缓存工具服务类
     */
    @Autowired
    private CacheToolService cacheToolService;

    @Resource
    private PersonalOperateService operateService;
    /** 前台文件服务类 */
    @Autowired
    private FrontFsFileService frontFsFileService;

    @Autowired
    private FileTool fileTool;

    @Autowired
    private DocInfoService docInfoService;

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
        try {
            String docIdString = String.valueOf(params.get("docIdString"));
            String userId = params.get("userId");
            String [] docIds = docIdString.split(",");
            for( int i=0;i<docIds.length;i++){
                operateService.cancelCollection(docIds[i],userId,"5");
            }
            response.setSuccess(true);
            response.setData(true);

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
