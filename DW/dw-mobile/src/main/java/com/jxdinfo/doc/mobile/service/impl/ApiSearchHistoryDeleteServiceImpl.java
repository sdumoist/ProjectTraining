package com.jxdinfo.doc.mobile.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxdinfo.doc.front.docsearch.model.DocHistorySearch;
import com.jxdinfo.doc.front.docsearch.service.DocHistorySearchService;
import com.jxdinfo.doc.mobile.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.jxdinfo.doc.mobile.constants.ApiConstants.SEARCH_HISTORY_DELETE;


/**
 * 清空搜索记录
 */
@Component
public class ApiSearchHistoryDeleteServiceImpl extends ApiBaseServiceImpl {


    private static final String businessID = SEARCH_HISTORY_DELETE;


    @Autowired
    private DocHistorySearchService docHistorySearchService;

    @Override
    public String getBusinessID() {
        return businessID;
    }

    /**
     * @param params 参数
     * @return Response
     * @description: 删除搜索历史纪录
     * @Title: execute
     * @author: zgr
     */
    @Override
    public Response execute(HashMap<String,String> params) {
        Response response = new Response();
        try {
            String userId = params.get("userId");
            boolean success = docHistorySearchService.remove(new QueryWrapper<DocHistorySearch>()
                    .eq("user_id",userId));
            Map map = new HashMap<>();
            map.put("result",success);
            response.setSuccess(true);
            response.setData(map);

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
