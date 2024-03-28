package com.jxdinfo.doc.manager.statistics.controller;

import java.util.List;
import java.util.Map;

import com.jxdinfo.doc.common.util.UserInfoUtil;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.manager.statistics.service.DeptStatisticsService;
import com.jxdinfo.hussar.core.base.controller.BaseController;
import com.jxdinfo.hussar.core.shiro.ShiroKit;

/**
 * 类的用途：部门文件大小
 * 作者：yjs ;
 * 修改内容：
 * @author yjs ;
 * @version 1.0
 */
@Controller
@RequestMapping("/deptStatistics")
public class DeptStatisticsController extends BaseController {

    /**
     * 部门文件大小 Service接口
     */
    @Autowired
    private DeptStatisticsService deptStatisticsService;

    /**
     * 跳转到部门文件大小页面
     * @author      YJS
     * @return      java.lang.String
     * @date        2018/8/28 11:09
     */
    @RequiresPermissions("deptStatistics:view")
    @GetMapping("/view")
    public String view() {
        return "/doc/manager/statistics/deptStatistics.html";
    }

    /**
     * 获取列表页
     * @author      yjs
     * @return     JSON
     * @date        2018/8/28
     */
    @PostMapping("/list")
    @ResponseBody
    public JSON getTopicList(String topicName, int page, int limit) {
        int beginIndex = page * limit - limit;
        //开始位置
        List<Map<String, Object>> list = deptStatisticsService.getStatisticsData();
        JSONObject json = new JSONObject();
        json.put("data", list);
        json.put("count",100);
        json.put("msg", "success");
        json.put("code", 0);
        return json;
    }

    /**
     * 修改部门空间的容量
     * @author      yjs
     * @return     JSON
     * @date        2018/8/28
     */
    @PostMapping("/updateSpace")
    @ResponseBody
    public JSON updateSpace(String id,String space) {
        deptStatisticsService.updateSpace(id,space);
        JSONObject json = new JSONObject();
        json.put("msg", "success");
        json.put("code", 0);
        return json;
    }

    /**
     * 显示个人剩余容量
     * @author      yjs
     * @return     JSON
     * @date        2018/8/31
     */
    @PostMapping("/showSize")
    @ResponseBody
    public JSON showSize() {
        ShiroUser shiroUser= UserInfoUtil.getCurrentUser();
        List<String> roleList = ShiroKit.getUser().getRolesList();
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        JSONObject json = new JSONObject();
        if (adminFlag != 1) {
            Map<String,String> space = deptStatisticsService.getSpaceByOrganId(shiroUser.getDeptId(),adminFlag);
            json.put("total",space.get("total"));
            json.put("lack",space.get("lack"));
            json.put("limit",space.get("limit"));
            json.put("present",space.get("present"));
        }else{
            json.put("limit","0");
        }
        json.put("msg", "success");
        json.put("code", 0);
        return json;
    }
}
