package com.jxdinfo.doc.manager.statistics.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 
 * 类的用途：文件统计查询接口Dao<p>
 * 创建日期：2018年9月26日 <br>
 * 修改历史：<br>
 * 修改日期：2018年9月26日 <br>
 * 修改作者：XuXinYing <br>
 * 修改内容：修改内容 <br>
 * @author XuXinYing
 * @version 1.0
 */
public interface FileStatisticsMapper {

    /**
     * 获取用户上传文档预览量或下载量
     * @Title: getUserPreviewData 
     * @param opType 操作类型
     * @author: XuXinYing
     * @return List
     */
    @SuppressWarnings("rawtypes")
    List<Map> getUserPreviewData(@Param(value = "opType") String opType);

    /**
     * 获取部门上传文档预览量
     * @Title: getUserPreviewData 
     * @param opType 操作类型
     * @author: XuXinYing
     * @return List
     */
    @SuppressWarnings("rawtypes")
    List<Map> getDeptData(@Param(value = "opType") String opType);

    /**
     * 获取部门上传文档预览量
     * @Title: getUserPreviewData 
     * @param opType 操作类型
     * @param page 页面数据
     * @author: XuXinYing
     * @return List
     */
    @SuppressWarnings("rawtypes")
    List getFileListData(Page page, @Param(value = "opType") String opType);

    /**
     * 获取部门上传文档量
     * @Title: getDeptFileUploadData 
     * @author: XuXinYing
     * @return List
     */
    @SuppressWarnings("rawtypes")
    List<Map> getDeptFileUploadData();

    List<Map> getOrigin(String searchTime);

    /**
     * 获取人员上传文档量
     * @Title: getUserFileUploadData 
     * @author: XuXinYing
     * @return List
     */
    @SuppressWarnings("rawtypes")
    List<Map> getUserFileUploadData();

    /**
     *获取文件数量
     * @Title:
     * @author: bjj
     * @return List
     */
    List<String> getFileNumData();
    /**
     * 获取人员上传文档量
     * @Title: getUserFileUploadData
     * @author: XuXinYing
     * @return List
     */
    @SuppressWarnings("rawtypes")
    List<Map> getDeptActive(String searchTime);

    List<Map> getFileListDataAllPerson(Page page, @Param(value = "opType") String opType);

    int getFileListDataAllPersonCount(@Param(value = "opType") String opType);
    List<Map> getFileListDataByAll(@Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize);
    List<Map> getFileListDataByWeek(@Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize);
    List<Map> getFileListDataByMonth(@Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize);
    int  getFileListDataByMonthCount();
    int getFileListDataByWeekCount();
    int getFileListDataByAllCount();

    int getFilesCount();

    int getFilesCountByIdAndFlag(@Param("docId") String docId, @Param("validFlag") String validFlag);

    List getPreviewRankListData(Page page);
    List getDownloadRankListData(Page page);
}
