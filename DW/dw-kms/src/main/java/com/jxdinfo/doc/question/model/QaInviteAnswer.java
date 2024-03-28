package com.jxdinfo.doc.question.model;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.sql.Timestamp;


/**
 * 问题邀我回答表
 */
@TableName("qa_invite_answer")
public class QaInviteAnswer extends Model<QaInviteAnswer> {

    /**
     * 序列化
     */
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId("ID")
    private String id;

    /**
     * 问题id
     */
    @TableField("QUE_ID")
    private String queId;

    /**
     * 邀请人ID
     */
    @TableField("INVITE_USER_ID")
    private String inviteUserId;

    /**
     * 邀请人姓名
     */
    @TableField("INVITE_USER_NAME")
    private String inviteUserName;

    /**
     * 邀请时间
     */
    @TableField("INVITE_TIME")
    private Timestamp inviteTime;

    /**
     * 被邀请人ID
     */
    @TableField("BY_INVITE_USER_ID")
    private String ByInviteUserId;

    /**
     * 被邀请人name
     */
    @TableField("BY_INVITE_USER_NAME")
    private String ByInviteUserName;

    /**
     * 是否回答
     */
    @TableField("ANSWER_FLAG")
    private String answerFlag;

    /**
     * 是否及时 0 新增占位 1 不及时 2 及时
     */
    @TableField("TIMELY_FLAG")
    private String timelyFlag;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQueId() {
        return queId;
    }

    public void setQueId(String queId) {
        this.queId = queId;
    }

    public String getInviteUserId() {
        return inviteUserId;
    }

    public void setInviteUserId(String inviteUserId) {
        this.inviteUserId = inviteUserId;
    }

    public String getInviteUserName() {
        return inviteUserName;
    }

    public void setInviteUserName(String inviteUserName) {
        this.inviteUserName = inviteUserName;
    }

    public Timestamp getInviteTime() {
        return inviteTime;
    }

    public void setInviteTime(Timestamp inviteTime) {
        this.inviteTime = inviteTime;
    }

    public String getByInviteUserId() {
        return ByInviteUserId;
    }

    public void setByInviteUserId(String byInviteUserId) {
        ByInviteUserId = byInviteUserId;
    }

    public String getByInviteUserName() {
        return ByInviteUserName;
    }

    public void setByInviteUserName(String byInviteUserName) {
        ByInviteUserName = byInviteUserName;
    }

    public String getAnswerFlag() {
        return answerFlag;
    }

    public void setAnswerFlag(String answerFlag) {
        this.answerFlag = answerFlag;
    }

    public String getTimelyFlag() {
        return timelyFlag;
    }

    public void setTimelyFlag(String timelyFlag) {
        this.timelyFlag = timelyFlag;
    }

    @Override
    protected Serializable pkVal() {
        return id;
    }
}
