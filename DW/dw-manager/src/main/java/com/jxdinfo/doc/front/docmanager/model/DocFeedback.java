package com.jxdinfo.doc.front.docmanager.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;


/**
 * 反馈表 实体类
 * @author zhongguangrui
 * @since 2018-12-03
 */
@TableName("doc_feedback")
public class DocFeedback extends Model<DocFeedback> {

    /**
     * 序列化
     */
    private static final long serialVersionUID = 1L;

    /**
     * 反馈表 主键
     */
    @TableId("feedback_id")
    private String feedbackId;

    /**
     * 反馈类型（1：问题；2：改进）
     */
    @TableField("feedback_type")
    private String feedbackType;

    /**
     * 反馈描述
     */
    @TableField("feedback_describe")
    private String feedbackDescribe;

    /**
     * 反馈人ID
     */
    @TableField("feedback_user_id")
    private String feedbackUserId;

    /**
     * 反馈人
     */
    @TableField("feedback_user")
    private String feedbackUser;

    /**
     * 联系方式
     */
    @TableField("contack_way")
    private String contackWay;

    /**
     * 反馈时间
     */
    @TableField("feedback_time")
    private Timestamp feedbackTime;

    /**
     * 处理状态
     */
    @TableField("feedback_state")
    private String feedbackState;

    /**
     * 处理人ID
     */
    @TableField("deal_user_id")
    private String dealUserId;

    /**
     * 处理人
     */
    @TableField("deal_user")
    private String dealUser;

    /**
     * 处理时间
     */
    @TableField("deal_time")
    private Timestamp dealTime;

    /**
     * 处理描述
     */
    @TableField("deal_describe")
    private String dealDescribe;

    /**
     * 附件列表
     */
    @TableField(exist = false)
    private List<FeedbackAttachment> feedbackAttachments;

    public DocFeedback() {
    }

    public DocFeedback(String feedbackType, String contackWay, String feedbackDescribe) {
        this.feedbackType = feedbackType;
        this.contackWay = contackWay;
        this.feedbackDescribe = feedbackDescribe;
    }

    public List<FeedbackAttachment> getFeedbackAttachments() {
        return feedbackAttachments;
    }

    public void setFeedbackAttachments(List<FeedbackAttachment> feedbackAttachments) {
        this.feedbackAttachments = feedbackAttachments;
    }

    public String getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(String feedbackId) {
        this.feedbackId = feedbackId;
    }

    public String getFeedbackType() {
        return feedbackType;
    }

    public void setFeedbackType(String feedbackType) {
        this.feedbackType = feedbackType;
    }

    public String getFeedbackDescribe() {
        return feedbackDescribe;
    }

    public void setFeedbackDescribe(String feedbackDescribe) {
        this.feedbackDescribe = feedbackDescribe;
    }

    public String getFeedbackUserId() {
        return feedbackUserId;
    }

    public void setFeedbackUserId(String feedbackUserId) {
        this.feedbackUserId = feedbackUserId;
    }

    public String getFeedbackUser() {
        return feedbackUser;
    }

    public void setFeedbackUser(String feedbackUser) {
        this.feedbackUser = feedbackUser;
    }

    public String getContackWay() {
        return contackWay;
    }

    public void setContackWay(String contackWay) {
        this.contackWay = contackWay;
    }

    public Timestamp getFeedbackTime() {
        return feedbackTime;
    }

    public void setFeedbackTime(Timestamp feedbackTime) {
        this.feedbackTime = feedbackTime;
    }

    public String getFeedbackState() {
        return feedbackState;
    }

    public void setFeedbackState(String feedbackState) {
        this.feedbackState = feedbackState;
    }

    public String getDealUserId() {
        return dealUserId;
    }

    public void setDealUserId(String dealUserId) {
        this.dealUserId = dealUserId;
    }

    public String getDealUser() {
        return dealUser;
    }

    public void setDealUser(String dealUser) {
        this.dealUser = dealUser;
    }

    public Timestamp getDealTime() {
        return dealTime;
    }

    public void setDealTime(Timestamp dealTime) {
        this.dealTime = dealTime;
    }

    public String getDealDescribe() {
        return dealDescribe;
    }

    public void setDealDescribe(String dealDescribe) {
        this.dealDescribe = dealDescribe;
    }

    @Override
    protected Serializable pkVal() {
        return this.feedbackId;
    }
}
