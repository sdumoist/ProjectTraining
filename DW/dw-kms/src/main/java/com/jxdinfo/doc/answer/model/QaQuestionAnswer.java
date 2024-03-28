package com.jxdinfo.doc.answer.model;


import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 问题回答表 实体类
 * @author sjw
 * @since 2021-02-22
 */
@TableName("qa_question_answer")
public class QaQuestionAnswer extends Model<QaQuestionAnswer> {

    /**
     * 序列化
     */
    private static final long serialVersionUID = 1L;

    /**
     * 回答ID
     */
    @TableId("ANS_ID")
    private String ansId;

    /**
     * 问题id
     */
    @TableField("QUE_ID")
    private String queId;

    /**
     * 回答内容
     */
    @TableField("ANS_CONTENT")
    private String ansContent;

    /**
     * 回答内容
     */
    @TableField("ANS_TIME")
    private Timestamp ansTime;

    /**
     * 回答人ID
     */
    @TableField("ANS_USER_ID")
    private String ansUserId;

    /**
     * 回答人姓名
     */
    @TableField("ANS_USER_NAME")
    private String ansUserName;

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
     * 最佳答案，0否，1是
     */
    @TableField("BEST_ANSWER")
    private String bestAnswer;

    /**
     * 非表字段 状态， 是不是提交
     */
    @TableField(exist = false)
    private String ansContentText;


    public String getAnsId() {
        return ansId;
    }

    public void setAnsId(String ansId) {
        this.ansId = ansId;
    }

    public String getQueId() {
        return queId;
    }

    public void setQueId(String queId) {
        this.queId = queId;
    }

    public String getAnsContent() {
        return ansContent;
    }

    public void setAnsContent(String ansContent) {
        this.ansContent = ansContent;
    }

    public Timestamp getAnsTime() {
        return ansTime;
    }

    public void setAnsTime(Timestamp ansTime) {
        this.ansTime = ansTime;
    }

    public String getAnsUserId() {
        return ansUserId;
    }

    public void setAnsUserId(String ansUserId) {
        this.ansUserId = ansUserId;
    }

    public String getAnsUserName() {
        return ansUserName;
    }

    public void setAnsUserName(String ansUserName) {
        this.ansUserName = ansUserName;
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

    public String getBestAnswer() {
        return bestAnswer;
    }

    public void setBestAnswer(String bestAnswer) {
        this.bestAnswer = bestAnswer;
    }

    public String getAnsContentText() {
        return ansContentText;
    }

    public void setAnsContentText(String ansContentText) {
        this.ansContentText = ansContentText;
    }

    @Override
    protected Serializable pkVal() {
        return this.ansId;
    }
}
