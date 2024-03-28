package com.jxdinfo.doc.manager.groupmanager.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.common.util.StringUtil;
import com.jxdinfo.doc.manager.docmanager.model.DocFileAuthority;
import com.jxdinfo.doc.manager.docmanager.model.DocResourceLog;
import com.jxdinfo.doc.manager.docmanager.service.DocFileAuthorityService;
import com.jxdinfo.doc.manager.foldermanager.model.DocFoldAuthority;
import com.jxdinfo.doc.manager.foldermanager.service.IDocFoldAuthorityService;
import com.jxdinfo.doc.manager.groupmanager.model.DocGroup;
import com.jxdinfo.doc.manager.groupmanager.model.DocGroupSort;
import com.jxdinfo.doc.manager.groupmanager.service.DocGroupService;
import com.jxdinfo.doc.manager.topicmanager.service.ITopicDocManagerService;
import com.jxdinfo.hussar.bsp.permit.service.ISysIdtableService;
import com.jxdinfo.hussar.common.treemodel.JSTreeModel;
import com.jxdinfo.hussar.core.base.controller.BaseController;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.support.HttpKit;
import com.jxdinfo.hussar.core.util.ToolUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.Timestamp;
import java.util.*;

/**
 * Created by Administrator on 2018/8/4 0004.
 */
@Controller
@RequestMapping("/group")
public class DocGroupController extends BaseController {

    /** PREFIX */
    private String PREFIX = "/doc/manager/groupmanager/";
    private String PERSONAL_PREFIX = "/doc/manager/personalgroupmanager/";

    /** 文档群组服务类 */
    @Autowired
    private DocGroupService docGroupService;

    /** 文档目录权限服务类 */
    @Autowired
    private IDocFoldAuthorityService docFoldAuthorityService;

    /** 文档文件权限服务类 */
    @Autowired
    private DocFileAuthorityService docFileAuthorityService;

    /** 获取编号公共方法 */
    @Autowired
    private ISysIdtableService sysIdtableService;

    /** 存储操作日志的方法*/
    @Autowired
    private ITopicDocManagerService iTopicDocManagerService;

    /**
     * @Author zoufeng
     * @Description //TODO 打开群组列表
     * @Date 8:51 2018/9/10
     * @Param [model]
     * @return java.lang.String 返回群组列表
     **/
    @RequestMapping("/groupListView")
    @RequiresPermissions("group:groupListView")
    public String groupListView(Model model) {
        List<String> roleList = ShiroKit.getUser().getRolesList();
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        String loginId = "admin";
        if (adminFlag != 1) {
            loginId = ShiroKit.getUser().getId();
        }
        model.addAttribute("loginId", loginId);
        return PREFIX + "group_view.html";
    }

    /**
     * @Author zoufeng
     * @Description //TODO 打开个人群组列表
     * @Date 8:51 2018/9/10
     * @Param [model]
     * @return java.lang.String 返回群组列表
     **/
    @RequestMapping("/personalGroupListView")
    public String personalGroupListView(Model model) {
        List<String> roleList = ShiroKit.getUser().getRolesList();
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        String loginId = "admin";
        if (adminFlag != 1) {
            loginId = ShiroKit.getUser().getId();
        }
        model.addAttribute("loginId", loginId);
        return PERSONAL_PREFIX + "personal_group_view.html";
    }

   /**
    * @Author zoufeng
    * @Description  打开群组新增界面
    * @Date 8:56 2018/9/10
    * @Param [model, groupId]  群组id
    * @return java.lang.String 群组html
    **/
    @RequestMapping("/groupAdd")
    public String groupAdd(Model model, String sortId, String flag) {
        DocGroup docGroup = new DocGroup();
        if (null != sortId) {
//            docGroup = docGroupService.selectGroupById(groupId);
            DocGroupSort parentSortName = docGroupService.selectSortInfo(sortId,flag);
            docGroup.setSortName(parentSortName.getSortName());
            docGroup.setSortId(sortId);
        }
        model.addAttribute("docGroup", docGroup);
        if(ToolUtil.isNotEmpty(flag) && flag.equals("1")){
            return PERSONAL_PREFIX +"personal_group_edit_new.html";
        }
        return PREFIX + "group_edit_new.html";
    }

