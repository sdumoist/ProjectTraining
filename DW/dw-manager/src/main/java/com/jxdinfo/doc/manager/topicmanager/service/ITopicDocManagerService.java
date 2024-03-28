package com.jxdinfo.doc.manager.topicmanager.service;

import com.jxdinfo.doc.manager.docmanager.model.DocResourceLog;
import com.jxdinfo.doc.manager.topicmanager.model.TopicFile;
import com.jxdinfo.doc.manager.topicmanager.model.SpecialTopic;

import java.util.List;

/**
 * @author xb
 * @Description:
 * @Date: 2018/7/4 14:24
 */
public interface ITopicDocManagerService {
    /**
     * 查询主题下拉框
     * @return
     */
    List searchTopic();

    /**
     * 根据文件夹查询文档
     * @param list
     * @return
     */
    List getDocByFsFile(List<String> list);
    /**
     * 保存文档主题
     * @param topicId
     * @param list
     * @return
     */
    int saveTopicDoc(String topicId, List<TopicFile> list);

    /**
     * 根据主题id查询专题详情
     * @param topicId
     * @return
     */
    SpecialTopic getTopicDetail(String topicId);

    /**
     * 根据主题id查询所有文档
     * @param topicId
     * @return
     */
    List getTopicDoclist(String topicId, int startNum, int size);

    /**
     * 查询所有的专题
     * @return
     */
    List<SpecialTopic> getTopicList();

    /**
     * 校验该专题下是否已经存在此文档
     * @return
     */
    int checkIsExist(String docId, String topicId);
    /**
     * 校验该专题下是否已经存在同名文档
     * @return
     */
    int checkIsSameFolderNameExist(String docId, String topicId);
    /**
     * 校验该专题下是否已经存在同名文档
     * @return
     */
    int checkIsSameNameExist(String docId, String topicId);
    /**
     * 根据文档id删除文档与专题之间的关系
     * @return
     */
    int delTopicFile(List<String> list);

    public int getDocCountTopicId(String topicId);

    void insertResourceLog(List<DocResourceLog> docdownloadList);

}
