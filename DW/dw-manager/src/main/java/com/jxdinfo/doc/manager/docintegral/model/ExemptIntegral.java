package com.jxdinfo.doc.manager.docintegral.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.sql.Timestamp;

@TableName("doc_free_file")
public class ExemptIntegral {

    /**
     * 主键
     */
    @TableId("file_id")
    private String fileId;

    /**
     * 文件Id
     */
    @TableField("doc_id")
    private String docId;

    /**
     * 文件名
     */
    @TableField("file_name")
    private String fileName;

    /**
     * 文件名
     */
    @TableField("file_type")
    private String fileType;

    /**
     * 文件名
     */
    @TableField("doc_name")
    private String docName;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private  Timestamp createTime;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }
}
