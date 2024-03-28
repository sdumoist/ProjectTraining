package com.jxdinfo.doc.client.groupmananger.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxdinfo.doc.client.response.ApiResponse;
import com.jxdinfo.doc.common.jwt.util.JWTUtil;
import com.jxdinfo.doc.common.util.StringUtil;
import com.jxdinfo.doc.manager.docmanager.model.DocFileAuthority;
import com.jxdinfo.doc.manager.docmanager.service.DocFileAuthorityService;
import com.jxdinfo.doc.manager.foldermanager.model.DocFoldAuthority;
import com.jxdinfo.doc.manager.foldermanager.service.IDocFoldAuthorityService;
import com.jxdinfo.doc.manager.groupmanager.model.DocGroup;
import com.jxdinfo.doc.manager.groupmanager.model.DocGroupSort;
import com.jxdinfo.doc.manager.groupmanager.service.DocGroupService;
import com.jxdinfo.hussar.bsp.permit.service.ISysIdtableService;
import com.jxdinfo.hussar.core.base.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by Administrator on 2018/8/4 0004.
 */
@Controller
@RequestMapping("/client/group")
public class GroupController extends BaseController {


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

    /**
     * 目录服务类
     */
    @Resource
    private JWTUtil jwtUtil;

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
        int num = docGroupService.checkGroupExist(docGroup.getGroupName(), null,null);
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
        int num = docGroupService.checkGroupExist(docGroup.getGroupName(), docGroup.getGroupId(),null);
        //得到查重的数量
        if (num > 0) {
            json.put("result", "0");
        } else {
            int editNum = docGroupService.updateGroup(docGroup);
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
     * @Description 获取分组及群组信息
     * @Date 16:06 2018/9/27
     * @Param id 分组或群组id
     * @return 分组及群组信息
     **/
    @RequestMapping("/sortAndGroupTree")
    @ResponseBody
    public ApiResponse selectSortAndGroup(String id){
        String userId = jwtUtil.getSysUsers().getUserId();
        List<Map<String, Object>> result = docGroupService.selectSortAndGroup(id,null,userId);
        return ApiResponse.data(200,result,"");
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
        int num = docGroupService.checkSortName(docGroupSort.getSortName(),docGroupSort.getParentSortId(),docGroupSort.getSortId(),null);
        //得到查重的数量
        if (num > 0) {
            json.put("result", "0");
        } else {
            int savenum = docGroupService.addGroupSort(docGroupSort);
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
        int num = docGroupService.checkSortName(docGroupSort.getSortName(),docGroupSort.getParentSortId(),docGroupSort.getSortId(),null);
        //得到查重的数量
        if (num > 0) {
            json.put("result", "0");
        } else {
            boolean res = docGroupService.updateSortInfo(docGroupSort);
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
    public JSONObject sortEdit(String groupId) {
        JSONObject json = new JSONObject();
        DocGroupSort docGroupSort = docGroupService.selectSortInfo(groupId,null);
        DocGroupSort ParentSortName = docGroupService.selectSortInfo(docGroupSort.getParentSortId(),null);
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
    public List<Map<String,Object>> getSortTreeData(){
        return docGroupService.getSortTreeData(null);
    }


}
