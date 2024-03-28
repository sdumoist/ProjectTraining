package com.jxdinfo.doc.mobile.service.impl;

import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.util.ESUtil;
import com.jxdinfo.doc.common.util.FileTool;
import com.jxdinfo.doc.front.docmanager.service.FrontFsFileService;
import com.jxdinfo.doc.manager.componentmanager.model.ComponentApply;
import com.jxdinfo.doc.manager.componentmanager.service.ComponentApplyService;
import com.jxdinfo.doc.manager.personalcenter.service.PersonalOperateService;
import com.jxdinfo.doc.mobile.model.Response;
import com.jxdinfo.doc.mobile.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jxdinfo.doc.mobile.constants.ApiConstants.COMPONENT_LIST;


/**
 * 主页最新动态显示
 */
@Component
public class ApiComponentListImpl extends ApiBaseServiceImpl {


    private static final String businessID = COMPONENT_LIST;

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
        Map map=new HashMap();
        try {
            String organId = params.get("orgId");
            String componentName = params.get("componentName");
            String componentType = params.get("componentType");
            String componentState= params.get("componentState");

            Integer componentTypeInt=null;
            Integer componentStateInt=null;
            Integer componentOriginInt=null;
            String componentOrigin = params.get("componentOrigin");
            if(componentType!=null&&!componentType.equals("")){
                componentTypeInt=Integer.parseInt(componentType);
            }

            if(componentOrigin!=null&&!componentOrigin.equals("")){
                componentOriginInt=Integer.parseInt(componentOrigin);
            }
            if(componentState!=null&&!componentState.equals("")){
                componentStateInt=Integer.parseInt(componentState);
            }
             Integer pageNumber =Integer.parseInt(String.valueOf(params.get("pageNum")));
            Integer pageSize = Integer.parseInt(String.valueOf(params.get("pageSize")));

            List< ComponentApply > list = componentApplyService.componentListMobile(componentName,componentTypeInt,componentStateInt,pageNumber * pageSize - pageSize,pageSize,componentOriginInt,null,organId,null,null);
            Integer count =componentApplyService.componentListCount(componentName,componentTypeInt,componentStateInt,componentOriginInt,null,organId,null, null, null);

            map.put("list",changeDate(list));

            map.put("Success",true);
            map.put("pageNum",pageNumber);
            map.put("pageSize",pageSize);
            map.put("count",count);
            response.setSuccess(true);
            response.setData(map);

        } catch (Exception e) {
            e.printStackTrace();
            response.setSuccess(false);
            response.setData(false);
            response.setMsg(e.getMessage());
        }
        response.setBusinessID(businessID);
      ;
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
