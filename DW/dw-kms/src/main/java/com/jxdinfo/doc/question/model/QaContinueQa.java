package com.jxdinfo.doc.question.model;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 追问追答表 实体类
 * @author sjw
 * @since 2021-02-24
 */
@TableName("qa_continue_q_a")
public class QaContinueQa extends Model<QaContinueQa> {

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
     * 回答ID
     */
    @TableField("ANS_ID")
    private String ansId;

    /**
     * 类型，1追问，2追答
     */
    @TableField("TYPE")
    private String type;

    /**
     * 被回复信息ID
     */
    @TableField("BY_REPLY_ID")
    private String byReplyId;

    /**
     * 内容
     */
    @TableField("CONTENT")
    private String content;

    /**
     * 时间
     */
    @TableField("TIME")
    private Timestamp time;

    /**
     * 追问追答人ID
     */
    @TableField("USER_ID")
    private String userId;

    /**
     * 追问追答人姓名
     */
    @TableField("USER_NAME")
    private String userName;

    /**
     * 状态，0正常，1删除
     */
    @TableField("STATE")
    private String state;

    /**
     * 排序
     */
    @TableField("SHOW_ORDER")
    private int showOrder;

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

    public String getAnsId() {
        return ansId;
    }

    public void setAnsId(String ansId) {
        this.ansId = ansId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getByReplyId() {
        return byReplyId;
    }

    public void setByReplyId(String byReplyId) {
        this.byReplyId = byReplyId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getShowOrder() {
        return showOrder;
    }

    public void setShowOrder(int showOrder) {
        this.showOrder = showOrder;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
