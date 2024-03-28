package com.jxdinfo.doc.front.docmanager.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;


/**
 * 反馈附件 实体类
 * @author zhongguangrui
 * @since 2018-12-04
 */
@TableName("doc_feedback_attachment")
public class FeedbackAttachment extends Model<FeedbackAttachment> {
    /**
     * 序列化
     */
    private static final long serialVersionUID = 1L;

    /**
     * 反馈附件 主键
     */
    @TableId("attachment_id")
    private String attachmentId;

    /**
     * 反馈的ID  外键
     */
    @TableField("feedback_id")
    private String feedbackId;

    /**
     * 附件类型（目前只有图片：0）
     */
    @TableField("attachment_type")
    private String attachmentType;

    /**
     * 附件地址
     */
    @TableField("attachment_url")
    private String attachmentUrl;


    public String getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(String attachmentId) {
        this.attachmentId = attachmentId;
    }

    public String getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(String feedbackId) {
        this.feedbackId = feedbackId;
    }

    public String getAttachmentType() {
        return attachmentType;
    }

    public void setAttachmentType(String attachmentType) {
        this.attachmentType = attachmentType;
    }

    public String getAttachmentUrl() {
        return attachmentUrl.replace("\\","/");
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    @Override
    protected Serializable pkVal() {
        return this.attachmentId;
    }
}
