package com.jxdinfo.doc.manager.resourceLog.service.impl;

import com.jxdinfo.doc.manager.resourceLog.dao.docResourceLogMapper;
import com.jxdinfo.doc.manager.resourceLog.model.ResourceLog;
import com.jxdinfo.doc.manager.resourceLog.service.docResourceLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: docResourceLogServiceImpl
 * @Description:TODO
 * @Author: lishilin
 * @Date: 2019/11/13
 * @Version: 1.0
 */

@Service
public class docResourceLogServiceImpl implements docResourceLogService {

    @Autowired
    private docResourceLogMapper docResourceLogMapper;

    @Override
    public List<ResourceLog> ResourceLogList(String resourceName, int startIndex, int pageSize) {
        return docResourceLogMapper.ResourceLogList(resourceName,startIndex,pageSize);
    }

    @Override
    public List<ResourceLog> ClientResourceLogList(String resourceId, String startTime) {
        return docResourceLogMapper.ClientResourceLogList(resourceId,startTime);
    }

    @Override
    public int ResourceLogListCount(String resourceName) {
        return docResourceLogMapper.ResourceLogListCount(resourceName);
    }

    /**
     * 查询本月的操作记录
     * @return Map
     */
    @Override
    public List<Map<String, Object>> getThisMonthCount() {
        return docResourceLogMapper.getThisMonthCount();
    }
}
