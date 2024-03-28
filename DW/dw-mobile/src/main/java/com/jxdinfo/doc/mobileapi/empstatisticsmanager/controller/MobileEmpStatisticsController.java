package com.jxdinfo.doc.mobileapi.empstatisticsmanager.controller;

import com.alibaba.fastjson.JSONObject;
import com.jxdinfo.doc.common.jwt.util.JWTUtil;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.manager.statistics.service.EmpStatisticsService;
import com.jxdinfo.hussar.bsp.permit.service.ISysUserRoleService;
import com.jxdinfo.hussar.config.front.common.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: EmpStatisticsController
 * @Author: lishilin
 * @Date: 2019/12/4
 * @Version: 1.0
 */
@CrossOrigin
@Controller
@RequestMapping("/mobile/empStatistics")
public class MobileEmpStatisticsController {

    /**
     * 部门文件大小 Service接口
     */
    @Autowired
    private EmpStatisticsService empStatisticsService;


    @Autowired
    private ISysUserRoleService sysUserRoleService;

    @Resource
    private JWTUtil jwtUtil;

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
