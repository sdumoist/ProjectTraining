package com.jxdinfo.doc.manager.topicmanager.dao;

import com.jxdinfo.doc.manager.docmanager.model.DocResourceLog;
import com.jxdinfo.doc.manager.topicmanager.model.SpecialTopic;
import com.jxdinfo.doc.manager.topicmanager.model.TopicFile;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author xb
 * @Description:
 * @Date: 2018/7/4 14:26
 */
public interface TopicDocMapper {
    List searchTopic();

    List<Map> getDocByFsFile(@Param("list") List<String> list);

    int saveTopicDoc(@Param("topicId") String topicId, @Param("list") List<TopicFile> list);

    SpecialTopic getTopicDetail(@Param("topicId") String topicId);

    List getTopicDoclist(@Param("topicId") String topicId, @Param("startNum") int startNum, @Param("size") int size);

    List<SpecialTopic> getTopicList();

    int checkIsExist(@Param("docId") String docId, @Param("topicId") String topicId);

    int delTopicFile(@Param("list") List<String> list);

    int  getDocCountTopicId(@Param("topicId") String topicId);

    /**
     * 校验该专题下是否已经存在同名文档
     * @return
     */
    int checkIsSameNameExist(@Param("docId") String docId, @Param("topicId") String topicId);

    int checkIsSameFolderNameExist(@Param("docId") String docId, @Param("topicId") String topicId);

    /**
     * 文档记录日志
     */
    public void insertResourceLog(List<DocResourceLog> docdownloadList);
}
