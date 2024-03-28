package com.jxdinfo.doc.manager.statistics.service;

import java.util.List;
import java.util.Map;
/**
 * 部门文件大小
 * 作者：yjs ;
 * 修改内容：
 * @author yjs ;
 * @version 1.0
 */
public interface DeptStatisticsService {
    /**
     * 获取列表页数据
     * @author      yjs
     * @return      List<Map<String, Object>>
     * @date        2018/8/28
     */
    List<Map<String, Object>> getStatisticsData();

    /**
     * 通过ID得到该部门剩余空间
     * @author      yjs
     * @return     double
     * @date        2018/8/28
     */
    double getStatisticsDataByOrganId(String organId, String size);

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
    Map<String,String> getSpaceByOrganId(String id, Integer adminFlag);
    
    /**
     * 通过orgID得到该部门已用空间
     *
     * @param organId 组织机构ID
     * @author wangning
     * @date 2018/9/13
     */
    double getUsedSpaceByOrganId(String organId);
}
