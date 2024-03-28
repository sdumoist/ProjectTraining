package com.jxdinfo.doc.manager.commonFolderManager.controller;

import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.common.util.UserInfoUtil;
import com.jxdinfo.doc.common.util.XSSUtil;
import com.jxdinfo.doc.manager.commonFolderManager.model.FsCommonFolder;
import com.jxdinfo.doc.manager.commonFolderManager.service.IFsCommonFolderService;
import com.jxdinfo.hussar.core.base.controller.BaseController;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 常用目录Controller
 */
@Controller
@RequestMapping("/fsCommonFolder")
public class FsCommonFolderController extends BaseController {

    /**
     * 目录管理服务类
     */
    @Autowired
    private IFsCommonFolderService fsCommonFolderService;
    /**
     * PREFIX
     */
    private String prefix = "/doc/manager/commonfoldermanager/";
    /**
     * 语义分析：生成标签-是否开启
     */
    @Value("${semanticAnalysis.analysisUsing}")
    private String analysisUsing;

    @Value("${sameName.newVersion:false}")
    private boolean sameNameNewVersion;

    /**
     * 查询用户常用目录
     * @return
     */
    @PostMapping(value = "/getCommonFolder")
    @ResponseBody
    public Map getCommonFolder(@RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
                               @RequestParam(defaultValue = "60") int pageSize,String order) {
        int beginIndex = pageNumber * pageSize - pageSize;
        List<String> roleList = ShiroKit.getUser().getRolesList();
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        String userId = ShiroKit.getUser().getId();
        List<FsCommonFolder> list = fsCommonFolderService.selectAllCommonFold(userId, beginIndex, pageSize, order);
        //封装传到前台的信息
        Map histories = new HashMap();
        histories.put("msg","success");
        histories.put("code",0);
        histories.put("adminFlag",adminFlag);
        histories.put("rows",list);
        histories.put("count",list.size());
        return histories;
    }

    /**
     * 批量增加常用目录
     * @param ids
     */
    @PostMapping(value = "/addCommonFold")
    @ResponseBody
    public Map<String,Object> addCommonFold(String ids){
        return fsCommonFolderService.addCommonFold(ids);
    }

    /**
     * 批量删除常用目录
     * @param ids
     */
    @PostMapping(value = "/deleteCommonFold")
    @ResponseBody
    public void deleteCommonFold(String ids){
        fsCommonFolderService.deleteCommonFold(ids);
    }

    /**
     * 更新常用目录
     * @param commonFolderId
     * @param commonFolderName
     */
    @PostMapping(value = "/updateCommonFold")
    @ResponseBody
    public void updateCommonFold(String commonFolderId, String commonFolderName){
        fsCommonFolderService.updateCommonFold(commonFolderId, commonFolderName);
    }

    /**
     * 移动目录
     * @param idOne
     * @param idTwo
     */
    @RequestMapping("/moveFolder")
    @ResponseBody
    public void moveFolder(String idOne, String idTwo) {
        fsCommonFolderService.moveFolder(idOne,idTwo);
    }
}
