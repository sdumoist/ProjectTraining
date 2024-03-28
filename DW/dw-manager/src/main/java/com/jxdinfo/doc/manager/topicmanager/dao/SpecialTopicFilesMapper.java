package com.jxdinfo.doc.manager.topicmanager.dao;

import com.jxdinfo.doc.manager.topicmanager.model.SpecialTopicFiles;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 专题文档
 * @author zhangzhen
 * @date 2018/4/11
 */
public interface SpecialTopicFilesMapper {
    /**
     * 查询专题及专题下的文档
     * @return 查询专题及专题下的文档
     * @date 2018-4-11
     */
    List getSpecialTopicFiles();
    /**
     * @title: 搜索专题文件详情
     * @description: 搜索专题文件详情
     * @date: 2018-4-12.
     * @author: rxy
     * @param: fileType  page  keyWords categoryCode
     * @return:
     */
    List searchTopicFilesDetail(@Param("array") String[] array, @Param("page") int page, @Param("keyWords") String keyWords, @Param("topicId") String topicId, @Param("fileType") String fileType);
    /**
     * @title: 统计专题文件详情数量
     * @description: 统计专题文件详情数量
     * @date: 2018-4-13
     * @author: rxy
     * @param: fileType  page  keyWords topicId
     * @return:
     */
    public int countTopicFilesDetail(@Param("fileTypeArry") String[] fileTypeArry, @Param("page") int page, @Param("keyWords") String keyWords, @Param("topicId") String topicId, @Param("fileType") String fileType);
    /**
     * @title: 计算专题文档数量
     * @description: 计算专题文档数量
     * @date: 2018-4-13.
     * @author: rxy
     * @param: id
     * @return:
     */
    public int countTopicFiles(String id);
    /**
     * @title: 计算预览专题文档次数
     * @description: 计算预览专题文档次数
     * @date: 2018-4-13.
     * @author: rxy
     * @param: id
     * @return:
     */
    public int countPreviewTopicFiles(String id);
    /**
     * 批量新增专题文档
     * @param list 专题文档对象
     * @return 新增数量
     */
    int addSpecialTopicFiles(List<SpecialTopicFiles> list);
    /**
     * 批量更新专题文档
     * @param list 专题文档集合
     * @return 更新的数量
     */
    int editSpecialTopicFiles(@Param("list") List<SpecialTopicFiles> list);
    /**
     * 删除专题下的文档
     * @param list 文档ID集合
     * @param topicId 专题ID
     * @return 删除的数量
     */
    int delDocById(@Param("list") List<String> list, @Param("topicId") String topicId);
    /**
     * 批量删除专题下的文档
     * @param list 专题ID集合
     * @return 删除的数量
     */
    int delDoc(@Param("list") List<String> list);

    /**
     * 根据文档id查询所在专题
     * @param docId
     * @return
     * @author zgr
     */
    List<SpecialTopicFiles> selectTopicsByDocId(@Param("docId") String docId);
}
