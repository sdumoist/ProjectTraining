package com.jxdinfo.doc.manager.personalcenter.controller;

import com.jxdinfo.doc.common.util.FileTool;
import com.jxdinfo.doc.manager.docmanager.model.FsFolderView;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 个人中心
 * @author yjs
 * @date 2018-11-13
 */
@Controller
@RequestMapping("/personalUpload")
public class PersonalUploadController {

    private String PREFIX = "/doc/manager/personalcenter/";

    /*文件服务*/
    @Autowired
    private IFsFolderService fsFolderService;

    /**
     * @author yjs
     * @return 跳转到文件系统-个人中心
     */
    @RequiresPermissions("personalUpload:list")
    @GetMapping("/list")
    public String index() {
        return PREFIX + "personal_upload.html";
    }


    /**
     * @author yjs
     * @description 跳转到文件系统获得列表
     * @param pageNumber 当前页数
     * @param pageSize 每页数据条数
     * @param name 模糊查询关键字
     * @return 查询结果
     */
    @PostMapping("/getlist")
    @ResponseBody
    public Object getList(  @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
                            @RequestParam(value = "pageSize", defaultValue = "60") int pageSize,
                            String name,String[] typeArr,String order) {
        //获取当前登录用户id
        String userId = ShiroKit.getUser().getId();
        List<FsFolderView> list = new ArrayList<>();
        //获取用户上传数据列表
        list = fsFolderService.getPersonUpload(userId,(pageNumber - 1) * pageSize, pageSize,name,typeArr,order);
        list = changeSize(list);
        //获取上传数据列表的条数
        int num = fsFolderService.getPersonUploadNum(userId,name);
        Map<String, Object> result = new HashMap<>(5);
        result.put("userId", ShiroKit.getUser().getName());
        result.put("total", num);
        result.put("rows", list);
        return result;
    }

    /**
     * @author yjs
     * @description 转化文件大小的方法
     * @param list 待转换数据的列表
     * @return
     */
    public List<FsFolderView> changeSize(List<FsFolderView> list) {
        for (FsFolderView fsFolderView : list) {
            if (fsFolderView.getFileSize() != null && !"".equals(fsFolderView.getFileSize())) {
                fsFolderView.setFileSize(FileTool.longToString(fsFolderView.getFileSize()));
            }
        }
        return list;
    }
}
