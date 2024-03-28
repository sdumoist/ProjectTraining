package com.jxdinfo.doc.manager.groupmanager.dao;

import com.jxdinfo.doc.manager.groupmanager.model.DocGroup;
import com.jxdinfo.doc.manager.groupmanager.model.DocGroupSort;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface DocGroupMapper {

    /**
     * 群组列表查询
     * @param uerName 人员名
     * @param startIndex 开始位置
     * @param pageSize  每页数据条数
     * @return list
     */
    List<DocGroup> groupList(@Param("groupId") String groupId, @Param("uerName") String uerName, @Param("startIndex") int startIndex, @Param("pageSize") int pageSize);

    int getGroupListCount(@Param("groupId") String groupId, @Param("uerName") String uerName);

    int insertGroup(DocGroup record);

    int insertGroupUser(@Param("groupId") String groupId, @Param("userId") String userId);

    int updateByPrimaryKey(DocGroup record);

    int deleteByPrimaryKey(String groupId);

    int checkGroupExist(@Param("groupName") String groupName, @Param("groupId") String groupId,@Param("groupFlag") String groupFlag,@Param("loginId") String loginId);

    int delGroupById(String groupId);

    int delGroupUserById(@Param("groupId") String groupId, @Param("list") List<String> list);

    DocGroup selectGroupById(String groupId);

    List<Map<String, Object>> selectGroupByName(@Param("groupId") String groupId);

    List<Map> selectGroupUserById(@Param("groupId") String groupId, @Param("startIndex") int startIndex, @Param("pageSize") int pageSize);

    List<String> getPremission(@Param("userId") String userId);

    /** 新增群组分组 */
    int insertGroupSort(DocGroupSort docGroupSort);

    /** 删除分组 */
    int deleteSort(@Param("groupSortID") String groupSortID);

    /** 获取分组下是否含有群组及分组 */
    int getSortToGroup(@Param("groupSortID") String groupSortID);

    /** 获取分组信息 */
    Map<String,Object> selectSortInfo(@Param("groupSortID") String groupSortID);

    /** 更新分组信息 */
    int updateSortInfo(DocGroupSort docGroupSort);

    /** 获取分组及群组信息*/
    List<Map<String , Object>> getSortAndGroupTree(@Param("id") String id,@Param("flag") String flag,@Param("loginId") String loginId);

    /** 查询是否存在同名分组 */
    int checkSortName(@Param("sortName") String sortName, @Param("parentSortId") String parentSortId, @Param("sortId") String sortId,@Param("groupFlag") String groupFlag,@Param("loginId") String loginId);

    /** 获取分组树数据 */
    List<Map<String,Object>> getSortTreeData(@Param("flag") String flag,@Param("loginId") String loginId);

    /** 判断群组是否给目录授权 */
    int getIsFolder(@Param("groupId") String groupId);

    /** 判断群组是否给文件授权 */
    int getIsFile(@Param("groupId") String groupId);
}