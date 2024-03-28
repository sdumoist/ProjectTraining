package com.jxdinfo.doc.mobile.service.impl;

import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.mobile.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jxdinfo.doc.mobile.constants.ApiConstants.INDEX_UPLOAD_RANK;


/**
 * 主页贡献榜获取
 */
@Component
public class ApiIndexUploadRankServiceImpl extends ApiBaseServiceImpl {


    private static final String businessID = INDEX_UPLOAD_RANK;

    /** 缓存工具服务类 */
    @Autowired
    private CacheToolService cacheToolService;

    @Override
    public String getBusinessID() {
        return businessID;
    }

    /**
     * @param params 参数：num
     * @return Response
     * @description: 获取贡献榜（上传量排名）
     * @Title: execute
     * @author:zhongguangrui
    */
    @Override
    public Response execute(HashMap<String,String> params) {
        Response response = new Response();
        try {
            String type="user";
            Integer num = Integer.parseInt(params.get("num"));
            List<Map> list =cacheToolService.getUploadData(type);
            num = num > list.size() ? list.size() : num;
            response.setSuccess(true);
            response.setData(list.subList(0,num));
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
