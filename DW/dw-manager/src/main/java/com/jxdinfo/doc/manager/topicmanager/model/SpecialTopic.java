package com.jxdinfo.doc.manager.topicmanager.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 * 专题
 * @author zhangzhen
 * @date 2018/4/9
 */
public class SpecialTopic implements Serializable {
    /**
     * 主键
     */
    private String topicId;

    /**
     * fengmian
     */
    private String topicCover;
    /**
     * 专题名称
     */
    private String topicName;
    /**
     * 展示顺序
     */
    private Integer showOrder;
    /**
     * 专题描述
     */
    private String topicDesc;

    /**
     * 作者id
     */
    private String authorId;
    
    /**
     * 作者名称
     */
    private String authorName;

    /**
     * 创建作者id
     */
    private String createUserId;

    /**
     * 专题关联文档数量
     */
    private Integer docNum;

    /**
     *浏览量
     */

    private int viewNum;
    /**
     *文章列表
     */
    private List DocList;
    private String updateUserId;
    public int getViewNum() {
        return viewNum;
    }

    public void setViewNum(int viewNum) {
        this.viewNum = viewNum;
    }
    public Integer getDocNum() {
        return docNum;
    }

    public void setDocNum(Integer docNum) {
        this.docNum = docNum;
    }

    public List getDocList() {
        return DocList;
    }

    public void setDocList(List docList) {
        DocList = docList;
    }



    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getTopicCover() {
        return topicCover;
    }

    public void setTopicCover(String topicCover) {
        this.topicCover = topicCover;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public String getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId;
    }

    public Timestamp getEnableTime() {
        return enableTime;
    }

    public void setEnableTime(Timestamp enableTime) {
        this.enableTime = enableTime;
    }

    public Timestamp getDisableTime() {
        return disableTime;
    }

    public void setDisableTime(Timestamp disableTime) {
        this.disableTime = disableTime;
    }
    /**
     *文章数
     */
    private int docCount;

    public int getDocCount() {
        return docCount;
    }

    public void setDocCount(int docCount) {
        this.docCount = docCount;
    }

    /**

    /**

     * 创建时间
     */
    private Timestamp createTime;
    /**
     * 专题启用时间
     */
    private Timestamp updateTime;
    /**
     * 专题失效时间
     */
    private Timestamp endTime;
    /**
     * 专题失效时间
     */
    private Timestamp enableTime;
    /**
     * 专题失效时间
     */
    private Timestamp disableTime;

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public Integer getShowOrder() {
        return showOrder;
    }

    public void setShowOrder(Integer showOrder) {
        this.showOrder = showOrder;
    }

    public String getTopicDesc() {
        return topicDesc;
    }

    public void setTopicDesc(String topicDesc) {
        this.topicDesc = topicDesc;
    }


    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

}
