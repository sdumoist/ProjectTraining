package com.jxdinfo.doc.manager.resourceLog.service;

import com.jxdinfo.doc.manager.resourceLog.model.ResourceLog;

import java.util.List;
import java.util.Map;

public interface docResourceLogService {
    List<ResourceLog> ResourceLogList(String resourceName, int startIndex, int pageSize);

    List<ResourceLog> ClientResourceLogList(String resourceId, String startTime);

    int ResourceLogListCount(String resourceNameStr);

    /**
     * 查询本月的操作记录
     * @return Map
     */
    List<Map<String, Object>> getThisMonthCount();
}
