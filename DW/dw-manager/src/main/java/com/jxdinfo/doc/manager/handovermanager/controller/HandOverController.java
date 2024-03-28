package com.jxdinfo.doc.manager.handovermanager.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.common.util.ESUtil;
import com.jxdinfo.doc.manager.componentmanager.model.ComponentApply;
import com.jxdinfo.doc.manager.componentmanager.service.ComponentApplyService;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.model.FsFile;
import com.jxdinfo.doc.manager.docmanager.model.FsFolderView;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import com.jxdinfo.doc.manager.handovermanager.model.DocHandOver;
import com.jxdinfo.doc.manager.handovermanager.model.DocHandOverAttachment;
import com.jxdinfo.doc.manager.handovermanager.service.HandOverAttachmentService;
import com.jxdinfo.doc.manager.handovermanager.service.HandOverService;
import com.jxdinfo.hussar.bsp.organ.dao.SysStruMapper;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.sys.model.DicSingle;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.*;

@Controller
@RequestMapping("/handover")
public class HandOverController {
    @Resource
    private HandOverService handOverService;
    @Resource
    private DocInfoService docInfoService;
    @Resource
    private IFsFolderService fsFolderService;
    @Resource
    private ComponentApplyService componentApplyService;
    @Resource
    private HandOverAttachmentService handOverAttachmentService;
    @Resource
    private SysStruMapper sysStruMapper;
    @Autowired
    private ESUtil esUtil;

    @Autowired
    private ISysUsersService iSysUsersService;

    /**
     * 打开组件复用查看
     */
    @GetMapping("/messageListView")
    @RequiresPermissions("handover:messageListView")
    public String multiplexListDeptView() {
        return "/doc/manager/handovermanager/handover-list-message.html";
    }

    @GetMapping("/getMessageList")
    @ResponseBody
    public JSON getMessageList(String deptName, int page, int limit, String acceptDeptName, String acceptName, String handovername) {
        int beginIndex = page * limit - limit;
        List<DocHandOver> messageList = handOverService.getMessageList(beginIndex, limit, handovername, acceptName, deptName, acceptDeptName, 1,null,0,null,"0");
        int messageCount = handOverService.getMessageListCount(handovername, acceptName, deptName, acceptDeptName, 1,null,0);
        JSONObject json = new JSONObject();
        json.put("count", messageCount);
        json.put("data", messageList);
        json.put("msg", "success");
        json.put("code", 0);
        return json;
    }

    @PostMapping("/deleteMessage")
    @ResponseBody
    public JSON deleteMessage(String idStr) {
        //专题ID
        String[] ids = idStr.split(",");

        int j = 0;
        boolean str = false;
        for (int i = 0; i < ids.length; i++) {
            str = handOverService.removeById(ids[i]);
            j++;
        }
        JSONObject json = new JSONObject();
        if (j != 0 && str) {
            json.put("result", "1");
        }
        return json;
    }

    /**
     * 打开组件复用查看
     */
    @GetMapping("/handoverView")
    public String handoverView() {
        return "/doc/manager/handovermanager/handover.html";
    }

    /**
     * @param pageNumber   当前页数
     * @param pageSize     每页数据条数
     * @param handovername 模糊查询关键字
     * @return 查询结果
     * @author yjs
     * @description 跳转到文件系统获得列表
     */
    @PostMapping("/getExamine")
    @ResponseBody
    public Object getExamine(@RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
                             @RequestParam(value = "pageSize", defaultValue = "60") int pageSize,
                             String handovername, String order, String type) {
        Map orderMap = new HashMap();
        String  isDesc="0";
        if("1".equals(order)||"3".equals(order)||"5".equals(order)||"7".equals(order)||"9".equals(order)||"11".equals(order)||"13".equals(order)||"15".equals(order)){
            isDesc = "1";
        }
        //排序和查询规则
        orderMap.put("0", "handOverUserName");
        orderMap.put("1", "handOverUserName");
        orderMap.put("2", "handOverDeptName");
        orderMap.put("3", "handOverDeptName");
        orderMap.put("4", "handOverDate");
        orderMap.put("5", "handOverDate");
        orderMap.put("6", "SUBSTRING_INDEX(fileSize,'k',1)+0");
        orderMap.put("7", "SUBSTRING_INDEX(fileSize,'k',1)+0");
        orderMap.put("8", "acceptName");
        orderMap.put("9", "acceptName");
        orderMap.put("10", "acceptDeptName");
        orderMap.put("11", "acceptDeptName");
        orderMap.put("12", "handOverType");
        orderMap.put("13", "handOverType");
        orderMap.put("14", "num");
        orderMap.put("15", "num");
        String orderResult = (String) orderMap.get(order);
        int beginIndex = pageNumber * pageSize - pageSize;
        String orgId = ShiroKit.getUser().getDeptId();
        List<String> roleList = ShiroKit.getUser().getRolesList();
        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        List<DocHandOver> messageList = handOverService.getMessageList(beginIndex, pageSize, handovername, null, null, null, 0,orgId,adminFlag,orderResult,isDesc);
        int messageCount = handOverService.getMessageListCount(handovername, null, null, null, 0,orgId,adminFlag);
        JSONObject json = new JSONObject();
        json.put("count", messageCount);
        json.put("rows", messageList);
        json.put("msg", "success");
        json.put("code", 0);
        json.put("userId", ShiroKit.getUser().getName());
        return json;
    }

