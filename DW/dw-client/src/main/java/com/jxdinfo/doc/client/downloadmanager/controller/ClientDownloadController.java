package com.jxdinfo.doc.client.downloadmanager.controller;

import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.common.jwt.util.JWTUtil;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.foldermanager.service.IDocFoldAuthorityService;
import com.jxdinfo.doc.manager.groupmanager.service.DocGroupService;
import com.jxdinfo.doc.manager.personalcenter.service.PersonalOperateService;
import com.jxdinfo.hussar.bsp.organ.dao.SysStruMapper;
import com.jxdinfo.hussar.bsp.permit.service.ISysUserRoleService;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/client/download")
public class ClientDownloadController {
    /**
     * 个人操作的服务
     */
    @Resource
    private PersonalOperateService operateService;

    @Autowired
    private DocGroupService docGroupService;
    /**
     * 文档管理服务类
     */
    @Autowired
    private IDocFoldAuthorityService docFoldAuthorityService;
    @Resource
    private  ISysUserRoleService sysUserRoleService;
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
    /**
     * @author luzhanzhao
     * @param name 关键字
     * @param pageNumber 当前页数
     * @param pageSize 每页长度
     * @return 获取到的个人下载记录集合
     */
    @RequestMapping("/list")
    @ResponseBody
    public Map list(String name, String[] typeArr,@RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber, @RequestParam(defaultValue = "60") int pageSize,String order){
        int beginIndex = pageNumber * pageSize - pageSize;
        String userId = jwtUtil.getSysUsers().getUserId();
        String deptId =docFoldAuthorityService.getDeptIds( iSysUsersService.getById(userId).getDepartmentId());

        //获取当前登录人
        FsFolderParams fsFolderParams = new FsFolderParams();
        List<String> listGroup = docGroupService.getPremission(userId);
        fsFolderParams.setUserId(userId);
        fsFolderParams.setGroupList(listGroup);
        List roleList = sysUserRoleService.getRolesByUserId(userId);
        fsFolderParams.setRoleList(roleList);
        //获得目录管理权限层级码
        fsFolderParams.setType("2");
        String levelCode = businessService.getLevelCodeByUserUpload(fsFolderParams);
        List<Map> list = operateService.getMyHistory(userId, "4", beginIndex, pageSize, name,typeArr,order,levelCode,deptId);
        int count = operateService.getMyHistoryCount(userId, "4", name);
        Map histories = new HashMap();
        histories.put("msg","success");
        histories.put("code",0);
        histories.put("rows",list);
        histories.put("count",count);
        return histories;
    }


}