    /**
     * 获取组织机构树
     *
     * @return List<Map<String, Object>>
     * @Title: orgTree
     * @author: ChenXin
     */
    @RequestMapping("/groupTree")
    @ResponseBody
    public List<Map<String, Object>> groupTree() {
        String deptId = super.getPara("treeType");
        List<Map<String, Object>> result = docGroupService.selectGroupByName("");
        // 根节点名称
        Map<String, Object> root = new HashMap<String, Object>();
        root.put("ID", "af9090a8fdfe487f9487df9cdc6e88");
        root.put("CODE", "");
        root.put("TEXT", "群组");
        root.put("PARENT", "#");
        root.put("STRULEVEL", "0");
        root.put("ISLEAF", "0");
        root.put("TYPE", "isRoot");
        result.add(root);
        return result;

    }

    /**
     * 群组管理列表查询
     *
     * @return 专题列表
     */
    @RequestMapping("/groupList")
    @ResponseBody
    public JSON getTopicList(String groupId, String uerName, int page, int limit) {
        int beginIndex = page * limit - limit;
        //开始位置

        String userName = StringUtil.transferSqlParam(uerName);

        List<DocGroup> groupList = docGroupService.groupList(groupId, userName, beginIndex, limit);
        int counts = docGroupService.getGroupListCount(groupId, userName);
        JSONObject json = new JSONObject();
        json.put("count", counts);
        json.put("data", groupList);
        json.put("msg", "success");
        json.put("code", 0);
        return json;
    }

    /**
     * 新增群组
     *
     * @param docGroup 群组对象
     * @return 新增结果
     */
    @RequestMapping("/addGroup")
    @ResponseBody
    public JSON addGroup(DocGroup docGroup) {
        String groupId = UUID.randomUUID().toString().replaceAll("-", "");
        docGroup.setGroupId(groupId);
        String currentCode = this.sysIdtableService.getCurrentCode("GROUP_NUM", "doc_group");
        int bigNum = Integer.parseInt(currentCode);
        docGroup.setShowOrder(bigNum);
        JSONObject json = new JSONObject();
        //检查群组名称是否已经存在
        int num = docGroupService.checkGroupExist(docGroup.getGroupName(), null,docGroup.getGroupFlag());
        //拼装操作历史记录
        List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
        DocResourceLog docResourceLog = new DocResourceLog();
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        String id = UUID.randomUUID().toString().replace("-", "");
        docResourceLog.setId(id);
        docResourceLog.setResourceId(docGroup.getGroupId());
        docResourceLog.setOperateTime(ts);
        docResourceLog.setResourceType(2);
        docResourceLog.setUserId(ShiroKit.getUser().getId());
        docResourceLog.setOperateType(24);
        docResourceLog.setAddressIp(HttpKit.getIp());
        resInfoList.add(docResourceLog);
        iTopicDocManagerService.insertResourceLog(resInfoList);
        //得到查重的数量
        if (num > 0) {
            json.put("result", "0");
        } else {
            int addNum = docGroupService.addGroup(docGroup);
            if (addNum == 1) {
                json.put("result", "1");
            } else {
                json.put("result", "2");
            }
        }
        return json;
    }

    /**
     * 新增群组人员
     *
     * @param docGroup
     * @return
     */
    @RequestMapping("/addGroupUser")
    @ResponseBody
    public JSON addGroupUser(DocGroup docGroup) {
        JSONObject json = new JSONObject();
        int addNum = docGroupService.addGroupUser(docGroup);
        if (addNum > 0) {
            json.put("result", "1");
        } else {
            json.put("result", "2");
        }
        return json;
    }


    /**
     * 编辑群组
     *
     * @param docGroup 群组对象
     * @return 编辑的数量
     */
    @RequestMapping("/editGroup")
    @ResponseBody
    public JSON editGroup(DocGroup docGroup) {
        JSONObject json = new JSONObject();
        int num = docGroupService.checkGroupExist(docGroup.getGroupName(), docGroup.getGroupId(),docGroup.getGroupFlag());
        //得到查重的数量
        if (num > 0) {
            json.put("result", "0");
        } else {
            int editNum = docGroupService.updateGroup(docGroup);
            //拼装操作历史记录
            List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
            DocResourceLog docResourceLog = new DocResourceLog();
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            String id = UUID.randomUUID().toString().replace("-", "");
            docResourceLog.setId(id);
            docResourceLog.setResourceId(docGroup.getGroupId());
            docResourceLog.setOperateTime(ts);
            docResourceLog.setResourceType(2);
            docResourceLog.setUserId(ShiroKit.getUser().getId());
            docResourceLog.setOperateType(26);
            docResourceLog.setAddressIp(HttpKit.getIp());
            resInfoList.add(docResourceLog);
            iTopicDocManagerService.insertResourceLog(resInfoList);
            //获取新增的数据条数
            if (editNum == 1) {
                json.put("result", "1");
            } else {
                json.put("result", "2");
            }
        }
        return json;
    }


