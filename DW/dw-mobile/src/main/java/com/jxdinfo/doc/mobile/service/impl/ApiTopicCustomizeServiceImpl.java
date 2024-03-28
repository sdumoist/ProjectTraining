package com.jxdinfo.doc.mobile.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxdinfo.doc.front.topicmanager.model.DocUserTopic;
import com.jxdinfo.doc.front.topicmanager.service.DocUserTopicService;
import com.jxdinfo.doc.mobile.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jxdinfo.doc.mobile.constants.ApiConstants.TOPIC_LIST_CUSTOMIZE;


/**
 * 定制显示专题、顺序
 */
@Component
public class ApiTopicCustomizeServiceImpl extends ApiBaseServiceImpl {


    private static final String businessID = TOPIC_LIST_CUSTOMIZE;
    /**
     * 用户专题关联表服务
     */
    @Autowired
    private DocUserTopicService docUserTopicService;
    @Override
    public String getBusinessID() {
        return businessID;
    }

    /**
     * @param params 参数,userId 当前用户;topicIds:按顺序排的topicId，逗号分隔
     * @return Response
     * @description: 定制显示专题、顺序
     * @Title: execute
     * @author:zhongguangrui
    */
    @Override
    public Response execute(HashMap<String,String> params) {
        Response response = new Response();
        try {
            String userId = String.valueOf(params.get("userId"));
            String topicIds = String.valueOf(params.get("topicIds"));

            String[] topicIdArray = topicIds.split(",");
            List<DocUserTopic> list = new ArrayList<>();
            for (int i = 0; i < topicIdArray.length; i ++){
                DocUserTopic docUserTopic = new DocUserTopic(userId,topicIdArray[i],i + 1);
                list.add(docUserTopic);
            }
            boolean deleteFlag = docUserTopicService.remove(new QueryWrapper<DocUserTopic>()
                    .eq("user_id",userId));
            boolean insertFlag = docUserTopicService.saveBatch(list);
            Map<String,Object> map = new HashMap<>();
            map.put("result",deleteFlag && insertFlag);
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
