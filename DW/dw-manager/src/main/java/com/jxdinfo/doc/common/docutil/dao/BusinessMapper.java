package com.jxdinfo.doc.common.docutil.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;
import java.util.Map;

/**
 * @ClassName businessMapper
 * @Description 文库业务Mapper
 * @Author zoufeng
 * @Date 2018/9/10 9:25
 * @Version 1.0
 **/
public interface
BusinessMapper extends BaseMapper<T> {

    List<Map<String, Object>> getDicListByType(@Param("typeId") String var1);

    /**
     * 上移下移交换showorder
     * @param table 表名
     * @param idColumn 排序字段名
     * @param idOne 需要交换的id
     * @param idTwo 被交换的id
     * @return
     */
    int changeShowOrder(@Param("table") String table, @Param("idColumn") String idColumn, @Param("idOne") String idOne, @Param("idTwo") String idTwo);

    /**
     * 查询拥有权限的目录层级码
     * @param groupIds 群组id （1,2,3形式）
     * @param userId 用户ID
     * @return string 逗号隔开形式（'2','3'）
     */
    public String getLevelCodeByUser(@Param("groupIds") String groupIds, @Param("userId") String userId, @Param("type") String type, @Param("orgId") String orgId, @Param("roleIds") String roleIds);

    /**
     * 查询拥有权限的目录层级码
     * @param groupIds 群组id （1,2,3形式）
     * @param userId 用户ID
     * @return string 逗号隔开形式（'2','3'）
     */
    public String getLevelCodeByUserByUpload(@Param("groupIds") String groupIds, @Param("userId") String userId, @Param("type") String type, @Param("orgId") String orgId, @Param("roleIds") String roleIds);

    /**
     * 查询拥有权限的上级目录层级码
     * @param groupIds 群组id （1,2,3形式）
     * @param userId 用户ID
     * @return string 逗号隔开形式（'2','3'）
     */
    public String getUpLevelCodeByUserByUpload(@Param("groupIds") String groupIds, @Param("userId") String userId, @Param("type") String type, @Param("orgId") String orgId, @Param("roleIds") String roleIds);

    /**
     * 查询拥有权限的上级目录层级码
     * @param groupIds 群组id （1,2,3形式）
     * @param userId 用户ID
     * @return string 逗号隔开形式（'2','3'）
     */
    public String getUpLevelCodeByUser(@Param("groupIds") String groupIds, @Param("userId") String userId, @Param("type") String type, @Param("orgId") String orgId, @Param("roleIds") String roleIds);

    /**
     * 查询拥有权限的上级文件所在目录层级码
     * @param groupIds 群组id （1,2,3形式）
     * @param userId 用户ID
     * @return string 逗号隔开形式（'2','3'）
     */
    public String getFileLevelCodeByUser(@Param("groupIds") String groupIds, @Param("userId") String userId, @Param("levelCodeString") String levelCodeString, @Param("orgId") String orgId, @Param("roleIds") String roleIds);

    /**
     * @Author zoufeng
     * @Description 获取父节点集合
     * @Date 20:29 2018/10/15
     * @Param 组织机构id
     * @return
     **/
    public String getParentList(@Param("orgId") String orgId);

    /**
     * @Author zoufeng
     * @Description 查询文档的权限
     * @Date 17:59 2018/10/26
     * @Param
     * @return
     **/
    public String getFileUpLeveCode(@Param("groupIds") String groupIds, @Param("userId") String userId, @Param("levelCode") String levelCode, @Param("orgId") String orgId, @Param("type") String type, @Param("roleIds") String roleIds);

    public String testMysql(@Param("param1") String param1);

    public String getLevelCodeByUserUpload(@Param("groupIds") String groupIds, @Param("userId") String userId, @Param("type") String type, @Param("orgId") String orgId, @Param("roleIds") String roleIds);

}
