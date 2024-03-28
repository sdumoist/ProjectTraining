package com.jxdinfo.doc.mobileapi.sharemanager.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.common.jwt.util.JWTUtil;
import com.jxdinfo.doc.common.jwt.util.RemoteIpMobileUtil;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.front.docmanager.service.FrontDocInfoService;
import com.jxdinfo.doc.front.docsharemanager.service.ShareResourceService;
import com.jxdinfo.doc.manager.folderextranetauth.service.IFolderExtranetAuthService;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.groupmanager.service.DocGroupService;
import com.jxdinfo.doc.manager.sharemanager.service.IPersonalShareService;
import com.jxdinfo.hussar.bsp.organ.dao.SysStruMapper;
import com.jxdinfo.hussar.bsp.organ.model.SysStru;
import com.jxdinfo.hussar.bsp.permit.service.ISysUserRoleService;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
import com.jxdinfo.hussar.config.front.common.response.ApiResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author luzhanzhao
 * @date 2018-12-10
 * @description 分享相关的控制层
 */
@CrossOrigin
@Controller
@RequestMapping("/mobile/share")
public class MobileShareController {

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
    private ISysUsersService iSysUsersService;

    /**
     * 分享服务
     */
    @Resource
    private ShareResourceService shareResourceService;

    @Autowired
    private DocGroupService docGroupService;
    @Autowired
    private ISysUserRoleService sysUserRoleService;
    /**
     * 目录服务类
     */
    @Resource
    private BusinessService businessService;

    @Autowired
    private IFolderExtranetAuthService iFolderExtranetAuthService;

    @Value("${openExtranetLimit}")
    private String openExtranetLimit;

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
    public ApiResponse list(String name, @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber, @RequestParam(defaultValue = "60") int pageSize, String order, String timeType, HttpServletRequest request){
        int beginIndex = pageNumber * pageSize - pageSize;
        String userId = jwtUtil.getSysUsers().getUserId();
        List<String> roleList = sysUserRoleService.getRolesByUserId(userId);
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        //获取当前登录人
        FsFolderParams fsFolderParams = new FsFolderParams();
        String deptId = iSysUsersService.getById(userId).getDepartmentId();
        String orgId = "";
        SysStru stru = sysStruMapper.selectById(deptId);
        if(stru!=null){
            orgId = stru.getOrganAlias();
        }
        List<String> listGroup = docGroupService.getPremission(userId);

        List<String> folderExtranetIds = null;
        // 开启了外网访问限制
        if (adminFlag != 1 && StringUtils.equals(openExtranetLimit, "true")) {
            // 用户是从外网访问的系统
            if (adminFlag!=1 && RemoteIpMobileUtil.isExtranetVisit(request)) {

                folderExtranetIds = iFolderExtranetAuthService.getFolderExtranetListMobile();
                if (folderExtranetIds == null || folderExtranetIds.size() == 0) { // 没有外网可以访问的目录
                    System.out.println("===================没有配置外网可以访问的目录=====================");
                    return ApiResponse.data(200, null, "");
                }

            }
        }

        fsFolderParams.setUserId(userId);
        fsFolderParams.setGroupList(listGroup);
        //获得目录管理权限层级码
        fsFolderParams.setType("2");
        fsFolderParams.setUserId(userId);
        //获得目录管理权限层级码
        fsFolderParams.setType("2");
        String levelCode = businessService.getLevelCodeByUserUploadClient(fsFolderParams,orgId);
        Map<String,Object> map = iPersonalShareService.getMyShareHistoryMobile(userId,name,order,beginIndex,pageSize*pageNumber,timeType,levelCode,orgId, roleList,folderExtranetIds);
        return ApiResponse.data(200,map,"");
    }

    @RequestMapping("/cancelShare")
    @ResponseBody
    public ApiResponse cancelShare(String docId){
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("doc_id", docId);
        boolean  isSuccess = iPersonalShareService.remove(wrapper);
        return  ApiResponse.data(200,isSuccess,"");
    }
}