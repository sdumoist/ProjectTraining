package com.jxdinfo.doc.question.model;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 问题分享 实体类
 * @author yjs
 * @since 2021-03-01
 */
@TableName("qa_share_info")
public class QaShareInfo   extends Model<QaShareInfo> {
    /**
     * 序列化
     */
    private static final long serialVersionUID = 1L;

    /**
     * 分享ID
     */
    @TableId("ID")
    private String id;

    /**
     * 问题ID
     */
    @TableField("QUE_ID")
    private String queId;

    /**
     * 分享人ID
     */
    @TableField("SHARE_USER_ID")
    private String shareUserId;

    /**
     * 分享人姓名
     */
    @TableField("SHARE_USER_NAME")
    private String shareUserName;

    /**
     * 分享时间
     */
    @TableField("SHARE_TIME")
    private Timestamp shareTime;

    /**
     * 分享链接
     */
    @TableField("SHARE_URL")
    private String shareUrl;

    /**
     * 失效时间
     */
    @TableField("INVALID_TIME")
    private Timestamp InvalidTime;


    /**
     * 浏览次数
     */
    @TableField("REDA_NUM")
    private Integer readNum;

    /**
     * 真实链接
     */
    @TableField("REAL_URL")
    private String realUrl;
    @Override
    protected Serializable pkVal() {
        return id;
    }

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

    public String getShareUserId() {
        return shareUserId;
    }

    public void setShareUserId(String shareUserId) {
        this.shareUserId = shareUserId;
    }

    public String getShareUserName() {
        return shareUserName;
    }

    public void setShareUserName(String shareUserName) {
        this.shareUserName = shareUserName;
    }

    public Timestamp getShareTime() {
        return shareTime;
    }

    public void setShareTime(Timestamp shareTime) {
        this.shareTime = shareTime;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public Timestamp getInvalidTime() {
        return InvalidTime;
    }

    public void setInvalidTime(Timestamp invalidTime) {
        InvalidTime = invalidTime;
    }

    public Integer getReadNum() {
        return readNum;
    }

    public void setReadNum(Integer readNum) {
        this.readNum = readNum;
    }

    public String getRealUrl() {
        return realUrl;
    }

    public void setRealUrl(String realUrl) {
        this.realUrl = realUrl;
    }
}
