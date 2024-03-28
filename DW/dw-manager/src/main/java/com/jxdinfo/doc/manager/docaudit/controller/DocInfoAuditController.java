package com.jxdinfo.doc.manager.docaudit.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxdinfo.doc.common.util.ESUtil;
import com.jxdinfo.doc.common.util.FileTool;
import com.jxdinfo.doc.common.util.ProcessUtil;
import com.jxdinfo.doc.manager.docaudit.model.DocInfoAudit;
import com.jxdinfo.doc.manager.docaudit.service.IDocInfoAuditService;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.model.FsFolderView;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.hussar.core.base.controller.BaseController;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件审核控制器
 *
 * @author zn
 * @Date 2020-08-25 14:27:45
 */
@Controller
@RequestMapping("/docInfoAudit")
public class DocInfoAuditController extends BaseController {

    @Autowired
    private IDocInfoAuditService docInfoAuditService;

    @Autowired
    private DocInfoService docInfoService;

    @Autowired
    private ESUtil esUtil;

    @Value("${fileAudit.workflowUsing}")
    private boolean workflowUsing;

    @Autowired
    private ProcessUtil processUtil;


    /**
     * 跳转到文件审核首页
     */
    @RequestMapping("/view")
    public String index() {
        return "/doc/front/personalcenter/judge.html";
    }

    /**
     * 待我审批列表
     */
    @RequestMapping("/myApprovedList")
    @ResponseBody
    public Object myApprovedList(@RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
                                 @RequestParam(value = "pageSize", defaultValue = "60") int pageSize,
                                 String name, String order) {
        //获取当前登录用户id
        String userId = ShiroKit.getUser().getId();
        List<FsFolderView> list = new ArrayList<>();
        //获取用户上传数据列表
        list = docInfoAuditService.getApprovalList(userId, (pageNumber - 1) * pageSize, pageSize, order, name);
        // 转换文件大小
        list = changeSize(list);
        //获取上传数据列表的条数
        int num = docInfoAuditService.getApprovalListCount(userId,name);
        // 定义返回参数
        Map<String, Object> result = new HashMap<>(5);
        // 设置返回数据
        result.put("userId", ShiroKit.getUser().getName());
        // 设置返回数据
        result.put("total", num);
        // 设置返回数据
        result.put("rows", list);
        // 返回数据
        return result;
    }

    /**
     * 审批通过
     */
    @RequestMapping("/approved")
    @ResponseBody
    public Object approved(String id) {
        if (StringUtils.isNotEmpty(id)) {
            // 封装查询参数
            QueryWrapper<DocInfo> ew = new QueryWrapper<>();
            // 设置查询参数
            ew.in("doc_id", id.split(","));
            // 获取当前用户
            ShiroUser user = ShiroKit.getUser();
            // 查询文档列表
            List<DocInfo> docInfoList = docInfoService.list(ew);
            // 定义当前时间的时间戳
            Date date = new Date();
            Timestamp ts = new Timestamp(date.getTime());
            for (DocInfo docInfo : docInfoList) {
                docInfo.setValidFlag("1");
                docInfo.setAuditUser(user.getName());
                docInfo.setExamineState("1");
                docInfo.setAuditTime(ts);
                // 是否开启了工作流
                if (workflowUsing) {
                    Map<String, String> result = processUtil.completeProcess(docInfo.getTaskId(), user.getId(), null, null);
                    if (result.containsKey("result")) {
                        if (StringUtils.equals(result.get("result"), "true")) {
                            System.out.println("审核通过  " + docInfo.getTitle());
                            docInfoService.updateById(docInfo);
                        }
                    } else {
                        return ERROR;
                    }
                } else {
                    docInfoService.updateById(docInfo);
                    // 更新审核信息
                    QueryWrapper<DocInfoAudit> wp = new QueryWrapper<>();
                    wp.eq("doc_id", docInfo.getDocId());
                    wp.eq("audit_result", "1");
                    DocInfoAudit docInfoAudit = new DocInfoAudit();
                    docInfoAudit.setAuditResult("2");
                    docInfoAudit.setAuditTime(ts);
                    docInfoAudit.setLastEditor(user.getId());
                    docInfoAudit.setLastTime(ts);
                    docInfoAuditService.update(docInfoAudit, wp);
                }


                // 更新索引
                Map map = new HashMap(1);
                map.put("recycle", "1");
                esUtil.updateIndex(docInfo.getDocId(), map);


            }
        }
        // 更新文档审批信息
        //return docInfoService.updateBatchById(docInfoList);
        return SUCCESS;
    }

    /**
     * 审批驳回
     */
    @RequestMapping("/reject")
    @ResponseBody
    public Object reject(String id, String opinion) {
        if (StringUtils.isNotEmpty(id)) {
            // 封装查询参数
            QueryWrapper<DocInfo> ew = new QueryWrapper<>();
            // 设置查询参数
            ew.in("doc_id", id.split(","));
            // 获取当前用户
            ShiroUser user = ShiroKit.getUser();
            // 查询文档列表
            List<DocInfo> docInfoList = docInfoService.list(ew);
            Date date = new Date();
            // 定义当前时间的时间戳
            Timestamp ts = new Timestamp(date.getTime());
            for (DocInfo docInfo : docInfoList) {
                docInfo.setValidFlag("3");
                docInfo.setAuditUser(user.getName());
                docInfo.setAuditTime(ts);
                docInfo.setExamineState("3");
                docInfo.setAuditOpinion(opinion);

                // 是否开启了工作流
                if (workflowUsing) {
                    Map<String, String> result = processUtil.rejectProcess(docInfo.getTaskId(), user.getId());
                    if (result.containsKey("result")) {
                        if (StringUtils.equals(result.get("result"), "true")) {
                            System.out.println("审核驳回  " + docInfo.getTitle());
                            docInfoService.updateById(docInfo);
                        }
                    } else {
                        return ERROR;
                    }
                } else {
                    // 更新审核信息
                    QueryWrapper<DocInfoAudit> wp = new QueryWrapper<>();
                    wp.eq("doc_id", docInfo.getDocId());
                    wp.eq("audit_result", "1");
                    DocInfoAudit docInfoAudit = new DocInfoAudit();
                    docInfoAudit.setAuditResult("3");
                    docInfoAudit.setAuditOpinion(opinion);
                    docInfoAudit.setAuditTime(ts);
                    docInfoAudit.setLastEditor(user.getId());
                    docInfoAudit.setLastTime(ts);
                    docInfoAuditService.update(docInfoAudit, wp);
                }

                // 更新索引
                Map map = new HashMap(1);
                map.put("recycle", "3");
                esUtil.updateIndex(docInfo.getDocId(), map);
            }
        }
        // 更新文档审批信息
        return SUCCESS;
    }

    /**
     * 转化文件大小的方法
     * @param list 待转换数据的列表
     * @return
     */
    public List<FsFolderView> changeSize(List<FsFolderView> list) {
        for (FsFolderView fsFolderView : list) {
            if (fsFolderView.getFileSize() != null && !"".equals(fsFolderView.getFileSize())) {
                fsFolderView.setFileSize(FileTool.longToString(fsFolderView.getFileSize()));
            }
        }
        // 返回数据
        return list;
    }

}
