package com.jxdinfo.doc.question.model;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 问题补充表 实体类
 * @author sjw
 * @since 2021-03-01
 */
@TableName("qa_question_supplement")
public class QaQuestionSupplement extends Model<QaQuestionSupplement> {

    /**
     * 主键
     */
    @TableId("ID")
    private String id;

    /**
     * 问题ID
     */
    @TableField("QUE_ID")
    private String queId;

    /**
     * 问题补充
     */
    @TableField("SUPPLEMENT")
    private String supplement;

    /**
     * 补充时间
     */
    @TableField("SUP_TIME")
    private Timestamp supTime;

    /**
     * 补充用户ID
     */
    @TableField("SUP_USER_ID")
    private String supUserId;

    /**
     * 补充用户name
     */
    @TableField("SUP_USER_NAME")
    private String supUserName;

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

    public String getSupplement() {
        return supplement;
    }

    public void setSupplement(String supplement) {
        this.supplement = supplement;
    }

    public Timestamp getSupTime() {
        return supTime;
    }

    public void setSupTime(Timestamp supTime) {
        this.supTime = supTime;
    }

    public String getSupUserId() {
        return supUserId;
    }

    public void setSupUserId(String supUserId) {
        this.supUserId = supUserId;
    }

    public String getSupUserName() {
        return supUserName;
    }

    public void setSupUserName(String supUserName) {
        this.supUserName = supUserName;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
