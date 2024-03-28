package com.jxdinfo.doc.manager.groupmanager.service;

import com.jxdinfo.doc.manager.groupmanager.model.DocGroup;
import com.jxdinfo.doc.manager.groupmanager.model.DocGroupSort;

import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2018/8/4 0004.
 */
public interface DocGroupService {

    /**
     * 群组列表查询
     * @param uerName 人员名
     * @param startIndex 开始位置
     * @param pageSize  每页数据条数
     * @return list
     */
    public List<DocGroup> groupList(String groupId, String uerName, int startIndex, int pageSize);

    /**
     * 群组列表总数查询
     * @param uerName 人员名
     * @return 专题总数
     */
    public int getGroupListCount(String groupId, String uerName);

    /**
     * 新增群组
     * @param docGroup 群组对象
     * @return 新增数量
     */
    public int addGroup(DocGroup docGroup);

    /**
     * 新增群组人员
     * @param docGroup 群组对象
     * @return 新增数量
     */
    public int addGroupUser(DocGroup docGroup);

    /**
     * 检查群组名称是否存在
     * @param groupName  群组名称
     * @return 存在的数量
     */
    public int checkGroupExist(String groupName, String groupId,String groupFlag);

    /**
     * 更新群组
     * @param docGroup 群组对象
     * @return 新增数量
     */
    public int updateGroup(DocGroup docGroup);

    /**
     * 删除群组
     * @param groupId
     * @return
     */
    public int delGroupById(String groupId);

    /**
     * 查看群组
     * @param groupId
     * @return
     */
    public DocGroup selectGroupById(String groupId);

    /**
     * 获取群组人员
     * @param groupId
     * @return
     */
    public List<Map> selectGroupUserById(String groupId, int startIndex, int pageSize);

    /**
     * 获取群组人员
     * @param deptId
     * @return
     */
    public List<Map<String, Object>> selectGroupByName(String deptId);

    /**
     * 获取当前登录人所在的所有群组
     * @param userId
     * @return
     */
    public  List<String> getPremission(String userId);
    /**
     * @Author zoufeng
     * @Description 获取分组信息
     * @Date 11:39 2018/9/27
     * @Param
     * @return
     **/
    public DocGroupSort selectSortInfo(String groupSortId,String flag);

    /**
     * 删除群组人员
     * @param groupId 群组ID
     * @param idList 人员ID集合
     * @return
     */
    public int delGroupUserById(String groupId, List<String> idList);

    /**
     * 新增群分组
     * @param docGroupSort 分组对象
     * @return 新增数量
     */
    public int addGroupSort(DocGroupSort docGroupSort);

    /**
     * @Author zoufeng
     * @Description 获取分组及群组信息
     * @Date 16:06 2018/9/27
     * @Param id 分组或群组id
     * @return 分组及群组信息
     **/
    public List<Map<String, Object>> selectSortAndGroup(String id,String flag);

    /**
     * @Author zoufeng
     * @Description 获取分组及群组信息
     * @Date 16:06 2018/9/27
     * @Param id 分组或群组id
     * @return 分组及群组信息
     **/
    public List<Map<String, Object>> selectSortAndGroup(String id,String flag,String loginId);

    /**
     * @Author zoufeng
     * @Description 判断分组名称是否存在
     * @Date 9:08 2018/9/28
     * @Param [sortName] 分组名称
     * @return int 名称数量
     **/
    public int checkSortName(String sortName, String parentSortId, String sortId,String groupFlag);

    /**
     * @Author zoufeng
     * @Description 更新分组信息
     * @Date 11:58 2018/9/27
     * @Param 分组实体
     * @return 是否更新成功
     **/
    public boolean updateSortInfo(DocGroupSort docGruopSort);

    /**
     * @Author zoufeng
     * @Description 删除分组信息
     * @Date 11:30 2018/9/27
     * @Param [groupSortId] 分组id
     * @return int 删除数量
     **/
    public int delGroupSortById(String groupSortId);

    /**
     * @Author zoufeng
     * @Description 获取分组树数据
     * @Date 17:05 2018/9/28
     * @Param []
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     **/
    public List<Map<String,Object>> getSortTreeData(String flag);

    /**
     * @Author zoufeng
     * @Description 查询是否已经授权
     * @Date 15:05 2018/10/26
     * @Param [groupId] 群组id
     * @return int
     **/
    public int getIsAuthority(String groupId);

    /**
     * @Author
     * @Description 获取分组群组和用户组信息
     * @Date 16:06 2018/9/27
     * @Param id 分组或群组id
     * @return 分组及群组信息
     **/
    public List<Map<String, Object>> getGroupAndPergroupTree(String id);

}
