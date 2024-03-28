package com.jxdinfo.doc.manager.recyclePermission;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxdinfo.doc.manager.docmanager.model.DocFileAuthority;
import com.jxdinfo.doc.manager.docmanager.service.DocFileAuthorityService;
import com.jxdinfo.doc.manager.foldermanager.model.DocFoldAuthority;
import com.jxdinfo.doc.manager.foldermanager.service.IDocFoldAuthorityService;
import com.jxdinfo.hussar.core.base.controller.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 回收权限管理类
 */
@Controller
@RequestMapping({"/recyclePermission"})
public class RecyclePermissionController extends BaseController {

    private String PREFIX = "/doc/manager/recyclepermission/";

    @Autowired
    private IDocFoldAuthorityService iDocFoldAuthorityService;

    @Autowired
    private DocFileAuthorityService docFileAuthorityService;



    @RequestMapping({"/view"})
    public String index() {
        return PREFIX + "recycle_permission.html";
    }

    @RequestMapping({"/recycle"})
    @ResponseBody
    public Object recycle(String userIds) {
        if(StringUtils.isNotEmpty(userIds)){
            String[]  userIdsArr = userIds.split(",");
            // 删除用户目录权限
            QueryWrapper<DocFoldAuthority> foldAuthQW = new QueryWrapper<DocFoldAuthority>();
            foldAuthQW.in("author_id", userIdsArr);
            iDocFoldAuthorityService.remove(foldAuthQW);

            // 删除用户文件权限
            QueryWrapper<DocFileAuthority> fileAuthQW = new QueryWrapper<DocFileAuthority>();
            fileAuthQW.in("author_id", userIdsArr);
            List<DocFileAuthority> fileAuthList = docFileAuthorityService.list(fileAuthQW);
            if(fileAuthList!=null && fileAuthList.size()>0){
                // 找到相关的文件id
                List<String> fileIds = fileAuthList.stream().map(fileAuth -> fileAuth.getFileId()).collect(Collectors.toList());
                // 删除用户文件权限
                docFileAuthorityService.remove(fileAuthQW);
                // 重置相关文件的  es权限
                docFileAuthorityService.generateFileAuthorityToEs(fileIds);
            }
        }
        return SUCCESS_TIP;
    }

}
