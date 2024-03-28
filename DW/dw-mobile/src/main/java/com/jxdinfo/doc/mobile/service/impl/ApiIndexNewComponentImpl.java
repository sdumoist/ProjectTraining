package com.jxdinfo.doc.mobile.service.impl;

import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.util.ESUtil;
import com.jxdinfo.doc.common.util.FileTool;
import com.jxdinfo.doc.front.docmanager.service.FrontFsFileService;
import com.jxdinfo.doc.manager.componentmanager.model.ComponentApply;
import com.jxdinfo.doc.manager.componentmanager.service.ComponentApplyService;
import com.jxdinfo.doc.manager.docmanager.ex.ServiceException;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.personalcenter.service.PersonalOperateService;
import com.jxdinfo.doc.mobile.model.Response;
import com.jxdinfo.doc.mobile.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

import static com.jxdinfo.doc.mobile.constants.ApiConstants.COMPONENT_INDEX;


/**
 * 主页最新动态显示
 */
@Component
public class ApiIndexNewComponentImpl extends ApiBaseServiceImpl {


    private static final String businessID = COMPONENT_INDEX;


    /** 科研成功服务类 */
    @Autowired
    private ComponentApplyService componentApplyService;


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

       List< ComponentApply > list = componentApplyService.componentListMobile(null,null,2,0,10,null,null,null,null,null);
            List< ComponentApply > listNew = changeDate(list);
            response.setSuccess(true);
            response.setData(listNew);

        } catch (Exception e) {
            e.printStackTrace();
            response.setSuccess(false);
            response.setData(false);
            response.setMsg(e.getMessage());
        }
        response.setBusinessID(businessID);
        return response;
    }
    public List<ComponentApply> changeDate(List<ComponentApply> list) {
        for (ComponentApply componentApply : list) {
            Timestamp ts = componentApply.getCreateTime();
            Long tsLong=  ts.getTime();
            Long nowTs= new Date().getTime();
            Long lackTs = nowTs -tsLong;
            if( lackTs < 1000*60 ){
                componentApply.setShowTime("刚刚");
            }else if (lackTs >=  1000*60 && lackTs < 1000*60*60 ) {

                componentApply.setShowTime(lackTs/(1000*60)+"分钟前");

            }else if(lackTs >=1000*60*60 && lackTs<1000*60*60*24 ){
                componentApply.setShowTime(lackTs/(1000*60*60)+"小时前");
            }
            else if(lackTs >=1000*60*60*24 && lackTs<1000*60*60*24*7 ){
                componentApply.setShowTime(lackTs/(1000*60*60*24)+"天前");
            }else {
                String str= ConvertUtil.changeTime(ts);
                componentApply.setShowTime(str);
            }

        }
        return list;
    }
}
