package com.jxdinfo.doc.front.personalmanager.model;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by ZhongGuangrui on 2019/1/2.
 * 文档版本控制 实体类
 */
@TableName("doc_version")
public class DocVersion extends Model<DocVersion> {
    /**
     * 序列化
     */
    private static final long serialVersionUID = 1L;
    /**
     * 版本表 主键
     */
    @TableId("version_id")
    private String versionId;
    /**
     * 版本关联字段（每组文件唯一）
     */
    @TableField("version_reference")
    private String versionReference;
    /**
     * 外键、文档的主键
     */
    @TableField("doc_id")
    private String docId;
    /**
     * 版本有效标识
     */
    @TableField("valid_flag")
    private String validFlag;
    /**
     * 版本生效时间（设为最新的时间）
     */
    @TableField("apply_time")
    private Timestamp applyTime;
    /**
     * 操作者id
     */
    @TableField("apply_user_id")
    private String applyUserId;
    /**
     * 版本号
     */
    @TableField("version_number")
    private int versionNumber;
    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public String getVersionReference() {
        return versionReference;
    }

    public void setVersionReference(String versionReference) {
        this.versionReference = versionReference;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getValidFlag() {
        return validFlag;
    }

    public void setValidFlag(String validFlag) {
        this.validFlag = validFlag;
    }

    public Timestamp getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(Timestamp applyTime) {
        this.applyTime = applyTime;
    }

    public String getApplyUserId() {
        return applyUserId;
    }

    public void setApplyUserId(String applyUserId) {
        this.applyUserId = applyUserId;
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(int versionNumber) {
        this.versionNumber = versionNumber;
    }

    @Override
    protected Serializable pkVal() {
        return this.versionId;
    }
}
