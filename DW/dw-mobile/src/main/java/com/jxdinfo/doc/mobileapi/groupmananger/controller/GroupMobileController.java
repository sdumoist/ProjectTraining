package com.jxdinfo.doc.mobileapi.groupmananger.controller;

import com.jxdinfo.doc.common.jwt.util.JWTUtil;
import com.jxdinfo.doc.manager.docmanager.service.DocFileAuthorityService;
import com.jxdinfo.doc.manager.foldermanager.service.IDocFoldAuthorityService;
import com.jxdinfo.doc.manager.groupmanager.service.DocGroupService;
import com.jxdinfo.hussar.bsp.permit.service.ISysIdtableService;
import com.jxdinfo.hussar.config.front.common.response.ApiResponse;
import com.jxdinfo.hussar.core.base.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/8/4 0004.
 */
@CrossOrigin
@Controller
@RequestMapping("/mobile/group")
public class GroupMobileController extends BaseController {


    /**
     * 文档群组服务类
     */
    @Autowired
    private DocGroupService docGroupService;

    /**
     * 文档目录权限服务类
     */
    @Autowired
    private IDocFoldAuthorityService docFoldAuthorityService;

    /**
     * 文档文件权限服务类
     */
    @Autowired
    private DocFileAuthorityService docFileAuthorityService;

    /**
     * 获取编号公共方法
     */
    @Autowired
    private ISysIdtableService sysIdtableService;

    @Autowired
    private JWTUtil jwtUtil;

    /**
     * @return 分组及群组信息
     * @Author zoufeng
     * @Description 获取分组及群组信息
     * @Date 16:06 2018/9/27
     * @Param id 分组或群组id
     **/
    @RequestMapping("/sortAndGroupTree")
    @ResponseBody
    public ApiResponse selectSortAndGroup(String id) {
        String userId = jwtUtil.getSysUsers().getUserId();
        List<Map<String, Object>> result = docGroupService.selectSortAndGroup(id, "", userId);
        return ApiResponse.data(200, result, "");
    }
}
