package com.jxdinfo.doc.manager.docmanager.model;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;
import java.sql.Timestamp;

@TableName("doc_es_log")
public class DocEsLog  extends Model<DocEsLog> {
    /**
     * 文档ID
     */
    @TableId("doc_id")
    private String docId;

    /**
     * 上传者ID
     */
    @TableField("title")
    private String title;


    /**
     * 创建时间
     */
    @TableField("create_time")
    private Timestamp createTime;


    /**
     * 上传者ID
     */
    @TableField("content")
    private String content;
    /**
     * 上传者ID
     */
    @TableField("content_type")
    private String contentType;

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    protected Serializable pkVal() {
        return docId;
    }
}
