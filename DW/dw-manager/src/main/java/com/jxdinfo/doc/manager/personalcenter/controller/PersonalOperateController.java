package com.jxdinfo.doc.manager.personalcenter.controller;

import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.foldermanager.service.IDocFoldAuthorityService;
import com.jxdinfo.doc.manager.groupmanager.service.DocGroupService;
import com.jxdinfo.doc.manager.personalcenter.service.PersonalOperateService;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 个人中心数据处理
 * @author luzhanzhao
 * @date 2018-11-19
 */
@Controller
@RequestMapping("/personalOperate")
public class PersonalOperateController {
    /*个人中心数据处理的服务*/
    @Autowired
    private PersonalOperateService operateService;
    @Autowired
    private IDocFoldAuthorityService docFoldAuthorityService;
    @Autowired
    private DocGroupService docGroupService;


    @Autowired
    private ISysUsersService iSysUsersService;


    /**
     * 目录服务类
     */
    @Resource
    private BusinessService businessService;

    /**
     * @author luzhanzhao
     * @date 2018-11-19
     * @param name 模糊查询的关键字
     * @param pageNumber 当前页数
     * @param pageSize 每页显示条数
     * @param opType 需要查看的操作类型（3：预览；4：下载）
     * @return 详细信息列表
     */
    @PostMapping("/list")
    @ResponseBody
    public Map list(String name,String[] typeArr, @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber, @RequestParam(defaultValue = "60") int pageSize, @RequestParam(defaultValue = "3") String opType,String order){
        int beginIndex = pageNumber * pageSize - pageSize;
        List<String> roleList = ShiroKit.getUser().getRolesList();
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        //获取当前登录人
        ShiroUser shiroUser = ShiroKit.getUser();
        String userId = shiroUser.getId();
        FsFolderParams fsFolderParams = new FsFolderParams();
        List<String> listGroup = docGroupService.getPremission(userId);
        fsFolderParams.setUserId(userId);
        fsFolderParams.setGroupList(listGroup);
        //获得目录管理权限层级码
        fsFolderParams.setType("2");
        fsFolderParams.setRoleList(ShiroKit.getUser().getRolesList());
        String levelCode = businessService.getLevelCodeByUserUpload(fsFolderParams);
        String orgId = docFoldAuthorityService.getDeptIds(ShiroKit.getUser().getDeptId());
        List<Map> list = operateService.getMyHistory(userId, opType, beginIndex, pageSize, name,typeArr,order,levelCode,orgId);
        int count = operateService.getMyHistoryCount(userId, opType, name);
        Map histories = new HashMap();
        histories.put("msg","success");
        histories.put("code",0);
        histories.put("adminFlag",adminFlag);
        histories.put("rows",list);
        histories.put("count",count);
        return histories;
    }


    /**
     * @author luzhanzhao
     * @date 2018-11-19
     * @param histories 要删除的记录对应的文件id
     * @param opType 要删除的记录对应的操作类型（3：预览；4：下载）
     * @return 删除结果
     */
    @PostMapping("/deleteHistory")
    @ResponseBody
    public int deleteHistory(String[] histories, @RequestParam(defaultValue = "3") String opType){
        //获取当前登录人
        ShiroUser shiroUser = ShiroKit.getUser();
        String userId = shiroUser.getId();
        return operateService.deleteHistory(histories, userId,opType);
    }


    /**
     * @author luzhanzhao
     * @date 2018-11-19
     * @param opType 要清空记录对应的操作类型（3：预览；4：下载）
     * @return 结果
     */
    @PostMapping("/clearHistory")
    @ResponseBody
    public int clearHistory(@RequestParam(defaultValue = "3") String opType){
        //获取当前登录人
        ShiroUser shiroUser = ShiroKit.getUser();
        String userId = shiroUser.getId();
        return operateService.clearHistory(userId, opType);
    }
}
