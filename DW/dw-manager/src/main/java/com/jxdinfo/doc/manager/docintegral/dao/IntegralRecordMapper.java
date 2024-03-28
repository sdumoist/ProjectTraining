package com.jxdinfo.doc.manager.docintegral.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jxdinfo.doc.manager.docintegral.model.IntegralRecord;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 积分Mapper
 *
 * </p>
 *
 * @author yjs
 * @since 2018-12-3
 */
public interface IntegralRecordMapper extends BaseMapper<IntegralRecord>{

    /**
     * 查看用户今日积分
     * @author: yjs
     * @param userId 用户ID
     * @return Integer 积分
     */
    Integer getIntegralByToday(@Param("userId") String userId);

    /**
     * 查看用户所有积分
     * @author: yjs
     * @param userId 用户ID
     * @return Integer 积分
     */
    Integer getTotalIntegral(@Param("userId") String userId);

    /**
     * 查看用户特定操作类型的积分和
     * @author: zgr
     * @param userId    用户ID
     * @param ruleCodes 操作类型
     * @return  积分和
     */
    Integer getIntegralByType(@Param("userId") String userId, @Param("ruleCodes") String[] ruleCodes);

    /**
     * 增加积分的接口
     * @author: yjs
     *
     */
    void insertRecord(@Param("IntegralRecord") IntegralRecord integralRecord);

    /**
     * 查看积分排行
     * @author: yjs
     * @return List<Map<String,Object>>
     */
    public List<Map<String,Object>> getIntegralRank();
    /**
     * 查看用户今日积分
     * @author: yjs
     * @param userId 用户ID
     * @return Integer 积分
     */
    public Integer getIntegralTimesByToday(@Param("userId") String userId, @Param("ruleCode") String ruleCode);
    /**
     * 查看用户积分明细
     * @author: zgr
     * @param userId 用户ID
     * @param integralState  积分状态（1）
     * @return 集合
     */
    List<Map<String,Object>> getIntegralHistories(@Param("userId") String userId, @Param("integralState") String integralState, @Param("ruleCodes") String[] ruleCodes);

    /**
     * @author luzhanzhao
     * @date 2018-12-07
     * @description 获取积分记录表中的用户总数
     * @return
     */
    int getIntegralUserCount();

    /**
     * @author luzhanzhao
     * @date 2018-12-07
     * @description 按分页的方式获取用户积分排名
     * @param startIndex 起始下标
     * @param pageSize 每页长度
     * @return 排名信息
     */
    public List<Map<String, Object>> getIntegralRankByPage(@Param("startIndex") int startIndex, @Param("pageSize") int pageSize);

    /**
     * @author luzhanzhao
     * @date 2018-12-21
     * @description 当前文档（ids）中属于该用户（userId）的文档数量
     * @param docIds 要下载的文档ids
     * @param userId 要查询的用户id
     * @return 当前文档（ids）中属于该用户（userId）的文档数量
     */
    int selectDocCountByUser(@Param("docIds") String[] docIds, @Param("userId") String userId);

    /**
     * @author luzhanzhao
     * @date 2018-12-24
     * @param docIds 待下载的文档id
     * @param userId 当前登录的用户
     * @return 待下载文档中当前登录下载过的文档数量
     */
    int selectDownloadedCount(@Param("docIds") String[] docIds, @Param("userId") String userId);

    int selectNoCountIntegral(@Param("docIds") String[] docIds, @Param("userId") String userId);

    /**
     * @author luzhanzhao
     * @date 2018-12-24
     * @description 获取当前登录用户的排名
     * @param userId 当前登录用户
     * @return 排名
     */
    Integer getRankNum(@Param("userId") String userId);

    /**
     * @author luzhanzhao
     * @date 2018-12-25
     * @description 判断该用户是否存在积分记录
     * @param userId 当前用户
     * @return 积分记录的条数
     */
    int recordIsNull(@Param("userId") String userId);

    int checkInDoc(String docId);

}
