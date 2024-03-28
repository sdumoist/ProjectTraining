package com.jxdinfo.doc.question.model;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.sql.Timestamp;

@TableName("qa_file")
public class QaFile extends Model<QaFile> {
    /**
     * 主键
     */
    @TableId("ID")
    private String id;

    /**
     * 附件类型
     */
    @TableField("TYPE")
    private String type;

    /**
     * 文件地址
     */
    @TableField("FILE")
    private String file;

    /**
     * PDF文件地址
     */
    @TableField("PDF_FILE")
    private String pdfFile;

    /**
     * 本地文件地址
     */
    @TableField("LOCATION")
    private String location;

    /**
     * 上传人ID
     */
    @TableField("UPLOAD_USER_ID")
    private String uploadUserId;

    /**
     * 上传人姓名
     */
    @TableField("UPLOAD_USER_NAME")
    private String uploadUserName;

    /**
     * 本地文件地址
     */
    @TableField("UPLOAD_TIME")
    private Timestamp uploadTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getPdfFile() {
        return pdfFile;
    }

    public void setPdfFile(String pdfFile) {
        this.pdfFile = pdfFile;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUploadUserId() {
        return uploadUserId;
    }

    public void setUploadUserId(String uploadUserId) {
        this.uploadUserId = uploadUserId;
    }

    public String getUploadUserName() {
        return uploadUserName;
    }

    public void setUploadUserName(String uploadUserName) {
        this.uploadUserName = uploadUserName;
    }

    public Timestamp getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Timestamp uploadTime) {
        this.uploadTime = uploadTime;
    }

    @Override
    protected Serializable pkVal() {
        return id;
    }
}