    @PostMapping("/getHandover")
    @ResponseBody
    public Object getHandover(@RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
                              @RequestParam(value = "pageSize", defaultValue = "60") int pageSize,
                              String name, String order, String type) {
        //获取当前登录用户id
        String userId = null;
        Map orderMap = new HashMap();
        String  isDesc="0";
        if("1".equals(order)||"3".equals(order)||"5".equals(order)||"7".equals(order)||"9".equals(order)||"11".equals(order)||"13".equals(order)){
            isDesc = "1";
        }
        //排序和查询规则
        orderMap.put("0", "fileName");
        orderMap.put("1", "fileName");
        orderMap.put("2", "createTime");
        orderMap.put("3", "createTime");
        orderMap.put("4", "createUserName");
        orderMap.put("5", "createUserName");
        orderMap.put("6", "SUBSTRING_INDEX(fileSize,'k',1)+0");
        orderMap.put("7", "SUBSTRING_INDEX(fileSize,'k',1)+0");
        orderMap.put("8", "state");
        orderMap.put("9", "state");
        orderMap.put("10", "deptName");
        orderMap.put("11", "deptName");
        orderMap.put("12", "orderName");
        orderMap.put("13", "orderName");
        String orderResult = (String) orderMap.get(order);
        List<String> roleList = ShiroKit.getUser().getRolesList();
        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        if (adminFlag != 1) {
            userId = ShiroKit.getUser().getId();
        }
        List<FsFolderView> list = new ArrayList<>();
        //获取用户上传数据列表
        list = handOverService.getList(userId, (pageNumber - 1) * pageSize, pageSize, name, orderResult, type,isDesc);
        //获取上传数据列表的条数
        int num = handOverService.getCount(userId, name, type);
        Map<String, Object> result = new HashMap<>(5);
        result.put("userId", ShiroKit.getUser().getName());
        result.put("total", num);
        result.put("count", num);
        result.put("rows", list);
        return result;
    }

    /**
     * 新增文件夹
     */
    @PostMapping(value = "/add")
    @ResponseBody
    public Object add(DocHandOver docHandOver, String ids, String fileTypes, String authors, String names) {
        Map<String, Object> result = new HashMap<>(5);
        String[] idsStr = ids.split(",");
        String[] fileTypeStr = fileTypes.split(",");
        String[] authorsStr = authors.split(",");
        String[] namesStr = names.split(",");
        boolean isSameAuthor = false;
        for (int i = 0; i < idsStr.length; i++) {
            if (i > 0) {
                if (!authorsStr[i].equals(authorsStr[i - 1])) {
                    isSameAuthor = true;
                }
            }
        }
        if (isSameAuthor) {
            result.put("success", false);
            result.put("code", "2");
            return result;
        }


        String acceptId = docHandOver.getAcceptId();
        String acceptName = iSysUsersService.getById(acceptId).getUserName();
        String acceptDeptId = iSysUsersService.getById(acceptId).getDepartmentId();
        String acceptDeptName = sysStruMapper.selectById(acceptDeptId).getOrganAlias();
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        docHandOver.setAcceptId(acceptId);
        docHandOver.setAcceptDeptId(acceptDeptId);
        docHandOver.setAcceptDeptName(acceptDeptName);
        docHandOver.setAcceptName(acceptName);
        docHandOver.setHandOverDate(ts);
        String handOverId = UUID.randomUUID().toString().replaceAll("-", "");
        docHandOver.setHandOverId(handOverId);
        docHandOver.setHandOverState(0);
        docHandOver.setHandOverUserId(authorsStr[0]);
        String userName = iSysUsersService.getById(authorsStr[0]).getUserName();
        String deptId = iSysUsersService.getById(authorsStr[0]).getDepartmentId();
        String deptName = sysStruMapper.selectById(deptId).getOrganAlias();

        docHandOver.setHandOverUserName(userName);
        docHandOver.setHandOverDeptId(deptId);
        docHandOver.setHandOverDeptName(deptName);
        boolean b = handOverService.saveOrUpdate(docHandOver);
        for (int i = 0; i < idsStr.length; i++) {
            DocHandOverAttachment docHandOverAttachment = new DocHandOverAttachment();
            docHandOverAttachment.setAttachmentId(UUID.randomUUID().toString().replaceAll("-", ""));
            if (fileTypeStr[i].equals("folder")) {
                docHandOverAttachment.setResourceType(2);
            } else if (fileTypeStr[i].equals("component")) {
                docHandOverAttachment.setResourceType(1);
            } else {
                docHandOverAttachment.setResourceType(0);
            }
            docHandOverAttachment.setResourceId(idsStr[i]);
            docHandOverAttachment.setResourceName(namesStr[i]);
            docHandOverAttachment.setHandOverId(handOverId);
            handOverAttachmentService.saveOrUpdate(docHandOverAttachment);

        }
        if (b) {
            result.put("success", true);
            result.put("code", "0");
        } else {
            result.put("success", false);
            result.put("code", "1");
        }
        return result;

    }

