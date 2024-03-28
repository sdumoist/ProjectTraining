package com.jxdinfo.doc.Synchronous.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jxdinfo.doc.Synchronous.service.SynchronousService;
import com.jxdinfo.hussar.core.base.controller.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/syncData")
public class SyncDataController extends BaseController {

    /**
     * 同步用户组织机构接口
     */
    @Autowired
    private SynchronousService synchronousService;

    /**
     * 日志
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(SyncDataController.class);

    /**
     * 跳转到同步页面
     * @return 同步页面
     */
    @RequestMapping("/gotoSyncDataPage")
    public String initSyncData() {
        return "/doc/manager/system/syncData.html";
    }

    /**
     * 同步用户
     * @return json
     */
    @RequestMapping("/syncUser")
    @ResponseBody
    public JSON syncUser() {
        long time1 = System.currentTimeMillis();
        boolean flag = true;
        JSONObject json = new JSONObject();
        try {
            synchronousService.synchronousUser();
        } catch (Exception e) {
            json.put("isSuccess", false);
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
     * @return json
     */
    @RequestMapping("/syncOrgan")
    @ResponseBody
    public JSON syncOrgan() {
        long time1 = System.currentTimeMillis();
        boolean flag = true;
        JSONObject json = new JSONObject();
        try {
            synchronousService.synchronousOrgan("1");
        } catch (Exception e) {
            json.put("isSuccess", false);
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
}