    /**
     * 删除群组
     *
     * @param groupId 群组ID
     * @return 删除的数量
     */
    @RequestMapping("/delGroupById")
    @ResponseBody
    public int delGroupById(String groupId) {
        int isAuthority =  docGroupService.getIsAuthority(groupId);
        if(isAuthority == 0){
            docFoldAuthorityService.remove(new QueryWrapper<DocFoldAuthority>().eq("author_id", groupId));
            docFileAuthorityService.remove(new QueryWrapper<DocFileAuthority>().eq("author_id", groupId));
            //拼装操作历史记录
            List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
            DocResourceLog docResourceLog = new DocResourceLog();
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            String id = UUID.randomUUID().toString().replace("-", "");
            docResourceLog.setId(id);
            docResourceLog.setResourceId(groupId);
            docResourceLog.setOperateTime(ts);
            docResourceLog.setResourceType(2);
            docResourceLog.setUserId(ShiroKit.getUser().getId());
            docResourceLog.setOperateType(28);
            docResourceLog.setAddressIp(HttpKit.getIp());
            resInfoList.add(docResourceLog);
            iTopicDocManagerService.insertResourceLog(resInfoList);
            return docGroupService.delGroupById(groupId);
        }
        return 6;
    }

    /**
     * 删除群组人员
     *
     * @param groupId 群组ID
     * @param delIds  人员ID集合
     * @return 删除的数量
     */
    @RequestMapping("/delGroupUserById")
    @ResponseBody
    public int delGroupUserById(String groupId, String delIds) {
        int num = 0;
        if (null != delIds) {
            List<String> idList = Arrays.asList(delIds.split(","));
            //删除的群组ID集合
            num = docGroupService.delGroupUserById(groupId, idList);
        }
        return num;
    }


    /**
     * 打开群组修改页面
     */
    @RequestMapping("/groupEdit")
    public String topicView(Model model, String groupId,String flag) {
        DocGroup docGroup = docGroupService.selectGroupById(groupId);
        DocGroupSort parentSortName = docGroupService.selectSortInfo(docGroup.getSortId(),flag);
        docGroup.setSortId(parentSortName.getSortId());
        docGroup.setSortName(parentSortName.getSortName());
        model.addAttribute("docGroup", docGroup);
        if(ToolUtil.isNotEmpty(flag) && flag.equals("1")){
            return PERSONAL_PREFIX + "personal_group_edit_new.html";
        }
        return PREFIX + "group_edit_new.html";
    }

    /**
     * 查询需要修改的人员的信息
     *
     * @return
     */
    @RequestMapping("/getPersonList")
    @ResponseBody
    public JSON getPersonList(String groupId, int page, int limit) {
        int beginIndex = page * limit - limit;
        List<Map> list = docGroupService.selectGroupUserById(groupId, beginIndex, limit);
        int counts = docGroupService.getGroupListCount(groupId, null);
        JSONObject json = new JSONObject();
        json.put("count", counts);
        json.put("data", list);
        json.put("msg", "success");
        json.put("code", 0);
        return json;
    }

    /**
     * @Author zoufeng
     * @Description  打开分组新增界面
     * @Date 8:56 2018/9/10
     * @Param [model, groupId]  分组id
     * @return java.lang.String 分组html
     **/
    @RequestMapping("/sortAdd")
    public String sortAdd(Model model,String groupId, String groupName) {
        model.addAttribute("parentSortId", groupId);
        model.addAttribute("parentSortName", groupName);
        model.addAttribute("sortId", "");
        model.addAttribute("sortName", "");
        return PREFIX + "sort_add.html";
    }

