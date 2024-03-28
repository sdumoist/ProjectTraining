package com.jxdinfo.doc.manager.personalcenter.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface PersonalOperateMapper {
    /**
     * @author luzhazhao
     * @date 2018-11-16
     * @description 获取历史记录的方法
     * @param userId 当前登录用户id
     * @param opType 要查询的记录类型
     * @param startIndex 历史记录的起始下标
     * @param pageSize 每页记录长度
     * @param name 搜索关键字
     * @return 查询到的历史记录列表
     */
    List<Map> getMyHistory(@Param("userId") String userId, @Param("opType") String opType,
                           @Param("startIndex") int startIndex, @Param("pageSize") int pageSize,
                           @Param("name") String name, @Param("typeArr") String[] typeArr, @Param("order") String order,
                           @Param("levelCode") String levelCode, @Param("orgId") String orgId);

    List<Map> getMyHistoryMobile(@Param("userId") String userId, @Param("opType") String opType,
                           @Param("startIndex") int startIndex, @Param("pageSize") int pageSize,
                           @Param("name") String name, @Param("typeArr") String[] typeArr, @Param("order") String order,
                           @Param("levelCode") String levelCode, @Param("orgId") String orgId,@Param("folderIds") List folderIds);
    /**
     * @author lishilin
     * @date 2018-11-16
     * @description 获取历史记录的方法
     * @param userId 当前登录用户id
     * @param opType 要查询的记录类型
     * @param startIndex 历史记录的起始下标
     * @param pageSize 每页记录长度
     * @param name 搜索关键字
     * @return 查询到的历史记录列表
     */
    List<Map> getMyHistoryClient(@Param("userId") String userId, @Param("opType") String opType,
                           @Param("startIndex") int startIndex, @Param("pageSize") int pageSize,
                           @Param("name") String name, @Param("typeArr") String[] typeArr, @Param("order") String order,
                           @Param("levelCode") String levelCode, @Param("orgId") String orgId, @Param("timeType") String timeType);

    /**
     * @author luzhanzhao
     * @date 2018-11-16
     * @description 根据条件获取历史记录总数
     * @param userId 当前登录的用户id
     * @param opType 要查询的记录的类型
     * @param name 要查询记录的关键字
     * @return 查询到的记录条数
     */
    int getMyHistoryCount(@Param("userId") String userId, @Param("opType") String opType, @Param("name") String name);

    int getMyHistoryCountMobile(@Param("userId") String userId, @Param("opType") String opType, @Param("name") String name,List folderIds);

    /**
     * @author lishilin
     * @date 2018-11-16
     * @description 根据条件获取历史记录总数
     * @param userId 当前登录的用户id
     * @param opType 要查询的记录的类型
     * @param name 要查询记录的关键字
     * @return 查询到的记录条数
     */
    int getMyHistoryCountClient(@Param("userId") String userId, @Param("opType") String opType, @Param("name") String name, @Param("timeType") String timeType);

    /**
     * @author luzhanzhao
     * @date 2018-11-19
     * @param histories 要删除的记录对应的文件id集合
     * @param userId 当前登录的用户id
     * @param opType 操作类型（3：预览；4：下载)
     * @return 删除结果
     */
    int deleteHistory(@Param("histories") String[] histories, @Param("userId") String userId, @Param("opType") String opType);

    /**
     * @author luzhanzhao
     * @date 2018-11-19
     * @param userId 当前登录的用户id
     * @param opType 要清空的记录对应
     * @return 结果
     */
    int clearHistory(@Param("userId") String userId, @Param("opType") String opType);

    /**
     * @author luzhanzhao
     * @date 2018-11-16
     * @description 根据文件id查询历史记录的数量
     * @param docId 要查询的文件id
     * @param userId 当前登录的用户id
     * @param opType 查询记录的类型
     * @return 查询到的条数
     */
    int getMyHistoryCountByFileId(@Param("docId") String docId, @Param("userId") String userId, @Param("opType") String opType);

    /**
     * @author yjs
     * @date 2018-11-19
     * @description 取消个人收藏的mapper
     * @param docId 要取消收藏的文档id
     * @param userId 当前登录的用户id
     * @param opType 操作类型
     * @return 取消收藏的状态
     */
    int cancelCollection(@Param("docId") String docId, @Param("userId") String userId, @Param("opType") String opType);

    /**
     * @author yjs
     * @date 2018-11-19
     * @description 删除收藏记录的mapper
     * @param ids 要删除的记录id
     * @return 删除状态
     */
    int deleteCollection(@Param("ids") String[] ids);
}
