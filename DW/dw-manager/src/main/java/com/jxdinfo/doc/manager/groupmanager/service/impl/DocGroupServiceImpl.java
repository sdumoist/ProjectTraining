package com.jxdinfo.doc.manager.groupmanager.service.impl;/**
 * Created by zoufeng on 2018/9/27.
 */

import com.jxdinfo.doc.manager.groupmanager.dao.DocGroupMapper;
import com.jxdinfo.doc.manager.groupmanager.model.DocGroup;
import com.jxdinfo.doc.manager.groupmanager.model.DocGroupSort;
import com.jxdinfo.doc.manager.groupmanager.service.DocGroupService;
import com.jxdinfo.hussar.bsp.permit.service.ISysIdtableService;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.util.ToolUtil;
import dm.jdbc.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @ClassName DocGroupServiceImpl
 * @Description TODO
 * @Author zoufeng
 * @Date 2018/9/27 9:44
 * @Version 1.0
 **/
@Service
public class DocGroupServiceImpl implements DocGroupService {
    @Resource
    private DocGroupMapper docGroupMapper;

    /**
     * 获取编号公共方法
     */
    @Autowired
    private ISysIdtableService sysIdtableService;

    @Value("${personalGroup.query:false}")
    private boolean personalGroupQueryFlag;

    /**
     * 群组列表查询
     *
     * @param uerName    人员名
     * @param startIndex 开始位置
     * @param pageSize   每页数据条数
     * @return list
     */
    public List<DocGroup> groupList(String groupId, String uerName, int startIndex, int pageSize) {
        return docGroupMapper.groupList(groupId, uerName, startIndex, pageSize);
    }

    /**
     * 群组列表总数查询
     *
     * @param uerName 人员名
     * @return 专题总数
     */
    public int getGroupListCount(String groupId, String uerName) {
        return docGroupMapper.getGroupListCount(groupId, uerName);
    }

    /**
     * 新增群组
     *
     * @param docGroup 群组对象
     * @return 新增数量
     */
    public int addGroup(DocGroup docGroup) {
        docGroup.setParenteGroupId("af9090a8fdfe487f9487df9cdc6e88");
        docGroup.setGroupLevel(1);
        docGroup.setCreateUserId(ShiroKit.getUser().getId());
        int num = docGroupMapper.insertGroup(docGroup);
        if (num > 0 && null != docGroup.getUserId()) {
            String[] uersIds = docGroup.getUserId().split(",");
            for(int i=0 ;i<uersIds.length;i++){
                docGroupMapper.insertGroupUser(docGroup.getGroupId(), uersIds[i]);
            }

        }
        return num;
    }

    /**
     * 新增群组人员
     *
     * @param docGroup 群组对象
     * @return 新增数量
     */
    public int addGroupUser(DocGroup docGroup) {
        int num = 0;
        if (null != docGroup.getUserId()) {
            docGroupMapper.deleteByPrimaryKey(docGroup.getGroupId());
            String[] uersIds = docGroup.getUserId().split(",");
            for(int i=0 ;i<uersIds.length;i++){
                num=     docGroupMapper.insertGroupUser(docGroup.getGroupId(), uersIds[i]);
            }
        }
        return num;
    }

    /**
     * 检查群组名称是否存在
     *
     * @param groupName 群组名称
     * @return 存在的数量
     */
    public int checkGroupExist(String groupName, String groupId,String groupFlag) {
        String loginId = ShiroKit.getUser().getId();
        return docGroupMapper.checkGroupExist(groupName, groupId,groupFlag,loginId);
    }

    /**
     * 更新群组
     *
     * @param docGroup 群组对象
     * @return 新增数量
     */
    public int updateGroup(DocGroup docGroup) {
        int num = docGroupMapper.updateByPrimaryKey(docGroup);
        if (num > 0 && null != docGroup.getUserId()) {
            docGroupMapper.deleteByPrimaryKey(docGroup.getGroupId());
            String[] uersIds = docGroup.getUserId().split(",");
            for( int i=0 ;i<uersIds.length;i++){
                docGroupMapper.insertGroupUser(docGroup.getGroupId(), uersIds[i]);
            }

        }
        return num;
    }

    /**
     * 删除群组
     *
     * @param groupId
     * @return
     */
    public int delGroupById(String groupId) {
        docGroupMapper.deleteByPrimaryKey(groupId);
        return docGroupMapper.delGroupById(groupId);
    }

    /**
     * 查看群组
     *
     * @param groupId
     * @return
     */
    public DocGroup selectGroupById(String groupId) {
        return docGroupMapper.selectGroupById(groupId);
    }

    /**
     * 获取群组人员
     *
     * @param groupId
     * @return
     */
    public List<Map> selectGroupUserById(String groupId, int startIndex, int pageSize) {
        List<Map> list = docGroupMapper.selectGroupUserById(groupId, startIndex, pageSize);
        return list;
    }

