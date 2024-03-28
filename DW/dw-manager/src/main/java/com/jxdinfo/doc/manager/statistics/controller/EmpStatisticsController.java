package com.jxdinfo.doc.manager.statistics.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.common.util.StringUtil;
import com.jxdinfo.doc.common.util.UserInfoUtil;
import com.jxdinfo.doc.manager.groupmanager.service.DocGroupService;
import com.jxdinfo.doc.manager.statistics.dao.DeptStatisticsMapper;
import com.jxdinfo.doc.manager.statistics.dao.EmpStatisticsMapper;
import com.jxdinfo.doc.manager.statistics.service.DeptStatisticsService;
import com.jxdinfo.doc.manager.statistics.service.EmpStatisticsService;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: EmpStatisticsController
 * @Description:TODO
 * @Author: lishilin
 * @Date: 2019/12/4
 * @Version: 1.0
 */
@Controller
@RequestMapping("/empStatistics")
public class EmpStatisticsController {
    @Resource
    private EmpStatisticsMapper empStatisticsMapper;
    /**
     * 部门文件大小 Service接口
     */
    @Autowired
    private EmpStatisticsService empStatisticsService;

    /** 文档群组服务类 */
    @Autowired
    private DocGroupService docGroupService;


    /**
     * 跳转到个人文件大小页面
     * @author      YJS
     * @return      java.lang.String
     * @date        2018/8/28 11:09
     */
    @GetMapping("/empView")
    @RequiresPermissions("empStatistics:empView")
    public String empView() {
        return "/doc/manager/statistics/empStatistics.html";
    }


    /**
     * 获取列表页
     * @author      yjs
     * @return     JSON
     * @date        2018/8/28
     */
    @GetMapping("/empStatisticsList")
    @ResponseBody
    public JSON getEmpStatisticsList(String groupId, String uerName, int page, int limit) {
        int beginIndex = page * limit - limit;
        String userName = StringUtil.transferSqlParam(uerName);
        JSONObject json = new JSONObject();
        List<Map<String, Object>> list = empStatisticsService.getEmpStatisticsData(groupId, userName, beginIndex, limit);
        int counts = empStatisticsMapper.getUserListCount(groupId, userName);
        json.put("count", counts);
        json.put("data", list);
        json.put("msg", "success");
        json.put("code", 0);
        //开始位置
        return json;
    }

    /**
     * 修改个人空间的容量
     * @author      yjs
     * @return     JSON
     * @date        2018/8/28
     */
    @PostMapping("/updateSpace")
    @ResponseBody
    public JSON updateSpace(String id,String space) {
        empStatisticsService.updateSpace(id,space);
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
            Map<String,String> space = empStatisticsService.getSpaceByUserId(shiroUser.getId(),adminFlag);
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
