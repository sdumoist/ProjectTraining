package com.jxdinfo.doc.mobile.service.impl;

import com.jxdinfo.doc.manager.docintegral.service.IntegralRecordService;
import com.jxdinfo.doc.mobile.model.Response;
import com.jxdinfo.hussar.bsp.organ.dao.SysStruMapper;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

import static com.jxdinfo.doc.mobile.constants.ApiConstants.PERSONAL_MESSAGE;


/**
 * 手机端我的信息展示
 */
@Component
public class ApiPersonalMessageServiceImpl extends ApiBaseServiceImpl {


    private static final String businessID = PERSONAL_MESSAGE;

    @Autowired
    private ISysUsersService iSysUsersService;

    @Resource
    private SysStruMapper  sysStruMapper;

    @Autowired
    private IntegralRecordService integralRecordService;

    @Override
    public String getBusinessID() {
        return businessID;
    }

    /**
     * @param params 参数
     * @return Response
     * @description: 获取我的组织机构、积分
     * @Title: execute
     * @author: wst
     */
    @Override
    public Response execute(HashMap<String,String> params) {
        Response response = new Response();
        try {
            String userId = params.get("userId");
            String organId = iSysUsersService.getById(userId).getDepartmentId();
            String organName=sysStruMapper.selectById(organId).getOrganAlias();
            Integer totalIntegral =integralRecordService.showIntegral(userId);
            Map myMessage = new HashMap();
            myMessage.put("userId",userId);
            myMessage.put("organId",organId);
            myMessage.put("organName",organName);
            myMessage.put("integral",totalIntegral);
            response.setSuccess(true);
            response.setData(myMessage);
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
