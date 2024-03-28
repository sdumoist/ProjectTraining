/*
 * ApiBaseController
 * 版权所有：金现代信息产业股份有限公司  2017-2022
 * 金现代信息产业股份有限公司保留所有权利，未经允许不得以任何形式使用。
 */
package com.jxdinfo.doc.mobile.controller;

import com.alibaba.fastjson.JSON;
import com.jxdinfo.doc.mobile.model.Request;
import com.jxdinfo.doc.mobile.model.Response;
import com.jxdinfo.doc.mobile.service.ApiBaseService;
import com.jxdinfo.doc.common.util.GzipUtil;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 类的用途：通用接口的入口（通过比较businessID来跳转具体接口）<p>
 * 创建日期：2018-8-29 <br>
 * 修改日期：2018-8-29 <br>
 * 修改作者：zlz <br>
 * 修改内容：修改内容 <br>
 *
 * @author zlz
 * @version 1.0
 */
@Controller
@CrossOrigin
public class ApiBaseController {


    @Resource
    private List<ApiBaseService> serviceList;

    private static Logger logger = LoggerFactory.getLogger(ApiBaseController.class);

    @RequestMapping(value = "/wyyServices/params", method = RequestMethod.POST)
    public void ApiBase(HttpServletRequest request, HttpServletResponse response) {
        String parameter = request.getParameter("json");
        //获取token
        Response responseJson = new Response();
        Request requestjson = new Request();
        try {
            //拼装Request对象
            JSONObject jasonObject = new JSONObject();
            Boolean isNotGzip = parameter.contains("businessID");
            if(isNotGzip){
                jasonObject = JSONObject.fromObject(parameter);
            }else{
                jasonObject = JSONObject.fromObject(GzipUtil.gunzip(parameter));
            }
            requestjson = (Request) JSONObject.toBean(jasonObject, Request.class);
            String businessID = jasonObject.getString("businessID");
            for (ApiBaseService services : serviceList) {
                if (businessID.equals(services.getBusinessID())) {
                    responseJson = services.execute(requestjson.getParams());
                    break;
                }
            }
            responseJson.setTag(requestjson.getTag());
            if(isNotGzip){
                renderJson(response, JSON.toJSONString(responseJson));
            }else{
                renderJson(response, GzipUtil.gzip(JSON.toJSONString(responseJson)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("异常ApiBaseController", e.getMessage());
            responseJson.setSuccess(false);
            renderJson(response, JSON.toJSONString(responseJson));
        }
    }

    /**
     * 发送json。使用UTF-8编码。
     *
     * @param response HttpServletResponse
     * @param text     发送的字符串
     */
    public static void renderJson(HttpServletResponse response, String text) {
        render(response, "application/json;charset=UTF-8", text);
    }

    /**
     * 发送内容。使用UTF-8编码。
     *
     * @param response
     * @param contentType
     * @param text
     */
    public static void render(HttpServletResponse response, String contentType,
                              String text) {
        response.setContentType(contentType);
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        try {
            response.getWriter().write(text);
            response.getWriter().flush();
            response.getWriter().close();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }


}
