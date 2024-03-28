package com.jxdinfo.doc.interfaces.system.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jxdinfo.doc.interfaces.system.model.YYZCOrganise;
import com.jxdinfo.doc.interfaces.system.model.YYZCUser;
import com.jxdinfo.doc.interfaces.system.service.YYZCOrganiseService;
import com.jxdinfo.doc.interfaces.system.service.YYZCUserService;
import com.jxdinfo.doc.timer.client.ApiClient;
import com.jxdinfo.hussar.core.base.controller.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/synchronousData")
public class SynchronousDataController extends BaseController {

    /**
     * 同步用户服务接口
     */
    @Autowired
    private YYZCUserService yyzcUserService;

    /**
     * 同步组织机构接口
     */
    @Autowired
    private YYZCOrganiseService yyzcOrganiseService;

    /**
     * 日志
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(SynchronousDataController.class);

    /**
     * 跳转到同步页面
     * @Title: initSyschronousData 
     * @author: XuXinYing
     * @return 同步页面
     */
    @RequestMapping("/tasklist")
    public String initSynchronousData() {
        return "/doc/manager/system/synchronousData.html";
    }

    /**
     * 同步运营支撑用户
     * @Title: getYyzcUser 
     * @author: XuXinYing
     * @return json
     */
    @RequestMapping("/getYyzcUser")
    @ResponseBody
    public JSON getYyzcUser() {
        long time1 = System.currentTimeMillis();
        boolean flag = true;
        final ApiClient client = new ApiClient();
        final String userList = client.userList();
        List<YYZCUser> userInfoList = new ArrayList<YYZCUser>();
        userInfoList = JSONObject.parseArray(userList, YYZCUser.class);

        JSONObject json = new JSONObject();
        try {
            flag = this.yyzcUserService.insertOrUpdateYyzcUser(userInfoList);
        } catch (Exception e) {
            json.put("isSuccess", flag);
            e.printStackTrace();
            json.put("msg", e.getCause().toString());
            return json;
        }
        json.put("isSuccess", flag);
        long time2 = System.currentTimeMillis();
        System.out.println("同步用户的时间是：" + (time2 - time1) + "ms");
        json.put("msg", "同步成功,同步用户的时间是" + (time2 - time1) + "ms");
        return json;
    }

    /**
     * 同步运营支撑组织机构
     * @Title: getYyzcOrganise 
     * @author: XuXinYing
     * @return json
     */
    @RequestMapping("/getYyzcOrganise")
    @ResponseBody
    public JSON getYyzcOrganise() {
        long time1 = System.currentTimeMillis();
        boolean flag = true;
        ApiClient client = new ApiClient();
        String organiseList = client.orgOrganise();
        List<YYZCOrganise> organisesInfoList = new ArrayList<YYZCOrganise>();
        organisesInfoList = JSONObject.parseArray(organiseList, YYZCOrganise.class);
        JSONObject json = new JSONObject();
        try {
            flag = this.yyzcOrganiseService.insertOrUpdateYYZCOrganise(organisesInfoList);
        } catch (Exception e) {
            json.put("isSuccess", flag);
            e.printStackTrace();
            json.put("msg", e.getCause().toString());
            return json;
        }
        json.put("isSuccess", flag);
        long time2 = System.currentTimeMillis();
        System.out.println("同步组织机构的时间是：" + (time2 - time1) + "ms");
        json.put("msg", "同步成功, 同步组织机构的时间是"+(time2 - time1) + "ms");
        return json;
    }

    /**
     * 同步运营支撑头像
     * @Title: getYyzcHeadPhoto 
     * @author: XuXinYing
     * @return json
     */
    @RequestMapping("/getYyzcHeadPhoto")
    @ResponseBody
    public JSON getYyzcHeadPhoto() {
        long time1 = System.currentTimeMillis();
        boolean flag = true;
        JSONObject json = new JSONObject();
        try {
            flag = this.yyzcUserService.getUserPhotoInfo();
        } catch (Exception e) {
            json.put("isSuccess", flag);
            e.printStackTrace();
            json.put("msg", e.getCause().toString());
            return json;
        }
        long time2 = System.currentTimeMillis();
        json.put("msg", "同步成功,同步头像的时间是" + (time2 - time1) + "ms");
        json.put("isSuccess", flag);
        return json;
    }
}
