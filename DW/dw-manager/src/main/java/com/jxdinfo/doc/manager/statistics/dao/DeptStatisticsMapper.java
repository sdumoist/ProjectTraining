package com.jxdinfo.doc.manager.statistics.dao;

import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;


/**
 * 部门文件大小
 * 作者：yjs ;
 * 修改内容：
 *
 * @author yjs ;
 * @version 1.0
 */
public interface DeptStatisticsMapper {
    /**
     * 获取列表页数据
     *
     * @return List<Map<String, Object>>
     * @author yjs
     * @date 2018/8/29
     */
    List<Map<String, Object>> getStatisticsData();

    /**
     * 通过ID得到该部门剩余空间
     *
     * @return double
     * @author yjs
     * @date 2018/8/29
     */
  double getStatisticsDataByOrganId(@Param("organId") String organId);
}
