package com.jxdinfo.doc.client.empstatisticsmanager.controller;

import com.alibaba.fastjson.JSONObject;
import com.jxdinfo.doc.client.response.ApiResponse;
import com.jxdinfo.doc.common.jwt.util.JWTUtil;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.common.util.StringUtil;
import com.jxdinfo.doc.manager.groupmanager.service.DocGroupService;
import com.jxdinfo.doc.manager.statistics.dao.EmpStatisticsMapper;
import com.jxdinfo.doc.manager.statistics.service.EmpStatisticsService;
import com.jxdinfo.hussar.bsp.permit.service.ISysUserRoleService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
@RequestMapping("/client/empStatistics")
public class ClientEmpStatisticsController {
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


    @Autowired
    private ISysUserRoleService sysUserRoleService;

    @Resource
    private JWTUtil jwtUtil;
    /**
     * 跳转到个人文件大小页面
     * @author      YJS
     * @return      java.lang.String
     * @date        2018/8/28 11:09
     */
    @RequiresPermissions("empStatistics:view")
    @RequestMapping("/empView")
    public String empView() {
        return "/doc/manager/statistics/empStatistics.html";
    }


    /**
     * 获取列表页
     * @author      yjs
     * @return     JSON
     * @date        2018/8/28
     */
    @RequestMapping("/empStatisticsList")
    @ResponseBody
    public ApiResponse getEmpStatisticsList(String groupId, String uerName, int page, int limit) {
        int beginIndex = page * limit - limit;
        String userName = StringUtil.transferSqlParam(uerName);
        JSONObject json = new JSONObject();
        if (groupId==null|| "".equals(groupId)){
            json.put("count", 0);
            json.put("data", null);
            json.put("msg", "success");
            json.put("code", 0);
            //开始位置
            return ApiResponse.data(200,json,"");
        }else {
            List<Map<String, Object>> list = empStatisticsService.getEmpStatisticsData(groupId, userName, beginIndex, limit);
            int counts = empStatisticsMapper.getUserListCount(groupId, userName);
            json.put("count", counts);
            json.put("data", list);
            json.put("msg", "success");
            json.put("code", 0);
            //开始位置
            return ApiResponse.data(200,json,"");
        }
    }

    /**
     * 修改个人空间的容量
     * @author      yjs
     * @return     JSON
     * @date        2018/8/28
     */
    @RequestMapping("/updateSpace")
    @ResponseBody
    public ApiResponse updateSpace(String id,String space) {
        empStatisticsService.updateSpace(id,space);
        JSONObject json = new JSONObject();
        json.put("msg", "success");
        json.put("code", 0);
        return ApiResponse.data(200,json,"");
    }


    /**
     * 显示个人剩余容量
     * @author      yjs
     * @return     JSON
     * @date        2018/8/31
     */
    @RequestMapping("/showSize")
    @ResponseBody
    public ApiResponse showSize() {
        String userId = jwtUtil.getSysUsers().getUserId();
        List<String> roleList = sysUserRoleService.getRolesByUserId(userId);
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        JSONObject json = new JSONObject();
        if (adminFlag != 1) {
            Map<String,String> space = empStatisticsService.getSpaceByUserId(userId,adminFlag);
            json.put("total",space.get("total"));
            json.put("lack",space.get("lack"));
            json.put("limit",space.get("limit"));
            json.put("present",space.get("present"));
        }else{
            json.put("limit","0");
        }

        return ApiResponse.data(200,json,"");
    }

}
