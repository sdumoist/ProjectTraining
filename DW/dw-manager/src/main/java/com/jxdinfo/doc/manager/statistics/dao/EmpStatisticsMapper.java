package com.jxdinfo.doc.manager.statistics.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: EmpStatisticsMapper
 * @Description:TODO
 * @Author: lishilin
 * @Date: 2019/12/4
 * @Version: 1.0
 */
public interface EmpStatisticsMapper {

    /**
     * 通过ID得到个人剩余空间
     *
     * @return double
     * @author lsl
     * @date 2018/8/29
     */
    double getStatisticsDataByUserId(@Param("userId") String userId);

    /**
     * 获取列表页数据
     *
     * @return List<Map<String, Object>>
     * @author lsl
     * @date 2018/8/29
     */
    List<Map<String, Object>> getEmpStatisticsData(@Param("groupId") String groupId, @Param("uerName") String uerName, @Param("startIndex") int startIndex, @Param("pageSize") int pageSize);

    int getUserListCount(@Param("groupId") String groupId, @Param("uerName") String uerName);
}
