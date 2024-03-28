package com.jxdinfo.doc.manager.historymanager.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface RelationHistoryMapper {
    /**
     * @author luzhanzhao
     * @date 2018-11-27
     * @description 获取操作记录表中的用户列表
     * @return 用户操作记录表中的用户id列表
     */
    List<Map> getUsers();

    /**
     * @author luzhanzhao
     * @date 2018-11-27
     * @param userId 当前要查询的用户id
     * @param isEmpty 该数据库是否为空，为空则不对时间进行限制，如果不为空则只查当天
     * @return 当前用户的相关操作记录
     */
    List<Map> getUserResourceLog(@Param("userId") String userId, @Param("isEmpty") String isEmpty);

    /**
     * @author luzhanzhao
     * @date 2018-11-27
     * @description 查询是否存在该上下篇的关系
     * @param map 按current-child信息查询数据库是否存在该关系
     * @return 如果返回0则不存在
     */
    int selectCount(Map map);

    /**
     * @author luzhanzhao
     * @date 2018-11-27
     * @description 将上下篇关系按current-child存入数据库中
     * @param map currentId-当前篇id；childId篇id
     * @return
     */
    int insertIntoDocRelation(Map map);

    /**
     * @author luzhanzhao
     * @date 2018-11-27
     * @description 对存在的current-child关系列进行数据更新
     * @param map 要更新的current-child关系列
     * @return
     */
    int updateDocRelation(Map map);

    /**
     * @author luzhanzhao
     * @date 2018-11-27
     * @description 查询关系表是否为空，如果返回0则为空
     * @return 关系表中的条数
     */
    int selectRelationCount();
}
