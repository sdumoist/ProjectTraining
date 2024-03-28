package com.jxdinfo.doc.manager.topicmanager.model;

/**
 * 文档专题关系
 * @author zhangzhen
 * @date 2018/4/9
 */
public class SpecialTopicFiles {
    /**
     * 专题ID
     */
    private String topicId;
    /**
     * 文档ID
     */
    private String docId;
    /**
     * 主键
     */
    private String topicFileId;

    /**
     * 展示顺序
     */
    private Integer showOrder;

    public String getTopicFileId() {
        return topicFileId;
    }

    public void setTopicFileId(String topicFileId) {
        this.topicFileId = topicFileId;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public Integer getShowOrder() {
        return showOrder;
    }

    public void setShowOrder(Integer showOrder) {
        this.showOrder = showOrder;
    }
}