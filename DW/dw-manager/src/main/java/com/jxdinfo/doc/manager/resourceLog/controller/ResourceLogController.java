package com.jxdinfo.doc.manager.resourceLog.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jxdinfo.doc.common.util.StringUtil;
import com.jxdinfo.doc.manager.resourceLog.model.ResourceLog;
import com.jxdinfo.doc.manager.resourceLog.service.docResourceLogService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @ClassName: ResourceLogController
 * @Description:TODO
 * @Author: lishilin
 * @Date: 2019/11/13
 * @Version: 1.0
 */
@Controller
@RequestMapping("/resource")
public class ResourceLogController {

    @Autowired
    private docResourceLogService docResourceLogService;

    @GetMapping("/resourceListView")
    @RequiresPermissions("resource:resourceListView")
    public String topicListView() {
        return "/doc/manager/docresourcelog/resource-list.html";
    }

    /**
     * 专题信息列表查询
     *
     * @return 专题列表
     */
    @GetMapping("/resourceList")
    @ResponseBody
    public JSON getTopicList(String resourceName, int page, int limit) {
        int beginIndex = page * limit - limit;
        //开始位置

        String resourceNameStr = StringUtil.transferSqlParam(resourceName);
        List<ResourceLog> ResourceLogList = docResourceLogService.ResourceLogList(resourceNameStr, beginIndex, limit);
        int ResourceLogCount = docResourceLogService.ResourceLogListCount(resourceNameStr);
        JSONObject json = new JSONObject();
        json.put("count", ResourceLogCount);
        json.put("data", ResourceLogList);
        json.put("msg", "success");
        json.put("code", 0);
        return json;
    }

}
