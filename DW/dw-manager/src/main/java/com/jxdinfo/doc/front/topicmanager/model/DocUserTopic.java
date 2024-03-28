package com.jxdinfo.doc.front.topicmanager.model;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;

/**
 * Created by ZhongGuangrui on 2019/1/22.
 * 用户定制专题 关联表
 */
@TableName("doc_user_topic")
public class DocUserTopic extends Model<DocUserTopic> {
    /**
     * 序列化
     */
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId("user_topic_id")
    private String userTopicId;
    /**
     * 用户id
     */
    @TableField("user_id")
    private String userId;

    /**
     * 专题id
     */
    @TableField("topic_id")
    private String topicId;
    /**
     * 专题展示顺序
     */
    @TableField("show_order")
    private Integer showOrder;

    public DocUserTopic() {
    }

    public DocUserTopic(String userId, String topicId, Integer showOrder) {
        this.userId = userId;
        this.topicId = topicId;
        this.showOrder = showOrder;
    }

    public String getUserTopicId() {
        return userTopicId;
    }

    public void setUserTopicId(String userTopicId) {
        this.userTopicId = userTopicId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public Integer getShowOrder() {
        return showOrder;
    }

    public void setShowOrder(Integer showOrder) {
        this.showOrder = showOrder;
    }

    @Override
    protected Serializable pkVal() {
        return this.userTopicId;
    }
}
