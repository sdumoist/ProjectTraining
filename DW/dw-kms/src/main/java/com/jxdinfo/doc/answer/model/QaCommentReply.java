package com.jxdinfo.doc.answer.model;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 评论回复表 实体类
 * @author sjw
 * @since 2021-02-22
 */
@TableName("qa_comment_reply")
public class QaCommentReply extends Model<QaCommentReply> {

    /**
     * 序列化
     */
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId("COMMENT_REPLY_ID")
    private String commentReplyId;

    /**
     * 问题ID
     */
    @TableField("QUE_ID")
    private String queId;

    /**
     * 回答ID
     */
    @TableField("ANS_ID")
    private String ansId;

    /**
     * 被回复信息ID
     */
    @TableField("BY_REPLY_ID")
    private String byReplyId;

    /**
     * 被回复人ID
     */
    @TableField("BY_REPLY_USER_ID")
    private String byReplyUserId;

    /**
     * 被回复人姓名
     */
    @TableField("BY_REPLY_USER_NAME")
    private String byReplyUserName;

    /**
     * 回复内容
     */
    @TableField("REPLY_CONTENT")
    private String replyContent;

    /**
     * 回复时间
     */
    @TableField("REPLY_TIME")
    private Timestamp replyTime;

    /**
     * 回复人ID
     */
    @TableField("REPLY_USER_ID")
    private String replyUserId;

    /**
     * 回复人姓名
     */
    @TableField("REPLY_USER_NAME")
    private String replyUserName;

    /**
     * 状态，0正常，1删除
     */
    @TableField("STATE")
    private String state;

    /**
     * 赞成数
     */
    @TableField("AGREE_NUM")
    private int agreeNum;

    /**
     * 评论ID
     */
    @TableField("COMMENT_ID")
    private String commentId;

    public String getCommentReplyId() {
        return commentReplyId;
    }

    public void setCommentReplyId(String commentReplyId) {
        this.commentReplyId = commentReplyId;
    }

    public String getQueId() {
        return queId;
    }

    public void setQueId(String queId) {
        this.queId = queId;
    }

    public String getAnsId() {
        return ansId;
    }

    public void setAnsId(String ansId) {
        this.ansId = ansId;
    }

    public String getByReplyId() {
        return byReplyId;
    }

    public void setByReplyId(String byReplyId) {
        this.byReplyId = byReplyId;
    }

    public String getByReplyUserId() {
        return byReplyUserId;
    }

    public void setByReplyUserId(String byReplyUserId) {
        this.byReplyUserId = byReplyUserId;
    }

    public String getByReplyUserName() {
        return byReplyUserName;
    }

    public void setByReplyUserName(String byReplyUserName) {
        this.byReplyUserName = byReplyUserName;
    }

    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }

    public Timestamp getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(Timestamp replyTime) {
        this.replyTime = replyTime;
    }

    public String getReplyUserId() {
        return replyUserId;
    }

    public void setReplyUserId(String replyUserId) {
        this.replyUserId = replyUserId;
    }

    public String getReplyUserName() {
        return replyUserName;
    }

    public void setReplyUserName(String replyUserName) {
        this.replyUserName = replyUserName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getAgreeNum() {
        return agreeNum;
    }

    public void setAgreeNum(int agreeNum) {
        this.agreeNum = agreeNum;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    @Override
    protected Serializable pkVal() {
        return this.commentReplyId;
    }
}