    /**
     * @Author zoufeng
     * @Description 获取分组及群组信息
     * @Date 16:06 2018/9/27
     * @Param id 分组或群组id
     * @return 分组及群组信息
     **/
    @RequestMapping("/sortAndGroupTree")
    @ResponseBody
    public List<Map<String, Object>> selectSortAndGroup(String id,String flag){
        List<Map<String, Object>> result = docGroupService.selectSortAndGroup(id,flag);
        return result;
    }

    /**
     * @Author zoufeng
     * @Description 群组新增
     * @Date 9:03 2018/9/28
     * @Param
     * @return
     **/
    @RequestMapping("/addSort")
    @ResponseBody
    public JSON addSort(DocGroupSort docGroupSort) {
        JSONObject json = new JSONObject();
        //检查群组名称是否已经存在
        int num = docGroupService.checkSortName(docGroupSort.getSortName(),docGroupSort.getParentSortId(),docGroupSort.getSortId(),docGroupSort.getGroupFlag());
        //得到查重的数量
        if (num > 0) {
            json.put("result", "0");
        } else {
            int savenum = docGroupService.addGroupSort(docGroupSort);
            //拼装操作历史记录
            List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
            DocResourceLog docResourceLog = new DocResourceLog();
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            String id = UUID.randomUUID().toString().replace("-", "");
            docResourceLog.setId(id);
            docResourceLog.setResourceId(docGroupSort.getSortId());
            docResourceLog.setOperateTime(ts);
            docResourceLog.setResourceType(2);
            docResourceLog.setUserId(ShiroKit.getUser().getId());
            docResourceLog.setOperateType(23);
            docResourceLog.setAddressIp(HttpKit.getIp());
            resInfoList.add(docResourceLog);
            iTopicDocManagerService.insertResourceLog(resInfoList);
            if (savenum == 1) {
                json.put("result", "1");
            } else {
                json.put("result", "2");
            }
        }
        return json;
    }

    /**
     * @Author zoufeng
     * @Description 修改分组信息
     * @Date 9:31 2018/9/28
     * @Param 实体
     * @return
     **/
    @RequestMapping("/editSort")
    @ResponseBody
    public JSON editSort(DocGroupSort docGroupSort) {
        JSONObject json = new JSONObject();
        int num = docGroupService.checkSortName(docGroupSort.getSortName(),docGroupSort.getParentSortId(),docGroupSort.getSortId(),docGroupSort.getGroupFlag());
        //得到查重的数量
        if (num > 0) {
            json.put("result", "0");
        } else {
            boolean res = docGroupService.updateSortInfo(docGroupSort);
            //拼装操作历史记录
            List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
            DocResourceLog docResourceLog = new DocResourceLog();
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            String id = UUID.randomUUID().toString().replace("-", "");
            docResourceLog.setId(id);
            docResourceLog.setResourceId(docGroupSort.getSortId());
            docResourceLog.setOperateTime(ts);
            docResourceLog.setResourceType(2);
            docResourceLog.setUserId(ShiroKit.getUser().getId());
            docResourceLog.setOperateType(25);
            docResourceLog.setAddressIp(HttpKit.getIp());
            resInfoList.add(docResourceLog);
            iTopicDocManagerService.insertResourceLog(resInfoList);
            //获取新增的数据条数
            if (res) {
                json.put("result", "1");
            } else {
                json.put("result", "2");
            }
        }
        return json;
    }

    /**
     * @Author zoufeng
     * @Description 获取分组信息
     * @Date 11:57 2018/9/28
     * @Param 分组id
     * @return
     **/
    @RequestMapping("/sortEdit")
    @ResponseBody
    public JSONObject sortEdit(String groupId,String flag) {
        JSONObject json = new JSONObject();
        DocGroupSort docGroupSort = docGroupService.selectSortInfo(groupId,flag);
        DocGroupSort ParentSortName = docGroupService.selectSortInfo(docGroupSort.getParentSortId(),flag);
        json.put("parentSortName", ParentSortName.getSortName());
        json.put("parentSortId", docGroupSort.getParentSortId());
        json.put("sortName", docGroupSort.getSortName());
        json.put("sortId", docGroupSort.getSortId());
        return json;
    }

