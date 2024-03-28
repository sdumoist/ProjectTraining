package com.jxdinfo.doc.manager.topicmanager.service.impl;

import com.jxdinfo.doc.manager.docmanager.model.DocResourceLog;
import com.jxdinfo.doc.manager.topicmanager.dao.TopicDocMapper;
import com.jxdinfo.doc.manager.topicmanager.model.SpecialTopic;
import com.jxdinfo.doc.manager.topicmanager.model.TopicFile;
import com.jxdinfo.doc.manager.topicmanager.service.ITopicDocManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author xb
 * @Description:
 * @Date: 2018/7/4 14:25
 */
@Service
public class TopicDocManagerServiceImpl implements ITopicDocManagerService {

    @Autowired
    private TopicDocMapper topicDocMapper;

    @Override
    public List searchTopic() {
        return topicDocMapper.searchTopic();
    }

    /**
     * 根据文件夹查询文档
     *
     * @param list
     * @return
     */
    @Override
    public List getDocByFsFile(List<String> list) {
        return topicDocMapper.getDocByFsFile(list);
    }

    /**
     * 保存文档主题
     *
     * @param topicId
     * @param list
     * @return
     */
    @Override
    public int saveTopicDoc(String topicId, List<TopicFile> list) {
        return topicDocMapper.saveTopicDoc( topicId,list);
    }

    /**
     * 根据主题id查询专题详情
     * @param topicId
     * @return
     */
    @Override
    public SpecialTopic getTopicDetail(String topicId) {
         return topicDocMapper.getTopicDetail(topicId);
    }

    /**
     * 根据主题id查询所有文档
     *
     * @param topicId
     * @return
     */
    @Override
    public List getTopicDoclist(String topicId,int startNum,int size) {
        return topicDocMapper.getTopicDoclist(topicId,startNum,size);
    }

    /**
     * 查询所有的专题
     *
     * @return
     */
    @Override
    public List<SpecialTopic> getTopicList() {
        return topicDocMapper.getTopicList();
    }

    /**
     * 校验该专题下是否已经存在此文档
     *
     * @param docId
     * @param topicId
     * @return
     */
    @Override
    public int checkIsExist(String docId, String topicId) {
        return topicDocMapper.checkIsExist(docId,topicId);
    }

    /**
     * 根据文档id删除文档与专题之间的关系
     *
     * @param list
     * @return
     */
    @Override
    public int delTopicFile(List<String> list) {
        return topicDocMapper.delTopicFile(list);
    }

    /**
     * 查询文章的数量
     *
     * @param
     * @return
     */
    public int getDocCountTopicId(String topicId) {
        return topicDocMapper.getDocCountTopicId(topicId);
    }

    @Override
    public void insertResourceLog(List<DocResourceLog> docdownloadList) {
        topicDocMapper.insertResourceLog(docdownloadList);
    }

    /**
     * 校验该专题下是否已经存在同名文档
     *
     * @param docId
     * @param topicId
     * @return
     */
    @Override
    public int checkIsSameNameExist(String docId, String topicId) {
        return topicDocMapper.checkIsSameNameExist(docId,topicId);
    }
    /**
     * 校验该专题下是否已经存在同名文档
     *
     * @param docId
     * @param topicId
     * @return
     */
    @Override
    public int checkIsSameFolderNameExist(String docId, String topicId) {
        return topicDocMapper.checkIsSameFolderNameExist(docId,topicId);
    }
}
