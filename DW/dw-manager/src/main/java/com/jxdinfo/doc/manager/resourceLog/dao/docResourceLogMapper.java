package com.jxdinfo.doc.manager.resourceLog.dao;

import com.jxdinfo.doc.manager.resourceLog.model.ResourceLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface docResourceLogMapper {
    /**
     * resourceLog列表查询
     * @param resourceName resourceLog名称
     * @param startIndex 开始位置
     * @param pageSize  每页数据条数
     * @return list
     */
    List<ResourceLog> ResourceLogList(@Param("resourceName") String resourceName, @Param("startIndex") int startIndex, @Param("pageSize") int pageSize);

    /**
     * resourceLog列表查询
     * @param resourceId 文件id
     * @param time 时间
     * @return
     */
    List<ResourceLog> ClientResourceLogList(@Param("resourceId") String resourceId, @Param("time") String time);

    /**
     * resourceLog列表查询
     * @param resourceName resourceLog名称
     * @return int
     */
    int ResourceLogListCount(@Param("resourceName") String resourceName);

    /**
     * 查询本月的操作记录
     * @return Map
     */
    List<Map<String, Object>> getThisMonthCount();
}
