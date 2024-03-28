package com.jxdinfo.doc.manager.topicmanager.model;

import java.sql.Timestamp;

/**
 * @author xb
 * @Description:
 * @Date: 2018/7/4 21:01
 */
public class TopicFile {
    private String topicFileId;
    private String docId;
    private String specialTopicId;
    private Integer showOrder;
    private Timestamp createTime;


    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getSpecialTopicId() {
        return specialTopicId;
    }

    public void setSpecialTopicId(String specialTopicId) {
        this.specialTopicId = specialTopicId;
    }

    public Integer getShowOrder() {
        return showOrder;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public void setShowOrder(Integer showOrder) {
        this.showOrder = showOrder;
    }

	public String getTopicFileId() {
		return topicFileId;
	}

	public void setTopicFileId(String topicFileId) {
		this.topicFileId = topicFileId;
	}
}
