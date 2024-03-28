package com.jxdinfo.doc.front.docsearch.model;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;

/**
 * Created by ZhongGuangrui on 2019/1/16.
 * 搜索历史记录 实体类
 */
@TableName("doc_history_search")
public class DocHistorySearch extends Model<DocHistorySearch> {
    /**
     * 序列化
     */
    private static final long serialVersionUID = 1L;
    /**
     * 历史记录表主键
     */
    @TableId("history_id")
    private String historyId;
    /**
     * 用户id
     */
    @TableField("user_id")
    private String userId;
    /**
     * 关键词
     */
    @TableField("keywords")
    private String keywords;
    /**
     * 搜索时间
     */
    @TableField("create_time")
    private String createTime;

    /**
     * 搜索时间
     */
    @TableField("valid_flag")
    private String validFlag;

    public String getValidFlag() {
        return validFlag;
    }

    public void setValidFlag(String validFlag) {
        this.validFlag = validFlag;
    }

    public String getHistoryId() {
        return historyId;
    }

    public void setHistoryId(String historyId) {
        this.historyId = historyId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    protected Serializable pkVal() {
        return this.historyId;
    }
}
