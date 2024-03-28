package com.jxdinfo.doc.mobile.service.impl;

import com.jxdinfo.doc.front.docsharemanager.service.ShareResourceService;
import com.jxdinfo.doc.manager.docintegral.service.IntegralRuleService;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.mobile.model.Response;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jxdinfo.doc.mobile.constants.ApiConstants.SHARE_LINK;


/**
 * 主页最新动态显示
 */
@Component
public class ApiShareLinkServiceImpl extends ApiBaseServiceImpl {


    private static final String businessID = SHARE_LINK;
    /**
     * 缓存工具服务类
     */
    /**
     * 积分记录服务
     */
    @Resource
    private IntegralRuleService integralRuleService;

    /**
     * 分享服务
     */
    @Resource
    private ShareResourceService shareResourceService;

    /**
     * 文档信息服务类
     */
    @Resource
    private DocInfoService idocInfoService;
    /** 前台文件服务类 */

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
            String userId = params.get("userId");
            String fileId = params.get("docId");
            String fileType = params.get("fileType");
            Map share = new HashMap();
            Map map1= shareResourceService.newShareResourceMobile(fileId, fileType,0,1,null,userId);
            Map map2= shareResourceService.newShareResourceMobile(fileId, fileType,1,1,null,userId);
            Map map3= shareResourceService.newShareResourceMobile(fileId, fileType,0,7,null,userId);
            Map map4= shareResourceService.newShareResourceMobile(fileId, fileType,1,7,null,userId);
            Map map5= shareResourceService.newShareResourceMobile(fileId, fileType,0,0,null,userId);
            Map map6= shareResourceService.newShareResourceMobile(fileId, fileType,1,0,null,userId);
            share.put("map1",map1);
            share.put("pmap1",map2);
            share.put("map7",map3);
            share.put("pmap7",map4);
            share.put("map0",map5);
            share.put("pmap0",map6);

            response.setSuccess(true);
            response.setData(share);

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
