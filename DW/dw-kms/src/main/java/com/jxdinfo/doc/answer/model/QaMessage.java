package com.jxdinfo.doc.answer.model;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 消息提醒表 实体类
 * @author sjw
 * @since 2021-02-22
 */
@TableName("qa_message")
public class QaMessage extends Model<QaMessage> {

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
     * 用户id
     */
    @TableField("USER_ID")
    private String userId;

    /**
     * 类型，1回答，2评论回复，3邀请回答
     */
    @TableField("TYPE")
    private String type;

    /**
     * 消息内容
     */
    @TableField("CONTENT")
    private String content;

    /**
     * 数据id
     */
    @TableField("DATA_ID")
    private String dataId;

    /**
     * 消息时间
     */
    @TableField("MESSAGE_TIME")
    private Timestamp messageTime;

    /**
     * 状态，0未读，1已读，2删除
     */
    @TableField("STATE")
    private String state;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public Timestamp getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(Timestamp messageTime) {
        this.messageTime = messageTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
