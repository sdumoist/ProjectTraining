package com.jxdinfo.doc.question.model;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 问题回答表 实体类
 * @author sjw
 * @since 2021-02-24
 */
@TableName("qa_question")
public class QaQuestion extends Model<QaQuestion> {

    /**
     * 序列化
     */
    private static final long serialVersionUID = 1L;

    /**
     * 问题id
     */
    @TableId("QUE_ID")
    private String queId;

    /**
     * 问题标题
     */
    @TableField("TITLE")
    private String title;

    /**
     * 问题补充
     */
    @TableField("SUPPLEMENT")
    private String supplement;

    /**
     * 状态，0待回答，1已解决，2已删除
     * 状态，0待回答，1已解决，2已结束，3已删除 --最新
     */
    @TableField("STATE")
    private String state;

    /**
     * 悬赏积分值
     */
    @TableField("REWARD_POINITS")
    private int rewardPoinits;

    /**
     * 标签
     */
    @TableField("LABEL")
    private String label;

    /**
     * 可回复者，1全体人员，2指定专家
     */
    @TableField("ANSWER_FLAG")
    private String answerFlag;

    /**
     * 提问者ID
     */
    @TableField("QUE_USER_ID")
    private String queUserId;

    /**
     * 提问者姓名
     */
    @TableField("QUE_USER_NAME")
    private String queUserName;

    /**
     * 提问时间
     */
    @TableField("QUE_TIME")
    private Timestamp queTime;

    /**
     * 阅读数
     */
    @TableField("READ_NUM")
    private int readNum;

    /**
     * 阅读数
     */
    @TableField("MAJOR_ID")
    private String majorId;

    /**
     * 阅读数
     */
    @TableField("MAJOR_NAME")
    private String majorName;

    public String getQueId() {
        return queId;
    }

    public void setQueId(String queId) {
        this.queId = queId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSupplement() {
        return supplement;
    }

    public void setSupplement(String supplement) {
        this.supplement = supplement;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getRewardPoinits() {
        return rewardPoinits;
    }

    public void setRewardPoinits(int rewardPoinits) {
        this.rewardPoinits = rewardPoinits;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getAnswerFlag() {
        return answerFlag;
    }

    public void setAnswerFlag(String answerFlag) {
        this.answerFlag = answerFlag;
    }

    public String getQueUserId() {
        return queUserId;
    }

    public void setQueUserId(String queUserId) {
        this.queUserId = queUserId;
    }

    public String getQueUserName() {
        return queUserName;
    }

    public void setQueUserName(String queUserName) {
        this.queUserName = queUserName;
    }

    public Timestamp getQueTime() {
        return queTime;
    }

    public void setQueTime(Timestamp queTime) {
        this.queTime = queTime;
    }

    public int getReadNum() {
        return readNum;
    }

    public void setReadNum(int readNum) {
        this.readNum = readNum;
    }

    public String getMajorId() {
        return majorId;
    }

    public void setMajorId(String majorId) {
        this.majorId = majorId;
    }

    public String getMajorName() {
        return majorName;
    }

    public void setMajorName(String majorName) {
        this.majorName = majorName;
    }

    @Override
    protected Serializable pkVal() {
        return this.queId;
    }

}