    /**
     * 获取群组人员
     *
     * @param deptId
     * @return
     */
    public List<Map<String, Object>> selectGroupByName(String deptId) {
        List<Map<String, Object>> list = docGroupMapper.selectGroupByName(deptId);
        return list;
    }

    /**
     * 获取当前登录人所在的所有群组
     *
     * @param userId
     * @return
     */
    public List<String> getPremission(String userId) {
        List<String> list = docGroupMapper.getPremission(userId);
        return list;
    }

    /**
     * 删除群组人员
     *
     * @param groupId 群组ID
     * @param idList  人员ID集合
     * @return
     */
    public int delGroupUserById(String groupId, List<String> idList) {
        return docGroupMapper.delGroupUserById(groupId, idList);
    }

    /**
     * @Author zoufeng
     * @Description 新增分组
     * @Date 11:30 2018/9/27
     * @Param [docGroupSort] 分组对象
     * @return int 插入条数
     **/
    public int addGroupSort(DocGroupSort docGroupSort) {
        String groupSortId = UUID.randomUUID().toString().replaceAll("-", "");
        docGroupSort.setSortId(groupSortId);
        String currentCode = this.sysIdtableService.getCurrentCode("SORT_NUM", "doc_group_sort");
        int bigNum = Integer.parseInt(currentCode);
        docGroupSort.setShowOrder(bigNum);
        docGroupSort.setCreateUserId(ShiroKit.getUser().getId());
        int num = docGroupMapper.insertGroupSort(docGroupSort);
        return num;
    }

   /**
    * @Author zoufeng
    * @Description 删除分组信息
    * @Date 11:30 2018/9/27
    * @Param [groupSortId] 分组id
    * @return int 删除数量
    **/
    public int delGroupSortById(String groupSortId) {
        int num = 0;
        String res = getCheckSort(groupSortId);
        if("false".equals(res)){
            num = docGroupMapper.deleteSort(groupSortId);
        }else{
            num = 2;
        }
        return num;
    }

    /**
     * @Author zoufeng
     * @Description 查询分组下是否存在群组
     * @Date 10:53 2018/9/27
     * @Param groupSortId 分组id
     * @return false没有子节点,true含有子节点
     **/
    public String getCheckSort(String groupSortId){
        String result = "false";

        int num = docGroupMapper.getSortToGroup(groupSortId);
        if(num > 0){
            result = "true";
        }

        return result;
    }
    
    /**
     * @Author zoufeng
     * @Description 获取分组信息
     * @Date 11:39 2018/9/27
     * @Param 
     * @return 
     **/
    public DocGroupSort selectSortInfo(String groupSortId,String flag){
        String sortId = "";
        if(ToolUtil.isEmpty(groupSortId)){
            sortId = "af9090a8fdfe487f9487df9cdc6e88";
        }else{
            sortId = groupSortId;
        }
        Map<String,Object> map = docGroupMapper.selectSortInfo(sortId);
        DocGroupSort docGroupSort = new DocGroupSort();
        if(map != null){
            docGroupSort.setSortId(map.get("sortId").toString());
            docGroupSort.setSortName(map.get("sortName").toString());
            docGroupSort.setParentSortId(map.get("parentSortId").toString());
        }else{
            if(ToolUtil.isNotEmpty(flag) && flag.equals("1")){
                docGroupSort.setSortId("edef73ac63764885b95e7f572d09b0ff");
                docGroupSort.setSortName("个人群组");
            }else{
                docGroupSort.setSortId("#");
                docGroupSort.setSortName("金现代");
            }
        }
        return docGroupSort;
    }
    
    /**
     * @Author zoufeng
     * @Description 更新分组信息
     * @Date 11:58 2018/9/27
     * @Param 分组实体
     * @return 是否更新成功
     **/
    public boolean updateSortInfo(DocGroupSort docGruopSort){
        boolean result = false;
        int num  = docGroupMapper.updateSortInfo(docGruopSort);
        if(num > 0){
            result = true;
        }

        return result;
    }
    
    /**
     * @Author zoufeng
     * @Description 获取分组及群组信息
     * @Date 16:06 2018/9/27
     * @Param id 分组或群组id
     * @return 分组及群组信息
     **/
    public List<Map<String, Object>> selectSortAndGroup(String id,String flag) {
        String loginId = ShiroKit.getUser().getId();
        List<Map<String, Object>> list = docGroupMapper.getSortAndGroupTree(id,flag,loginId);
        if(ToolUtil.isNotEmpty(flag) && flag.equals("1")){
            boolean temp = true;
            // 看是否有根节点
            if(list.size()>0){
                for(Map<String,Object> map:list){
                    if(StringUtils.equals("edef73ac63764885b95e7f572d09b0ff",map.get("ID").toString())){
                        temp = false;
                    }
                }
            }
            if (list.size() ==0 || temp){
                Map<String, Object> map = new HashMap<>();
                map.put("ID","edef73ac63764885b95e7f572d09b0ff");
                map.put("CODE","GROUP");
                map.put("TEXT","个人群组");
                map.put("PARENT","#");
                map.put("TYPE","9");
                map.put("USERID","superadmin");
                map.put("ISSORT","1");
                map.put("showOrder","1");
                list.add(map);
            }
        }
        return list;
    }

