package com.jxdinfo.doc.manager.docrecycle.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.jxdinfo.doc.manager.statistics.service.EmpStatisticsService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.common.util.UserInfoUtil;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.docmanager.service.FsFileService;
import com.jxdinfo.doc.manager.docrecycle.service.IDocRecycleService;
import com.jxdinfo.doc.manager.foldermanager.service.IDocFoldAuthorityService;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import com.jxdinfo.doc.manager.groupmanager.service.DocGroupService;
import com.jxdinfo.doc.manager.statistics.service.DeptStatisticsService;
import com.jxdinfo.hussar.core.base.controller.BaseController;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import com.jxdinfo.hussar.core.util.ToolUtil;

/**
 * 类的用途： 回收站管理 控制器
 * 创建日期：2018/8/9 11:00 ;
 * 修改历史：
 * 修改日期：2018/8/9 11:00 ;
 * 修改作者：ChenXin ;
 * 修改内容：
 *
 * @author ChenXin ;
 * @version 1.0
 */
@Controller
@RequestMapping("/docRecycle")
public class DocRecycleController extends BaseController {

    /**
     * 回收站 服务类
     */
    @Resource
    private IDocRecycleService iDocRecycleService;

    @Autowired
    private DocGroupService docGroupService;

    @Autowired
    private IFsFolderService fsFolderService;

    @Autowired
    private IDocFoldAuthorityService docFoldAuthorityService;

    @Autowired
    private FsFileService fsFileService;
    @Autowired
    private DocInfoService docInfoService;

    /**
     * 部门文件大小 Service接口
     */

    @Autowired
    private DeptStatisticsService deptStatisticsService;


    @Autowired
    private EmpStatisticsService empStatisticsService;

    /**
     * 跳转页面前缀
     */
    private static String PREFIX = "/doc/manager/docrecycle/";

    /**
     * 跳转到回收站管理页面
     *
     * @return java.lang.String
     * @author ChenXin
     * @date 2018/8/9 11:04
     */
    @RequiresPermissions("docRecycle:view")
    @GetMapping("/view")
    public String view() {
        return PREFIX + "docRecycle.html";
    }

    /**
     * 加载回收站文件列表
     *
     * @return java.lang.Object
     * @author ChenXin
     * @date 2018/8/9 11:04
     */
    @PostMapping("/docRecycleList")
    @ResponseBody
    public Object getLogTable() {
        // 页码
        String pageNum = super.getPara("page");
        // 每页数量
        String limitNum = super.getPara("limit");
        //查询条件（文件名）
        String fileName = super.getPara("fileName");
        Map<String, Object> list = iDocRecycleService.getDocRecycleList(pageNum, limitNum, fileName);
        return list;
    }

    /**
     * 还原回收站
     *
     * @return boolean
     * @author: ChenXin
     */
    @PostMapping("/restore")
    @ResponseBody
    public JSON restore() {
        String userId = ShiroKit.getUser().getId();
        List<String> listGroup = docGroupService.getPremission(userId);
        List<String> roleList = ShiroKit.getUser().getRolesList();
        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        JSONObject json = new JSONObject();
        String fileId = super.getPara("fileId");
        if (ToolUtil.isEmpty(fileId)) {
            fileId = "";
        }
        String folderId = super.getPara("folderId");
        String fileName = super.getPara("fileName");
        ShiroUser shiroUser= UserInfoUtil.getCurrentUser();
      /*  if (adminFlag != 1) {
            int isEdits = docFoldAuthorityService.findEdit(folderId, listGroup, userId);
            if (isEdits == 0) {
                json.put("result", "3");
                return json;
            }
        }*/
        DocInfo docInfo = docInfoService.getDocDetail(fileId);
        //判断是否为子级目录（只能在子文件夹上传文件）
        boolean isChild = fsFileService.isChildren(folderId);
        if (isChild) {
            String fileNameStr = fileName + docInfo.getDocType();
            if (iDocRecycleService.checkDocExist(folderId, fileNameStr)) {
                json.put("result", "0");
            } else {
                if (docInfo != null) {
                    if (adminFlag != 1) {
                        double lackShare = empStatisticsService.getStatisticsDataByUserId(userId, docInfo.getFileSize());
                        if (lackShare < 0) {
                            json.put("result", "5");
                            return json;
                        }
                    }
                }


                if (iDocRecycleService.restore(fileId, folderId)) {
                    json.put("result", "1");
                } else {
                    json.put("result", "2");
                }
            }
        } else {
            json.put("result", "4");
        }

        return json;
    }

    /**
     * 清空回收站
     *
     * @return boolean
     * @author: ChenXin
     */
    @PostMapping("/clear")
    @ResponseBody
    public boolean clear() {
        boolean flag = iDocRecycleService.clear();
        return flag;
    }

}