    @GetMapping("/view")
    public String view(String id, Model model) {
        DocHandOver docHandOver = handOverService.getById(id);
        model.addAttribute("id", id);
        model.addAttribute("docHandOver", docHandOver);
        return "/doc/manager/handovermanager/view.html";
    }

    @RequestMapping("/getAttachmentList")
    @ResponseBody
    public Object getAttachmentList(int page, int limit,
                                    String name, String id) {
        int beginIndex = page * limit - limit;
        List<FsFolderView> attachmentList = handOverAttachmentService.getAttachmentList(beginIndex, limit, name, id);
        int attachmentCount = handOverAttachmentService.getAttachmentCount(name, id);
        JSONObject json = new JSONObject();
        json.put("count", attachmentCount);
        json.put("data", attachmentList);
        json.put("msg", "success");
        json.put("code", 0);
        json.put("userId", ShiroKit.getUser().getName());

        return json;
    }

    @PostMapping("/pass")
    @ResponseBody
    public Object pass(String idStr) {
        String[] ids = idStr.split(",");
        for (int i = 0; i < ids.length; i++) {
            DocHandOver docHandOver = handOverService.getById(ids[i]);
            if (docHandOver == null) {
                continue;
            }
            docHandOver.setHandOverState(1);
            String acceptId = docHandOver.getAcceptId();
            String acceptName = docHandOver.getAcceptName();
            handOverService.updateById(docHandOver);
            List<DocHandOverAttachment> list = handOverAttachmentService.list(new
                    QueryWrapper<DocHandOverAttachment>()
                    .eq("hand_over_id", ids[i]));

            for (int j = 0; j < list.size(); j++) {
                if (list.get(j).getResourceType() == 0) {
                    String docId = list.get(j).getResourceId();
                    DocInfo docInfo = docInfoService.getById(docId);
                    docInfo.setUserId(acceptId);
                    docInfo.setAuthorId(acceptId);
                    docInfo.setContactsId(acceptId);
                    docInfoService.updateById(docInfo);
                    Map<String, Object> map = new HashMap<String, Object>(16);
                    List<String> indexList = new ArrayList<>();
                    indexList.add(docHandOver.getAcceptName());

                    indexList.add("allpersonflag");
                    map.put("permission", indexList.toArray(new String[indexList.size()]));
                    esUtil.updateIndex(docId, map);

                } else if (list.get(j).getResourceType() == 1) {
                    String componentId = list.get(j).getResourceId();
                    ComponentApply componentApply = componentApplyService.getById(componentId);
                    componentApply.setUserId(acceptId);
                    componentApply.setUserName(acceptName);
                    componentApply.setDeptId(docHandOver.getAcceptDeptId());
                    componentApply.setDeptName(docHandOver.getAcceptDeptName());
                    componentApplyService.updateById(componentApply);
                } else {
                    String folderId = list.get(j).getResourceId();
                    FsFolder fsFolder = fsFolderService.getById(folderId);
                    fsFolder.setCreateUserId(acceptId);
                    fsFolderService.updateById(fsFolder);
                }
            }
        }
        Map<String, Object> result = new HashMap<>(5);
        result.put("success", true);
        result.put("code", "0");
        return result;
    }

    @PostMapping("/back")
    @ResponseBody
    public Object back(String idStr) {
        String[] ids = idStr.split(",");
        for (int i = 0; i < ids.length; i++) {
            DocHandOver docHandOver = handOverService.getById(ids[i]);
            if (docHandOver == null) {
                continue;
            }
            docHandOver.setHandOverState(2);
            handOverService.updateById(docHandOver);

        }
        Map<String, Object> result = new HashMap<>(5);
        result.put("success", true);
        result.put("code", "0");
        return result;
    }
}