    /**
     * @Author zoufeng
     * @Description 删除分组信息
     * @Date 14:44 2018/9/28
     * @Param [sortId] 分组id
     * @return com.alibaba.fastjson.JSON
     **/
    @RequestMapping("/deleteSort")
    @ResponseBody
    public int deleteSort(String groupId){
        int num = docGroupService.delGroupSortById(groupId);
        //拼装操作历史记录
        List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
        DocResourceLog docResourceLog = new DocResourceLog();
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        String id = UUID.randomUUID().toString().replace("-", "");
        docResourceLog.setId(id);
        docResourceLog.setResourceId(groupId);
        docResourceLog.setOperateTime(ts);
        docResourceLog.setResourceType(2);
        docResourceLog.setUserId(ShiroKit.getUser().getId());
        docResourceLog.setOperateType(27);
        docResourceLog.setAddressIp(HttpKit.getIp());
        resInfoList.add(docResourceLog);
        iTopicDocManagerService.insertResourceLog(resInfoList);
        return num;
    }

    /**
     * @Author zoufeng
     * @Description 获取分组树 数据
     * @Date 16:57 2018/9/28
     * @Param
     * @return
     **/
    @RequestMapping("/getSortTree")
    @ResponseBody
    public List<Map<String,Object>> getSortTreeData(String flag){
        return docGroupService.getSortTreeData(flag);
    }

    /**
     * @Author zoufeng
     * @Description  打开分组树界面
     * @Date 8:56 2018/9/10
     * @Param [model, groupId]  分组id
     * @return java.lang.String 分组html
     **/
    @RequestMapping("/sortTree")
    public String sortTree(Model model,String flag) {
        if(ToolUtil.isNotEmpty(flag) && flag.equals("1")){
            return PERSONAL_PREFIX + "personal_sort_tree.html";
        }
        return PREFIX + "sort_tree.html";
    }

    /**
     * 设置权限获取公共群组和个人群组
     * @param id 分组id
     * @return 群组集合
     */
    @RequestMapping("/getGroupAndPergroupTree")
    @ResponseBody
    public List<Map<String, Object>> getGroupAndPergroupTree(String id){
        List<Map<String, Object>> result = docGroupService.getGroupAndPergroupTree(id);
        return result;
    }

    /**
     * 分享获取公共群组和个人群组
     * @return 群组集合
     */
    @RequestMapping("/getGroupAndPergroupTreeForShare")
    @ResponseBody
    public List<JSTreeModel> getGroupAndPergroupTreeForShare(){
        List<Map<String, Object>> result = docGroupService.getGroupAndPergroupTree(null);
        List<JSTreeModel> result2= new ArrayList();
        for ( int i=0;i<result.size();i++){
            JSTreeModel jsTreeModel = new JSTreeModel();
            jsTreeModel.setId(result.get(i).get("ID").toString());
            jsTreeModel.setCode(result.get(i).get("CODE").toString());
            jsTreeModel.setText(result.get(i).get("TEXT").toString());
            jsTreeModel.setParent(result.get(i).get("PARENT").toString());
            jsTreeModel.setType(result.get(i).get("CODE").toString());
            /*if(StringUtils.equals("edef73ac63764885b95e7f572d09b0ff",result.get(i).get("ID").toString())){
                jsTreeModel.setType("isRoot");
            }*/
            /*if(StringUtils.equals("#",result.get(i).get("ID").toString())){
                jsTreeModel.setType("isRoot");
            }*/
            result2.add(jsTreeModel);
        }
        return result2;
    }

    @RequestMapping("/getPersonalGroupTree")
    @ResponseBody
    public List<JSTreeModel> getPersonalGroupTree(){
        List<Map<String, Object>> result = docGroupService.selectSortAndGroup(null,"1");
        List<JSTreeModel> result2= new ArrayList();
        for ( int i=0;i<result.size();i++){
            JSTreeModel jsTreeModel = new JSTreeModel();
            jsTreeModel.setId(result.get(i).get("ID").toString());
            jsTreeModel.setCode(result.get(i).get("CODE").toString());
            jsTreeModel.setText(result.get(i).get("TEXT").toString());
            jsTreeModel.setParent(result.get(i).get("PARENT").toString());
            jsTreeModel.setType(result.get(i).get("CODE").toString());
            if(StringUtils.equals("edef73ac63764885b95e7f572d09b0ff",result.get(i).get("ID").toString())){
                jsTreeModel.setType("isRoot");
            }
            result2.add(jsTreeModel);
        }
        return result2;
    }
}
