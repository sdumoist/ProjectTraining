package com.jxdinfo.doc.question.model;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 问题关注表 实体类
 * @author sjw
 * @since 2021-03-01
 */
@TableName("qa_follow_info")
public class QaFollowInfo extends Model<QaFollowInfo> {

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
     * 关注人ID
     */
    @TableField("USER_ID")
    private String userId;

    /**
     * 关注时间
     */
    @TableField("FOLLOW_TIME")
    private Timestamp followTime;

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Timestamp getFollowTime() {
        return followTime;
    }

    public void setFollowTime(Timestamp followTime) {
        this.followTime = followTime;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
