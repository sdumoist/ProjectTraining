package com.jxdinfo.doc.mobile.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxdinfo.doc.common.util.StringUtil;
import com.jxdinfo.doc.manager.docintegral.model.IntegralRecord;
import com.jxdinfo.doc.manager.docintegral.service.IntegralRecordService;
import com.jxdinfo.doc.manager.docintegral.service.IntegralRuleService;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.mobile.model.Response;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jxdinfo.doc.mobile.constants.ApiConstants.INTEGRAL_RULE;


/**
 * 主页最新动态显示
 */
@Component
public class ApiIntegralRuleServiceImpl extends ApiBaseServiceImpl {


    private static final String businessID = INTEGRAL_RULE;
    /**
     * 缓存工具服务类
     */
    /**
     * 积分记录服务
     */
    @Resource
    private IntegralRuleService integralRuleService;

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

            integralRuleService.getIntegralRule(0,100);
            List<Map>  list =  integralRuleService.getIntegralRule(0,100);;

            response.setSuccess(true);
            response.setData(list);

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
