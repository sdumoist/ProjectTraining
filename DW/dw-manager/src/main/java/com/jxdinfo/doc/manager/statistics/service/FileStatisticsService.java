package com.jxdinfo.doc.manager.statistics.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * 
 * 类的用途：文件统计接口<p>
 * 创建日期：2018年9月26日 <br>
 * 修改历史：<br>
 * 修改日期：2018年9月26日 <br>
 * 修改作者：XuXinYing <br>
 * 修改内容：修改内容 <br>
 * @author XuXinYing
 * @version 1.0
 */
public interface FileStatisticsService {

    /**
     * 获取用户上传文档预览量
     * @Title: getUserPreviewData 
     * @param opType 操作类型
     * @author: XuXinYing
     * @return Map
     */
    Map<String, Object> getUserPreviewData(String opType);

    /**
     * 获取部门上传文档下载量
     * @Title: getDeptData 
     * @param opType 操作类型
     * @author: XuXinYing
     * @return Map
     */
    Map<String,Object> getDeptData(String opType);

    /**
     * 查询文件列表包含下载预览
     * @Title: getFileListData 
     * @author: XuXinYing
     * @param page 页面属性
     * @param opType 操作类型
     * @return List
     */
    @SuppressWarnings("rawtypes")
    List getFileListData(Page page, @Param("opType") String opType);

    /**
     * 获取上传文档数量
     * @Title: getUploadData 
     * @param type 操作类型
     * @author: XuXinYing
     * @return Map
     */
    List<Map> getUploadData(String type);
    /**
     * 查询文件数量
     * @Title:
     * @author: bjj
     * @param
     * @param
     * @return List
     */
    List<String> getFileNums();

    /**
     * 活跃度分析
     * @Title: getDeptActive
     * @author: bjj
     * @return List
     */
    List<Map> getDeptActive(String searchTime);


    List<Map> getFileListDataAllPerson(Page page, String opType);
    int  getFileListDataAllPersonCount(String opType);

    int getFilesCount();

    int getFilesCountByIdAndFlag(String id, String flag);

    List<Map> getOrigin(String searchTime);

    /**
     * 查询文件预览排行
     * @param page
     * @return
     */
    List getPreviewRankListData(Page page);

    /**
     * 查询文件下载排行
     * @param page
     * @return
     */
    List getDownloadRankListData(Page page);
}