    /**
     * @Author zoufeng
     * @Description 获取分组及群组信息
     * @Date 16:06 2018/9/27
     * @Param id 分组或群组id
     * @return 分组及群组信息
     **/
    public List<Map<String, Object>> selectSortAndGroup(String id,String flag,String loginId) {
        List<Map<String, Object>> list = docGroupMapper.getSortAndGroupTree(id,flag,loginId);
        if(ToolUtil.isNotEmpty(flag) && flag.equals("1")){
            boolean temp = true;
            // 看是否有根节点
            if(list.size()>0){
                for(Map<String,Object> map:list){
                    if(StringUtils.equals("edef73ac63764885b95e7f572d09b0ff",map.get("ID").toString())){
                        temp = false;
                    }
                }
            }
            if (list.size() ==0 || temp){
                Map<String, Object> map = new HashMap<>();
                map.put("ID","edef73ac63764885b95e7f572d09b0ff");
                map.put("CODE","GROUP");
                map.put("TEXT","个人群组");
                map.put("PARENT","#");
                map.put("TYPE","9");
                map.put("USERID","superadmin");
                map.put("ISSORT","1");
                map.put("showOrder","1");
                list.add(map);
            }
        }
        return list;
    }

   /**
    * @Author zoufeng
    * @Description 判断分组名称是否存在
    * @Date 9:08 2018/9/28
    * @Param [sortName] 分组名称
    * @return int 名称数量
    **/
    public int checkSortName(String sortName,String parentSortId,String sortId,String groupFlag){
        int num = 0;
        String loginId = ShiroKit.getUser().getId();
        num = docGroupMapper.checkSortName(sortName,parentSortId,sortId,groupFlag,loginId);
        return num;
    }

    /**
     * @Author zoufeng
     * @Description 获取分组树数据
     * @Date 17:05 2018/9/28
     * @Param []
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     **/
    public List<Map<String,Object>> getSortTreeData(String flag){
        String loginId = ShiroKit.getUser().getId();
        List<Map<String, Object>> list = docGroupMapper.getSortTreeData(flag, loginId);
        if(StringUtil.isNotEmpty(flag) && flag.equals("1")){
            boolean temp = true;
            // 看是否有根节点
            if(list.size()>0){
                for(Map<String,Object> map:list){
                    if(StringUtils.equals("edef73ac63764885b95e7f572d09b0ff",map.get("ID").toString())){
                        temp = false;
                    }
                }
            }
            if (list.size() ==0 || temp){
                Map<String, Object> map = new HashMap<>();
                map.put("ID","edef73ac63764885b95e7f572d09b0ff");
                map.put("CODE","GROUP");
                map.put("TEXT","个人群组");
                map.put("PARENT","#");
                map.put("TYPE","9");
                map.put("USERID","superadmin");
                map.put("ISSORT","1");
                map.put("showOrder","1");
                list.add(map);
            }
        }
        return list;
    }

    /**
     * @Author zoufeng
     * @Description 查询是否已经授权
     * @Date 15:05 2018/10/26
     * @Param [groupId] 群组id
     * @return int
     **/
    public int getIsAuthority(String groupId){
        int res = 0;
        int isFolder = docGroupMapper.getIsFolder(groupId);
        int isFile = docGroupMapper.getIsFile(groupId);
        if((isFile + isFolder) > 0){
            res = 1;
        }
        return res;
    }

    @Override
    public List<Map<String, Object>> getGroupAndPergroupTree(String id) {
        String userId = ShiroKit.getUser().getId();
        List<Map<String, Object>> list = new ArrayList<>();
        List<Map<String, Object>> publicList = new ArrayList<>();
        List<Map<String, Object>> personalList = new ArrayList<>();
        if(StringUtils.isNotEmpty(userId)){
            publicList  = selectSortAndGroup(id,null);
            personalList = selectSortAndGroup(id,"1");
        }
        boolean flag = true;
        // 看是否有根节点
        if(personalList.size()>0){
            for(Map<String,Object> map:personalList){
                if(StringUtils.equals("edef73ac63764885b95e7f572d09b0ff",map.get("ID").toString())){
                    flag = false;
                }
            }
        }
        if (personalList.size() !=0 && flag){
            Map<String, Object> map = new HashMap<>();
            map.put("ID","edef73ac63764885b95e7f572d09b0ff");
            map.put("CODE","GROUP");
            map.put("TEXT","个人群组");
            map.put("PARENT","#");
            map.put("TYPE","9");
            map.put("USERID","superadmin");
            map.put("ISSORT","1");
            map.put("showOrder","1");
            personalList.add(map);
        }
        list.addAll(publicList);
        if(personalGroupQueryFlag){
            list.addAll(personalList);
        }
        return list;
    }
}
