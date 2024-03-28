package com.jxdinfo.doc.manager.docmanager.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;
import java.sql.Timestamp;


/**
 * <p>
 * 文档上传记录
 * </p>
 *
 * @author 
 * @since 2018-07-16
 */
@TableName("doc_upload")
public class DocUpload extends Model<DocUpload> {

    private static final long serialVersionUID = 1L;

    @TableField("ID")
    private String id;

    @TableField("USER_ID")
    private String userId;

    @TableField("DOC_ID")
    private String docId;

    @TableField("UPLOAD_TIME")
    private Timestamp uploadTime;

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

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public Timestamp getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Timestamp uploadTime) {
        this.uploadTime = uploadTime;
    }

    /**
     * @Title: pkVal
     * @return 
     */
    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
