package com.jxdinfo.doc.question.model;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

/**
 * 点赞实体类
 */
@TableName("qa_agree")
public class QaAgree extends Model<QaAgree> {
    /**
     * 主键
     */
    @TableId("AGREE_ID")
    private String agreeId;

    /**
     * 问题id
     */
    @TableField("QUE_ID")
    private String queId;

    /**
     * 回答ID
     */
    @TableField("ANS_ID")
    private String ansId;

    /**
     * 用户id
     */
    @TableField("USER_ID")
    private String userId;

    public String getAgreeId() {
        return agreeId;
    }

    public void setAgreeId(String agreeId) {
        this.agreeId = agreeId;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    protected Serializable pkVal() {
        return this.agreeId;
    }
}
