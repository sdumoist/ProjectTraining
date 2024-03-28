package com.jxdinfo.doc.mobileapi.downloadmanager.controller;

import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.common.jwt.util.JWTUtil;
import com.jxdinfo.doc.common.jwt.util.RemoteIpMobileUtil;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.manager.folderextranetauth.service.IFolderExtranetAuthService;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.groupmanager.service.DocGroupService;
import com.jxdinfo.doc.manager.personalcenter.service.PersonalOperateService;
import com.jxdinfo.doc.mobileapi.foldermanager.service.IMobileFsFolderService;
import com.jxdinfo.hussar.bsp.organ.dao.SysStruMapper;
import com.jxdinfo.hussar.bsp.organ.model.SysStru;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@Controller
@RequestMapping("/mobile/download")
public class MobileDownloadController {
    /**
     * 个人操作的服务
     */
    @Resource
    private PersonalOperateService operateService;

    @Autowired
    private DocGroupService docGroupService;

    /**
     * 目录服务类
     */
    @Resource
    private BusinessService businessService;

    @Resource
    private JWTUtil jwtUtil;

    @Resource
    private SysStruMapper sysStruMapper;


    @Autowired
    private ISysUsersService iSysUsersService;

    @Autowired
    private IMobileFsFolderService fsMobileFolderService;

    @Autowired
    private IFolderExtranetAuthService iFolderExtranetAuthService;

    @Value("${openExtranetLimit}")
    private String openExtranetLimit;
    /**
     * @author luzhanzhao
     * @param name 关键字
     * @param pageNumber 当前页数
     * @param pageSize 每页长度
     * @return 获取到的个人下载记录集合
     */
    @RequestMapping("/list")
    @ResponseBody
    public Map list(String name, String[] typeArr,@RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber, @RequestParam(defaultValue = "60") int pageSize,String order, HttpServletRequest request){
        int beginIndex = pageNumber * pageSize - pageSize;
        String userId = jwtUtil.getSysUsers().getUserId();
        String deptId = iSysUsersService.getById(userId).getDepartmentId();
        String orgId = "";
        SysStru stru = sysStruMapper.selectById(deptId);
        if(stru!=null){
            orgId = stru.getOrganAlias();
        }
        Map histories = new HashMap();
        //获取当前登录人
        FsFolderParams fsFolderParams = new FsFolderParams();
        List<String> listGroup = docGroupService.getPremission(userId);
        Integer adminFlag = CommonUtil.getAdminFlag(listGroup);

        List<String> folderExtranetIds = null;
        // 开启了外网访问限制
        if (adminFlag != 1 && StringUtils.equals(openExtranetLimit, "true")) {
            // 用户是从外网访问的系统
            if (adminFlag!=1 && RemoteIpMobileUtil.isExtranetVisit(request)) {

                folderExtranetIds = iFolderExtranetAuthService.getFolderExtranetListMobile();
                if (folderExtranetIds == null || folderExtranetIds.size() == 0) { // 没有外网可以访问的目录
                    System.out.println("===================没有配置外网可以访问的目录=====================");
                    histories.put("msg","success");
                    histories.put("code",0);
                    histories.put("rows", null);
                    return histories;
                }

            }
        }

        fsFolderParams.setUserId(userId);
        fsFolderParams.setGroupList(listGroup);
        //获得目录管理权限层级码
        fsFolderParams.setType("2");
        String levelCode = businessService.getLevelCodeByUserUploadMobile(userId,fsFolderParams);
        List<Map> list = operateService.getMyHistoryMobile(userId, "4", beginIndex, pageSize, name,typeArr,order,levelCode,orgId,folderExtranetIds);
        int count = operateService.getMyHistoryCountMobile(userId, "4", name,folderExtranetIds);

        histories.put("msg","success");
        histories.put("code",0);
        histories.put("rows",list);
        histories.put("count",count);
        return histories;
    }
}
