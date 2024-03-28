package com.jxdinfo.doc.mobile.service.impl;

import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.util.FileTool;
import com.jxdinfo.doc.front.docmanager.service.FrontFsFileService;
import com.jxdinfo.doc.manager.docmanager.ex.ServiceException;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.model.DocResourceLog;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.personalcenter.service.PersonalOperateService;
import com.jxdinfo.doc.mobile.model.Response;
import com.jxdinfo.doc.mobile.util.ConvertUtil;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import com.jxdinfo.hussar.core.support.HttpKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.*;

import static com.jxdinfo.doc.mobile.constants.ApiConstants.DOC_COLLECTION;


/**
 * 主页最新动态显示
 */
@Component
public class ApiDocCollectionServiceImpl extends ApiBaseServiceImpl {


    private static final String businessID = DOC_COLLECTION;
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
            String docId = String.valueOf(params.get("docId"));
            String userId = params.get("userId");

            String collectionState=params.get("collectionState");
            if("0".equals(collectionState)){
                List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
                //获取当前时间戳
                Timestamp ts = new Timestamp(System.currentTimeMillis());
                //新建操作记录
                DocResourceLog docResourceLog = new DocResourceLog();
                //获取uuid的方法
                String id = UUID.randomUUID().toString().replace("-", "");
                //获取当前登录用户
                ShiroUser shiroUser = ShiroKit.getUser();
                //封装操作记录信息
                docResourceLog.setId(id);
                docResourceLog.setResourceId(docId);
                docResourceLog.setOperateTime(ts);
                docResourceLog.setResourceType(0);
                docResourceLog.setUserId(userId);
                docResourceLog.setOperateType(5);
                docResourceLog.setOrigin("mobile");
                docResourceLog.setValidFlag("1");

                    docResourceLog.setAddressIp(HttpKit.getIp());

                resInfoList.add(docResourceLog);
                //将操作记录插入操作记录表中
                docInfoService.insertResourceLog(resInfoList);
                response.setSuccess(true);
                response.setData(true);

            }else{
                operateService.cancelCollection(docId,userId,"5");
                response.setSuccess(true);
                response.setData(true);

            }

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
