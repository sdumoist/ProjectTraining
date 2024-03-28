package com.jxdinfo.doc.manager.statistics.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

public interface EmpStatisticsService {

    /**
     * 通过ID得到该部门剩余空间
     * @author      yjs
     * @return     double
     * @date        2018/8/28
     */
    double getStatisticsDataByUserId(String organId, String size);

    /**
     * 修改部门的剩余空间
     *
     * @author yjs
     * @date 2018/8/28
     */
    void updateSpace(String id, String space);

    /**
     * 查看登陆人部门空间
     *
     * @author yjs
     * @date 2018/8/31
     */
    Map<String,String> getSpaceByUserId(String id, Integer adminFlag);

    /**
     * 通过orgID得到该部门已用空间
     *
     * @param organId 组织机构ID
     * @author wangning
     * @date 2018/9/13
     */
    double getUsedSpaceByUserId(String organId);
    /**
     * 获取个人空间使用数据
     * @author      yjs
     * @return      List<Map<String, Object>>
     * @date        2018/8/28
     */
    List<Map<String, Object>> getEmpStatisticsData(String groupId, String uerName, int startIndex, int pageSize);
}
