package com.jxdinfo.doc.client.sharemanager.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxdinfo.doc.client.response.ApiResponse;
import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.common.jwt.util.JWTUtil;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.front.docmanager.service.FrontDocInfoService;
import com.jxdinfo.doc.front.docsharemanager.service.ShareResourceService;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.foldermanager.service.IDocFoldAuthorityService;
import com.jxdinfo.doc.manager.groupmanager.service.DocGroupService;
import com.jxdinfo.doc.manager.sharemanager.service.IPersonalShareService;
import com.jxdinfo.hussar.bsp.organ.dao.SysStruMapper;
import com.jxdinfo.hussar.bsp.permit.service.ISysUserRoleService;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author luzhanzhao
 * @date 2018-12-10
 * @description 分享相关的控制层
 */
@Controller
@RequestMapping("/client/share")
public class ClientShareController {

    /**
     * 文档服务类
     */
    @Autowired
    private FrontDocInfoService frontDocInfoService;
    @Autowired
    private IPersonalShareService iPersonalShareService;
    @Resource
    private JWTUtil jwtUtil;

    @Resource
    private SysStruMapper sysStruMapper;
    @Autowired
    private IDocFoldAuthorityService docFoldAuthorityService;
    @Autowired
    private ISysUsersService iSysUsersService;

    @Autowired
    private DocGroupService docGroupService;
    @Autowired
    private ISysUserRoleService sysUserRoleService;
    /**
     * 目录服务类
     */
    @Resource
    private BusinessService businessService;
    /**
     * 分享服务
     */
    @Resource
    private ShareResourceService shareResourceService;


    /**
     * @param fileId    文件id
     * @param fileType  文件类型
     * @param pwdFlag   有无提取码
     * @param validTime 有效期（0对应永久）
     * @param request
     * @return 分享结果
     * @author luzhanzhao
     * @date 2018-12-11
     * @description 分享链接
     */
    @RequestMapping("/shareHref")
    @ResponseBody
    public ApiResponse shareHref(String fileId, String fileType, @RequestParam(defaultValue = "0") int pwdFlag, @RequestParam(defaultValue = "0") int validTime,@RequestParam(defaultValue = "0") int authority,
                                 HttpServletRequest request) {
        String userId = jwtUtil.getSysUsers().getUserId();
        return ApiResponse.data(200,shareResourceService.newShareResourceClient(fileId, fileType, pwdFlag, validTime,authority, request,userId),"");
    }
    @RequestMapping("/list")
    @ResponseBody
    public ApiResponse list(String name, @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber, @RequestParam(defaultValue = "60") int pageSize, String order,String timeType){
        int beginIndex = pageNumber * pageSize - pageSize;
        String userId = jwtUtil.getSysUsers().getUserId();
        List<String> roleList = sysUserRoleService.getRolesByUserId(userId);
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        //获取当前登录人
        FsFolderParams fsFolderParams = new FsFolderParams();
        String deptId = docFoldAuthorityService.getDeptIds(iSysUsersService.getById(userId).getDepartmentId());

        List<String> listGroup = docGroupService.getPremission(userId);
        fsFolderParams.setUserId(userId);
        fsFolderParams.setGroupList(listGroup);
        //获得目录管理权限层级码
        fsFolderParams.setRoleList(roleList);
        fsFolderParams.setType("2");
        fsFolderParams.setUserId(userId);
        fsFolderParams.setGroupList(listGroup);
        //获得目录管理权限层级码
        fsFolderParams.setType("2");
        String levelCode = businessService.getLevelCodeByUserUploadClient(fsFolderParams,deptId);

        Map<String,Object> map = iPersonalShareService.getMyShareHistory(userId,name,order,beginIndex,pageSize,timeType,levelCode,deptId,roleList);
        return ApiResponse.data(200,map,"");
    }
    @RequestMapping("/cancelShare")
    @ResponseBody
    public ApiResponse cancelShare(String docId){
        Map <String,Object> map = new HashMap<>();
        QueryWrapper wrapper = new QueryWrapper();
        boolean  isSuccess = iPersonalShareService.removeById(docId);
        return  ApiResponse.data(200,isSuccess,"");
    }
}