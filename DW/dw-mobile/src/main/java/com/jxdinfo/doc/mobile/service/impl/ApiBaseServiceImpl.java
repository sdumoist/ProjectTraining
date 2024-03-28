package com.jxdinfo.doc.mobile.service.impl;


import com.jxdinfo.doc.mobile.model.Response;
import com.jxdinfo.doc.mobile.service.ApiBaseService;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.HashMap;

/**
 * 类的用途：service实现类父类<p>
 * 创建日期：2017-06-08<br>
 * 修改历史：<br>
 * 修改日期：2017-10-12<br>
 * 修改作者：zhaoAi <br>
 * 修改内容：修改内容 <br>
 * @author zhaoAi
 * @version 1.0
 */
@Service
public class ApiBaseServiceImpl implements ApiBaseService {

    /**
      * @description: 获得业务ID
      * @Title: getBusinessID
      * @author: zhaoAi
      * @return String
      */
    @Override
    public String getBusinessID() {
        return "";
    }

    /**
      * @description: 具体操作
      * @Title: execute
      * @author: zhaoAi
      * @param params 参数
      * @return Response
      */
    @Override
    public Response execute(HashMap<String,String> params) throws ParseException {
        return null;
    }

}
